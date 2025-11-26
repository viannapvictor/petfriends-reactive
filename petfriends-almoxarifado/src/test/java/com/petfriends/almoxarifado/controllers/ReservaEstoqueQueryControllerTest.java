package com.petfriends.almoxarifado.controllers;

import com.petfriends.almoxarifado.readmodel.ReservaEstoqueView;
import com.petfriends.almoxarifado.services.ReservaEstoqueQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(ReservaEstoqueQueryController.class)
@DisplayName("Testes do ReservaEstoqueQueryController")
class ReservaEstoqueQueryControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ReservaEstoqueQueryService service;

    @Test
    @DisplayName("Deve obter reserva por ID com sucesso")
    void deveObterReservaPorId() {
        ReservaEstoqueView view = ReservaEstoqueView.builder()
                .id("RES-001")
                .pedidoId("PED-001")
                .status("CONFIRMADA")
                .build();

        when(service.obterPorId("RES-001"))
                .thenReturn(Mono.just(view));

        webTestClient.get()
                .uri("/almoxarifado/reservas/RES-001")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("RES-001")
                .jsonPath("$.pedidoId").isEqualTo("PED-001")
                .jsonPath("$.status").isEqualTo("CONFIRMADA");
    }

    @Test
    @DisplayName("Deve retornar 404 quando reserva não existe")
    void deveRetornar404QuandoReservaNaoExiste() {
        when(service.obterPorId(anyString()))
                .thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/almoxarifado/reservas/RES-999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Deve obter reserva por pedido ID com sucesso")
    void deveObterReservaPorPedidoId() {
        ReservaEstoqueView view = ReservaEstoqueView.builder()
                .id("RES-001")
                .pedidoId("PED-001")
                .status("SEPARADA")
                .build();

        when(service.obterPorPedidoId("PED-001"))
                .thenReturn(Mono.just(view));

        webTestClient.get()
                .uri("/almoxarifado/reservas/pedido/PED-001")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("RES-001")
                .jsonPath("$.pedidoId").isEqualTo("PED-001")
                .jsonPath("$.status").isEqualTo("SEPARADA");
    }

    @Test
    @DisplayName("Deve retornar 404 quando pedido não tem reserva")
    void deveRetornar404QuandoPedidoNaoTemReserva() {
        when(service.obterPorPedidoId(anyString()))
                .thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/almoxarifado/reservas/pedido/PED-999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Deve tratar erro ao buscar por ID")
    void deveTratarErroAoBuscarPorId() {
        when(service.obterPorId(anyString()))
                .thenReturn(Mono.error(new RuntimeException("Erro no banco")));

        webTestClient.get()
                .uri("/almoxarifado/reservas/RES-001")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @DisplayName("Deve tratar erro ao buscar por pedido ID")
    void deveTratarErroAoBuscarPorPedidoId() {
        when(service.obterPorPedidoId(anyString()))
                .thenReturn(Mono.error(new RuntimeException("Erro no banco")));

        webTestClient.get()
                .uri("/almoxarifado/reservas/pedido/PED-001")
                .exchange()
                .expectStatus().is5xxServerError();
    }
}

