package com.petfriends.almoxarifado.controllers;

import com.petfriends.almoxarifado.services.ReservaEstoqueCommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@WebFluxTest(ReservaEstoqueCommandController.class)
@DisplayName("Testes do ReservaEstoqueCommandController")
class ReservaEstoqueCommandControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ReservaEstoqueCommandService service;

    private String requestBody;

    @BeforeEach
    void setUp() {
        requestBody = """
            {
                "pedidoId": "PED-001",
                "endereco": {
                    "rua": "Rua A",
                    "numero": "100",
                    "complemento": "Apt 10",
                    "bairro": "Centro",
                    "cidade": "São Paulo",
                    "estado": "SP",
                    "cep": "01000-000"
                },
                "itens": [
                    {"produtoId": "PROD-001", "quantidade": 10},
                    {"produtoId": "PROD-002", "quantidade": 5}
                ]
            }
            """;
    }

    @Test
    @DisplayName("Deve reservar estoque com sucesso")
    void deveReservarEstoqueComSucesso() {
        // Arrange
        when(service.reservarEstoque(anyString(), any(), anyList()))
                .thenReturn(Mono.just("RES-001"));

        // Act & Assert
        webTestClient.post()
                .uri("/almoxarifado/reservas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.reservaId").isEqualTo("RES-001")
                .jsonPath("$.message").isEqualTo("Estoque reservado com sucesso");

        verify(service).reservarEstoque(eq("PED-001"), any(), anyList());
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar reservar estoque com dados inválidos")
    void deveRetornarErroAoReservarEstoqueComDadosInvalidos() {
        // Arrange
        when(service.reservarEstoque(anyString(), any(), anyList()))
                .thenReturn(Mono.error(new IllegalArgumentException("Dados inválidos")));

        // Act & Assert
        webTestClient.post()
                .uri("/almoxarifado/reservas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Dados inválidos");

        verify(service).reservarEstoque(eq("PED-001"), any(), anyList());
    }

    @Test
    @DisplayName("Deve confirmar reserva com sucesso")
    void deveConfirmarReservaComSucesso() {
        // Arrange
        when(service.confirmarReserva("RES-001"))
                .thenReturn(Mono.just("RES-001"));

        // Act & Assert
        webTestClient.put()
                .uri("/almoxarifado/reservas/RES-001/confirmar")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.reservaId").isEqualTo("RES-001")
                .jsonPath("$.message").isEqualTo("Reserva confirmada com sucesso");

        verify(service).confirmarReserva("RES-001");
    }

    @Test
    @DisplayName("Deve retornar erro ao confirmar reserva inexistente")
    void deveRetornarErroAoConfirmarReservaInexistente() {
        // Arrange
        when(service.confirmarReserva("RES-999"))
                .thenReturn(Mono.error(new IllegalArgumentException("Reserva não encontrada")));

        // Act & Assert
        webTestClient.put()
                .uri("/almoxarifado/reservas/RES-999/confirmar")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Reserva não encontrada");

        verify(service).confirmarReserva("RES-999");
    }

    @Test
    @DisplayName("Deve cancelar reserva com sucesso")
    void deveCancelarReservaComSucesso() {
        // Arrange
        String cancelarRequest = """
            {
                "id": "RES-001",
                "motivo": "Cliente desistiu"
            }
            """;

        when(service.cancelarReserva("RES-001", "Cliente desistiu"))
                .thenReturn(Mono.just("RES-001"));

        // Act & Assert
        webTestClient.put()
                .uri("/almoxarifado/reservas/cancelar")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cancelarRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.reservaId").isEqualTo("RES-001")
                .jsonPath("$.message").isEqualTo("Reserva cancelada com sucesso");

        verify(service).cancelarReserva("RES-001", "Cliente desistiu");
    }

    @Test
    @DisplayName("Deve retornar erro ao cancelar reserva com motivo inválido")
    void deveRetornarErroAoCancelarReservaComMotivoInvalido() {
        // Arrange
        String cancelarRequest = """
            {
                "id": "RES-001",
                "motivo": ""
            }
            """;

        when(service.cancelarReserva("RES-001", ""))
                .thenReturn(Mono.error(new IllegalArgumentException("Motivo não pode ser vazio")));

        // Act & Assert
        webTestClient.put()
                .uri("/almoxarifado/reservas/cancelar")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cancelarRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Motivo não pode ser vazio");

        verify(service).cancelarReserva("RES-001", "");
    }

    @Test
    @DisplayName("Deve separar itens com sucesso")
    void deveSepararItensComSucesso() {
        // Arrange
        String separarRequest = """
            {
                "id": "RES-001",
                "operadorId": "OP-001"
            }
            """;

        when(service.separarItens("RES-001", "OP-001"))
                .thenReturn(Mono.just("RES-001"));

        // Act & Assert
        webTestClient.put()
                .uri("/almoxarifado/reservas/separar")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(separarRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.reservaId").isEqualTo("RES-001")
                .jsonPath("$.message").isEqualTo("Itens separados com sucesso");

        verify(service).separarItens("RES-001", "OP-001");
    }

    @Test
    @DisplayName("Deve retornar erro ao separar itens de reserva não confirmada")
    void deveRetornarErroAoSepararItensDeReservaNaoConfirmada() {
        // Arrange
        String separarRequest = """
            {
                "id": "RES-001",
                "operadorId": "OP-001"
            }
            """;

        when(service.separarItens("RES-001", "OP-001"))
                .thenReturn(Mono.error(new IllegalStateException("Reserva não está confirmada")));

        // Act & Assert
        webTestClient.put()
                .uri("/almoxarifado/reservas/separar")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(separarRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Reserva não está confirmada");

        verify(service).separarItens("RES-001", "OP-001");
    }

    @Test
    @DisplayName("Deve tratar erro genérico ao reservar estoque")
    void deveTratarErroGenericoAoReservarEstoque() {
        // Arrange
        when(service.reservarEstoque(anyString(), any(), anyList()))
                .thenReturn(Mono.error(new RuntimeException("Erro inesperado")));

        // Act & Assert
        webTestClient.post()
                .uri("/almoxarifado/reservas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Erro inesperado");

        verify(service).reservarEstoque(eq("PED-001"), any(), anyList());
    }

    @Test
    @DisplayName("Deve tratar erro genérico ao confirmar reserva")
    void deveTratarErroGenericoAoConfirmarReserva() {
        // Arrange
        when(service.confirmarReserva("RES-001"))
                .thenReturn(Mono.error(new RuntimeException("Erro no banco de dados")));

        // Act & Assert
        webTestClient.put()
                .uri("/almoxarifado/reservas/RES-001/confirmar")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Erro no banco de dados");

        verify(service).confirmarReserva("RES-001");
    }

    @Test
    @DisplayName("Deve processar request com lista de itens vazia")
    void deveProcessarRequestComListaDeItensVazia() {
        String requestVazio = """
            {
                "pedidoId": "PED-001",
                "endereco": {
                    "rua": "Rua A",
                    "numero": "100",
                    "bairro": "Centro",
                    "cidade": "São Paulo",
                    "estado": "SP",
                    "cep": "01000-000"
                },
                "itens": []
            }
            """;

        when(service.reservarEstoque(anyString(), any(), anyList()))
                .thenReturn(Mono.error(new IllegalArgumentException("Lista de itens não pode ser vazia")));

        webTestClient.post()
                .uri("/almoxarifado/reservas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestVazio)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Deve processar request com quantidade negativa")
    void deveProcessarRequestComQuantidadeNegativa() {
        String requestNegativo = """
            {
                "pedidoId": "PED-001",
                "endereco": {
                    "rua": "Rua A",
                    "numero": "100",
                    "bairro": "Centro",
                    "cidade": "São Paulo",
                    "estado": "SP",
                    "cep": "01000-000"
                },
                "itens": [
                    {"produtoId": "PROD-001", "quantidade": -5}
                ]
            }
            """;

        when(service.reservarEstoque(anyString(), any(), anyList()))
                .thenReturn(Mono.error(new IllegalArgumentException("Quantidade deve ser positiva")));

        webTestClient.post()
                .uri("/almoxarifado/reservas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestNegativo)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Deve processar request com CEP inválido")
    void deveProcessarRequestComCepInvalido() {
        String requestCepInvalido = """
            {
                "pedidoId": "PED-001",
                "endereco": {
                    "rua": "Rua A",
                    "numero": "100",
                    "bairro": "Centro",
                    "cidade": "São Paulo",
                    "estado": "SP",
                    "cep": "123"
                },
                "itens": [
                    {"produtoId": "PROD-001", "quantidade": 5}
                ]
            }
            """;

        when(service.reservarEstoque(anyString(), any(), anyList()))
                .thenReturn(Mono.error(new IllegalArgumentException("CEP inválido")));

        webTestClient.post()
                .uri("/almoxarifado/reservas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestCepInvalido)
                .exchange()
                .expectStatus().isBadRequest();
    }
}