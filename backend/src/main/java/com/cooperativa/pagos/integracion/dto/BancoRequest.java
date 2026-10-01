package com.cooperativa.pagos.integracion.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BancoRequest(UUID transactionId, BigDecimal amount, String currency) {}
