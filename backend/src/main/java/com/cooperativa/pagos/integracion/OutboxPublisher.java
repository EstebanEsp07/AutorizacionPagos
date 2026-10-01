package com.cooperativa.pagos.integracion;

import com.cooperativa.pagos.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxPublisher {
    private final OutboxRepository repo;
    private final RabbitTemplate rabbit;

    public OutboxPublisher(OutboxRepository repo, RabbitTemplate rabbit) {
        this.repo=repo; this.rabbit=rabbit;
    }

    @Scheduled(fixedDelay=1000)
    @Transactional
    public void publish() {
        for (var event: repo.findTop100ByPublishedFalseOrderByCreatedAtAsc()) {
            rabbit.convertAndSend(RabbitMQConfig.EXCHANGE, event.getEventType(), event.getPayload());
            event.markPublished();
            repo.save(event);
        }
    }
}
