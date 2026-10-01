package com.cooperativa.pagos.autorizacion.controller;

import com.cooperativa.pagos.autorizacion.dto.*;
import com.cooperativa.pagos.autorizacion.service.PagoService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pagos")
public class PagoController {
    private final PagoService service;
    public PagoController(PagoService service){this.service=service;}

    @PostMapping("/autorizar")
    public ResponseEntity<PagoResponse> autorizar(
        @RequestHeader("Idempotency-Key") String key,
        @RequestHeader(value="X-Forwarded-For", required=false) String ip,
        @Valid @RequestBody PagoRequest request) {
        var response=service.autorizar(key, request, ip == null ? "unknown" : ip);
        return ResponseEntity.status(response.estado().name().contains("UNKNOWN") ? 202 : 
            response.estado().name().equals("REJECTED") ? 422 : 200).body(response);
    }
}
