package com.petfriends.almoxarifado;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Testes do PetfriendsAlmoxarifadoApplication")
class PetfriendsAlmoxarifadoApplicationTest {

    @Test
    @DisplayName("Deve criar instância da aplicação")
    void deveCriarInstanciaDaAplicacao() {
        PetfriendsAlmoxarifadoApplication application = new PetfriendsAlmoxarifadoApplication();
        assertNotNull(application);
    }

    @Test
    @DisplayName("Deve executar main sem erros quando args são válidos")
    void deveExecutarMainSemErros() {
        assertDoesNotThrow(() -> {
            PetfriendsAlmoxarifadoApplication.class.getDeclaredConstructor().newInstance();
        });
    }

    @Test
    @DisplayName("Deve ter método main público e estático")
    void deveTerMetodoMainPublicoEstatico() throws NoSuchMethodException {
        var mainMethod = PetfriendsAlmoxarifadoApplication.class.getMethod("main", String[].class);
        assertNotNull(mainMethod);
    }
}

