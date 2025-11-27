package com.petfriends.transporte.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do WebClientConfig")
class WebClientConfigTest {

    @Test
    @DisplayName("Deve criar WebClient com URL do Almoxarifado")
    void deveCriarWebClientComUrlDoAlmoxarifado() {
        WebClientConfig config = new WebClientConfig();
        ReflectionTestUtils.setField(config, "almoxarifadoServiceUrl", "http://localhost:8082");

        WebClient.Builder builder = WebClient.builder();
        WebClient webClient = config.webClient(builder);

        assertNotNull(webClient);
    }

    @Test
    @DisplayName("Deve criar WebClient com URL customizada")
    void deveCriarWebClientComUrlCustomizada() {
        WebClientConfig config = new WebClientConfig();
        ReflectionTestUtils.setField(config, "almoxarifadoServiceUrl", "http://custom-url:9000");

        WebClient.Builder builder = WebClient.builder();
        WebClient webClient = config.webClient(builder);

        assertNotNull(webClient);
    }

    @Test
    @DisplayName("Deve usar URL padrão quando não especificada")
    void deveUsarUrlPadraoQuandoNaoEspecificada() {
        WebClientConfig config = new WebClientConfig();

        WebClient.Builder builder = WebClient.builder();
        WebClient webClient = config.webClient(builder);

        assertNotNull(webClient);
    }
}

