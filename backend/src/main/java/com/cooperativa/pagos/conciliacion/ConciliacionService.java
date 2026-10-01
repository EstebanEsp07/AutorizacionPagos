package com.cooperativa.pagos.conciliacion;

import com.cooperativa.pagos.autorizacion.domain.EstadoPago;
import com.cooperativa.pagos.autorizacion.domain.Pago;
import com.cooperativa.pagos.autorizacion.repository.PagoRepository;
import com.cooperativa.pagos.integracion.BancoExternoClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConciliacionService {
    private final PagoRepository pagos;
    private final BancoExternoClient banco;

    public ConciliacionService(PagoRepository pagos, BancoExternoClient banco) {
        this.pagos=pagos; this.banco=banco;
    }

    @Transactional
    public void conciliar(Pago pago) {
        var status=banco.consultar(pago.getId());
        if(status == null) return;
        // En producción: aplicar una máquina de estados y reglas de reverso/liberación de fondos.
        if("APPROVED".equalsIgnoreCase(status.status())) {
            pago.aprobar(status.code(), status.reference());
        }
        pagos.save(pago);
    }
}
