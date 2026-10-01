package com.cooperativa.pagos.autorizacion.dto;

import com.cooperativa.pagos.autorizacion.domain.EstadoPago;
import java.util.UUID;

public record PagoResponse(UUID transactionId, EstadoPago estado, String mensaje, String reference) {}
