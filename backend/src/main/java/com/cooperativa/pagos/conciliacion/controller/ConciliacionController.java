package com.cooperativa.pagos.conciliacion.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/conciliacion")
public class ConciliacionController {
    @PostMapping("/health")
    public String health(){ return "OK"; }
}
