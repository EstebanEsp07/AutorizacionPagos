package com.cooperativa.pagos.conciliacion;

import com.cooperativa.pagos.autorizacion.repository.PagoRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class ConciliacionListener {
    private final PagoRepository pagos;
    private final ConciliacionService service;

    public ConciliacionListener(PagoRepository pagos, ConciliacionService service) {
        this.pagos=pagos; this.service=service;
    }

    @RabbitListener(queues="payment.reconciliation")
    public void receive(String paymentId) {
        pagos.findById(UUID.fromString(paymentId)).ifPresent(service::conciliar);
    }
}
