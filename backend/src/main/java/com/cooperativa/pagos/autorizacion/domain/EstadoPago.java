package com.cooperativa.pagos.autorizacion.domain;

public enum EstadoPago {
    PENDING_EXTERNAL,
    APPROVED,
    REJECTED,
    TIMEOUT,
    UNKNOWN_STATUS,
    PENDING_RECONCILIATION
}
