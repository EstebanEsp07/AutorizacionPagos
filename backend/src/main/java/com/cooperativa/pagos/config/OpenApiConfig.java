package com.cooperativa.pagos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI pagosOpenAPI() {
        return new OpenAPI().info(new Info()
            .title("API de Autorización de Pagos")
            .version("1.0.0")
            .description("Autorización idempotente, auditoría y conciliación."));
    }
}
