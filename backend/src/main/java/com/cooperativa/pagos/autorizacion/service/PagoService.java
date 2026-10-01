package com.cooperativa.pagos.autorizacion.service;

import com.cooperativa.pagos.autorizacion.domain.*;
import com.cooperativa.pagos.autorizacion.dto.*;
import com.cooperativa.pagos.autorizacion.repository.*;
import com.cooperativa.pagos.auditoria.*;
import com.cooperativa.pagos.auditoria.domain.RegistroAuditoria;
import com.cooperativa.pagos.integracion.*;
import com.cooperativa.pagos.integracion.dto.BancoResponse;
import com.cooperativa.pagos.metricas.MetricasService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PagoService {
    private final PagoRepository pagos;
    private final CuentaRepository cuentas;
    private final AuditoriaRepository auditoria;
    private final OutboxRepository outbox;
    private final BancoExternoClient banco;
    private final MetricasService metricas;

    public PagoService(PagoRepository pagos, CuentaRepository cuentas, AuditoriaRepository auditoria,
                       OutboxRepository outbox, BancoExternoClient banco, MetricasService metricas) {
        this.pagos=pagos; this.cuentas=cuentas; this.auditoria=auditoria; this.outbox=outbox;
        this.banco=banco; this.metricas=metricas;
    }

    @Transactional
    public PagoResponse autorizar(String key, PagoRequest req, String ip) {
        long startedAt = System.nanoTime();

        var existente=pagos.findByIdempotencyKey(key);
        if(existente.isPresent()){
            metricas.duplicate();
            var p=existente.get();
            metricas.recordLatency(System.nanoTime() - startedAt);
            return new PagoResponse(p.getId(), p.getEstado(), p.getRespuestaCliente(), p.getBancoReference());
        }

        var cuenta=cuentas.findByNumero(req.cuenta()).orElseThrow(() -> new IllegalArgumentException("Cuenta no existe"));
        if(!cuenta.isActiva()) throw new IllegalStateException("Cuenta inactiva");
        if(req.monto().compareTo(cuenta.getSaldo())>0) throw new IllegalArgumentException("Saldo insuficiente");
        if(req.monto().compareTo(cuenta.getLimiteDiario())>0 ||
           cuenta.getAcumuladoDiario().add(req.monto()).compareTo(cuenta.getLimiteDiario())>0) {
            metricas.error("REJECTED_LIMIT");
            throw new LimiteException("Límite operativo excedido");
        }

        Pago pago=Pago.nuevo(key, cuenta.getId(), req.monto(), req.moneda());
        cuenta.reservar(req.monto());
        pagos.save(pago);
        cuentas.save(cuenta);

        RegistroAuditoria a=RegistroAuditoria.crear(pago.getId(), key, cuenta.getId(), req.monto(), req.moneda(),
            pago.getEstado().name(), req.toString(), ip);
        auditoria.save(a);

        try {
            BancoResponse r=banco.autorizar(pago.getId(), req.monto(), req.moneda());
            if(r != null && "APPROVED".equalsIgnoreCase(r.status())) {
                pago.aprobar(r.code(), r.reference());
            } else {
                pago.rechazar(r == null ? "EMPTY_RESPONSE" : r.code());
                metricas.error("REJECTED_BANK");
            }
            pagos.save(pago);
            a.finalizar(pago.getEstado().name(), pago.getRespuestaCliente(), pago.getCodigoBanco(),
                (System.nanoTime() - startedAt) / 1_000_000);
            auditoria.save(a);
            outbox.save(OutboxEvent.of(pago.getId(), "payment."+pago.getEstado().name().toLowerCase(), pago.getId().toString()));
            return new PagoResponse(pago.getId(), pago.getEstado(), pago.getRespuestaCliente(), pago.getBancoReference());
        } catch (Exception ex) {
            pago.timeout();
            pagos.save(pago);
            metricas.error("TIMEOUT_BANK");
            a.finalizar(pago.getEstado().name(), pago.getRespuestaCliente(), "TIMEOUT", (System.nanoTime() - startedAt) / 1_000_000);
            auditoria.save(a);
            outbox.save(OutboxEvent.of(pago.getId(), "payment.unknown", pago.getId().toString()));
            return new PagoResponse(pago.getId(), pago.getEstado(), pago.getRespuestaCliente(), null);
        }
    }


    public static class LimiteException extends RuntimeException {
        public LimiteException(String message){super(message);}
    }
}
