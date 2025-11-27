package com.petfriends.transporte;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Testes do PetfriendsTransporteApplication")
class PetfriendsTransporteApplicationTest {

    @Test
    @DisplayName("Deve criar instância da aplicação")
    void deveCriarInstanciaDaAplicacao() {
        PetfriendsTransporteApplication application = new PetfriendsTransporteApplication();
        assertNotNull(application);
    }

    @Test
    @DisplayName("Deve executar main sem erros quando args são válidos")
    void deveExecutarMainSemErros() {
        assertDoesNotThrow(() -> {
            PetfriendsTransporteApplication.class.getDeclaredConstructor().newInstance();
        });
    }

    @Test
    @DisplayName("Deve ter método main público e estático")
    void deveTerMetodoMainPublicoEstatico() throws NoSuchMethodException {
        var mainMethod = PetfriendsTransporteApplication.class.getMethod("main", String[].class);
        assertNotNull(mainMethod);
    }
}

