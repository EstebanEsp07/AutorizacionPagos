package com.cooperativa.pagos.integracion;

import com.cooperativa.pagos.integracion.dto.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.UUID;
import java.math.BigDecimal;

@Component
public class BancoExternoClient {
    private final RestClient client;

    public BancoExternoClient(@Value("${bank.base-url}") String baseUrl,
                               @Value("${bank.timeout-ms:4000}") int timeoutMs) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeoutMs);
        factory.setReadTimeout(timeoutMs);
        this.client = RestClient.builder()
            .baseUrl(baseUrl)
            .requestFactory(factory)
            .build();
    }

    @CircuitBreaker(name="bancoExterno")
    public BancoResponse autorizar(UUID transactionId, BigDecimal amount, String currency) {
        return client.post().uri("/bank/authorize")
            .body(new BancoRequest(transactionId, amount, currency))
            .retrieve().body(BancoResponse.class);
    }

    public BancoResponse consultar(UUID transactionId) {
        return client.get().uri("/bank/status/{id}", transactionId)
            .retrieve().body(BancoResponse.class);
    }
}
