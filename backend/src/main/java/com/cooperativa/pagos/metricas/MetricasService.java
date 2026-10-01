package com.cooperativa.pagos.metricas;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class MetricasService {
    private final Timer authorizationLatency;
    private final Counter duplicatePayments;
    private final Counter errors;
    private final Timer reconciliationTime;

    public MetricasService(MeterRegistry registry) {
        authorizationLatency = Timer.builder("payment.authorization.latency")
            .description("Latencia del flujo síncrono de autorización").publishPercentiles(0.95,0.99).register(registry);
        duplicatePayments = Counter.builder("payment.duplicates.total")
            .description("Solicitudes rechazadas/reutilizadas por idempotencia").register(registry);
        errors = Counter.builder("payment.errors.total")
            .description("Errores de pagos").tag("type","unknown").register(registry);
        reconciliationTime = Timer.builder("payment.reconciliation.duration")
            .description("Duración de conciliación").register(registry);
    }
    public Timer.Sample start(){return Timer.start();}
    public void recordLatency(long nanos){authorizationLatency.record(nanos, TimeUnit.NANOSECONDS);}
    public void duplicate(){duplicatePayments.increment();}
    public void error(String type){errors.increment();}
    public Timer reconciliation(){return reconciliationTime;}
}
