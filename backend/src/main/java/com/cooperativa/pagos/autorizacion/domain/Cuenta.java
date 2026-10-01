package com.cooperativa.pagos.autorizacion.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "cuentas")
public class Cuenta {
    @Id
    private UUID id;
    @Column(nullable=false, unique=true)
    private String numero;
    @Column(nullable=false)
    private BigDecimal saldo;
    @Column(name="limite_diario", nullable=false)
    private BigDecimal limiteDiario;
    @Column(name="acumulado_diario", nullable=false)
    private BigDecimal acumuladoDiario;
    @Column(nullable=false)
    private boolean activa;
    @Version
    private long version;

    public UUID getId() { return id; }
    public String getNumero() { return numero; }
    public BigDecimal getSaldo() { return saldo; }
    public BigDecimal getLimiteDiario() { return limiteDiario; }
    public BigDecimal getAcumuladoDiario() { return acumuladoDiario; }
    public boolean isActiva() { return activa; }

    public void reservar(BigDecimal monto) {
        this.saldo = this.saldo.subtract(monto);
        this.acumuladoDiario = this.acumuladoDiario.add(monto);
    }
}
