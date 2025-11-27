package com.petfriends.almoxarifado.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Testes do TracingConfig")
class TracingConfigTest {

    @Test
    @DisplayName("Deve criar configuração de tracing")
    void deveCriarConfiguracaoDeTracing() {
        TracingConfig config = new TracingConfig();

        assertNotNull(config);
    }

    @Test
    @DisplayName("Deve inicializar propagação de contexto sem erros")
    void deveInicializarPropagacaoDeContextoSemErros() {
        TracingConfig config = new TracingConfig();

        assertDoesNotThrow(() -> config.init());
    }

    @Test
    @DisplayName("Deve permitir múltiplas inicializações")
    void devePermitirMultiplasInicializacoes() {
        TracingConfig config = new TracingConfig();

        assertDoesNotThrow(() -> {
            config.init();
            config.init();
        });
    }
}

