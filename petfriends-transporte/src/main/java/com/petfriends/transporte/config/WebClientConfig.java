package com.petfriends.transporte.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${almoxarifado.service.url:http://localhost:8082}")
    private String almoxarifadoServiceUrl;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .baseUrl(almoxarifadoServiceUrl)
                .build();
    }
}

