package com.petfriends.almoxarifado.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do OpenApiConfig")
class OpenApiConfigTest {

    @Test
    @DisplayName("Deve criar configuração OpenAPI")
    void deveCriarConfiguracaoOpenAPI() {
        OpenApiConfig config = new OpenApiConfig();

        OpenAPI openAPI = config.almoxarifadoOpenAPI();

        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertTrue(openAPI.getInfo().getTitle().contains("Almoxarifado"));
        assertNotNull(openAPI.getInfo().getVersion());
        assertNotNull(openAPI.getInfo().getDescription());
    }

    @Test
    @DisplayName("Deve incluir informações de contato na configuração")
    void deveIncluirInformacoesDeContatoNaConfiguracao() {
        OpenApiConfig config = new OpenApiConfig();

        OpenAPI openAPI = config.almoxarifadoOpenAPI();

        assertNotNull(openAPI.getInfo().getContact());
        assertEquals("Equipe PetFriends", openAPI.getInfo().getContact().getName());
    }
}

