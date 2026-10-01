package com.cooperativa.pagos.autorizacion.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record PagoRequest(
    @NotBlank String cuenta,
    @NotNull @DecimalMin("0.01") BigDecimal monto,
    @NotBlank @Pattern(regexp="[A-Z]{3}") String moneda
) {}
