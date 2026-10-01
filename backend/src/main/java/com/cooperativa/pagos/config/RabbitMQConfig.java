package com.cooperativa.pagos.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE = "payments.events";
    public static final String NOTIFICATIONS = "payment.notifications";
    public static final String RECONCILIATION = "payment.reconciliation";

    @Bean
    TopicExchange paymentsExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    Queue notificationsQueue() {
        return QueueBuilder.durable(NOTIFICATIONS).build();
    }

    @Bean
    Queue reconciliationQueue() {
        return QueueBuilder.durable(RECONCILIATION).build();
    }

    @Bean
    Binding notificationsBinding() {
        return BindingBuilder.bind(notificationsQueue()).to(paymentsExchange()).with("payment.*");
    }

    @Bean
    Binding reconciliationBinding() {
        return BindingBuilder.bind(reconciliationQueue()).to(paymentsExchange()).with("payment.unknown");
    }
}
