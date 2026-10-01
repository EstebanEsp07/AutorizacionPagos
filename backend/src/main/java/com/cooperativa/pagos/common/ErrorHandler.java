package com.cooperativa.pagos.common;

import com.cooperativa.pagos.autorizacion.service.PagoService.LimiteException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({IllegalArgumentException.class, LimiteException.class})
    ResponseEntity<?> badRequest(RuntimeException e) {
        return ResponseEntity.unprocessableEntity().body(Map.of(
            "timestamp", OffsetDateTime.now().toString(),
            "error", e.getMessage()
        ));
    }
}
