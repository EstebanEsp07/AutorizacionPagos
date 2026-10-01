package com.cooperativa.pagos.auditoria.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name="auditoria")
public class RegistroAuditoria {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private UUID pagoId;
    private String idempotencyKey;
    private UUID cuentaId;
    private BigDecimal monto;
    private String moneda;
    private String estado;
    @Column(columnDefinition="text") private String requestPayload;
    @Column(columnDefinition="text") private String responsePayload;
    private String codigoRespuestaBanco;
    private Long latenciaMs;
    private String ipOrigen;
    private OffsetDateTime timestampInicio;
    private OffsetDateTime timestampFin;

    public static RegistroAuditoria crear(UUID pagoId, String key, UUID cuentaId, BigDecimal monto, String moneda,
                                          String estado, String request, String ip) {
        RegistroAuditoria a = new RegistroAuditoria();
        a.pagoId=pagoId; a.idempotencyKey=key; a.cuentaId=cuentaId; a.monto=monto; a.moneda=moneda;
        a.estado=estado; a.requestPayload=request; a.ipOrigen=ip; a.timestampInicio=OffsetDateTime.now();
        return a;
    }
    public void finalizar(String estado, String response, String bankCode, long latency) {
        this.estado=estado; this.responsePayload=response; this.codigoRespuestaBanco=bankCode;
        this.latenciaMs=latency; this.timestampFin=OffsetDateTime.now();
    }
}
