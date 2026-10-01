package com.cooperativa.pagos.autorizacion.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name="pagos")
public class Pago {
    @Id
    private UUID id;
    @Column(name="idempotency_key", nullable=false, unique=true)
    private String idempotencyKey;
    @Column(name="cuenta_id", nullable=false)
    private UUID cuentaId;
    @Column(nullable=false)
    private BigDecimal monto;
    @Column(nullable=false)
    private String moneda;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private EstadoPago estado;
    private Integer respuestaHttp;
    @Column(columnDefinition="text")
    private String respuestaCliente;
    private String codigoBanco;
    private String bancoReference;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public UUID getId() { return id; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public UUID getCuentaId() { return cuentaId; }
    public BigDecimal getMonto() { return monto; }
    public String getMoneda() { return moneda; }
    public EstadoPago getEstado() { return estado; }
    public Integer getRespuestaHttp() { return respuestaHttp; }
    public String getRespuestaCliente() { return respuestaCliente; }
    public String getCodigoBanco() { return codigoBanco; }
    public String getBancoReference() { return bancoReference; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    public static Pago nuevo(String key, UUID cuentaId, BigDecimal monto, String moneda) {
        Pago p = new Pago();
        p.id = UUID.randomUUID();
        p.idempotencyKey = key;
        p.cuentaId = cuentaId;
        p.monto = monto;
        p.moneda = moneda;
        p.estado = EstadoPago.PENDING_EXTERNAL;
        p.createdAt = OffsetDateTime.now();
        p.updatedAt = p.createdAt;
        return p;
    }

    public void aprobar(String code, String reference) {
        this.estado = EstadoPago.APPROVED; this.codigoBanco = code; this.bancoReference = reference; this.respuestaHttp = 200; this.respuestaCliente = "Pago aprobado"; this.updatedAt=OffsetDateTime.now();
    }
    public void rechazar(String code) {
        this.estado = EstadoPago.REJECTED; this.codigoBanco = code; this.respuestaHttp = 422; this.respuestaCliente = "Pago rechazado por el banco"; this.updatedAt=OffsetDateTime.now();
    }
    public void timeout() {
        this.estado = EstadoPago.UNKNOWN_STATUS; this.respuestaHttp = 202; this.respuestaCliente = "Pago en proceso de confirmación"; this.updatedAt=OffsetDateTime.now();
    }
}
