package com.petfriends.transporte.controllers;

import com.petfriends.transporte.readmodel.EntregaView;
import com.petfriends.transporte.services.EntregaQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(EntregaQueryController.class)
@DisplayName("Testes do EntregaQueryController")
class EntregaQueryControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private EntregaQueryService service;

    @Test
    @DisplayName("Deve obter entrega por ID com sucesso")
    void deveObterEntregaPorId() {
        EntregaView view = EntregaView.builder()
                .id("ENT-001")
                .pedidoId("PED-001")
                .status("EM_TRANSITO")
                .build();

        when(service.obterPorId("ENT-001"))
                .thenReturn(Mono.just(view));

        webTestClient.get()
                .uri("/transporte/entregas/ENT-001")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("ENT-001")
                .jsonPath("$.pedidoId").isEqualTo("PED-001")
                .jsonPath("$.status").isEqualTo("EM_TRANSITO");
    }

    @Test
    @DisplayName("Deve retornar 404 quando entrega não existe")
    void deveRetornar404QuandoEntregaNaoExiste() {
        when(service.obterPorId(anyString()))
                .thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/transporte/entregas/ENT-999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Deve obter entrega por pedido ID com sucesso")
    void deveObterEntregaPorPedidoId() {
        EntregaView view = EntregaView.builder()
                .id("ENT-001")
                .pedidoId("PED-001")
                .status("CONCLUIDA")
                .build();

        when(service.obterPorPedidoId("PED-001"))
                .thenReturn(Mono.just(view));

        webTestClient.get()
                .uri("/transporte/entregas/pedido/PED-001")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("ENT-001")
                .jsonPath("$.pedidoId").isEqualTo("PED-001")
                .jsonPath("$.status").isEqualTo("CONCLUIDA");
    }

    @Test
    @DisplayName("Deve retornar 404 quando pedido não tem entrega")
    void deveRetornar404QuandoPedidoNaoTemEntrega() {
        when(service.obterPorPedidoId(anyString()))
                .thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/transporte/entregas/pedido/PED-999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Deve obter entrega por reserva ID com sucesso")
    void deveObterEntregaPorReservaId() {
        EntregaView view = EntregaView.builder()
                .id("ENT-001")
                .reservaId("RES-001")
                .status("AGENDADA")
                .build();

        when(service.obterPorReservaId("RES-001"))
                .thenReturn(Mono.just(view));

        webTestClient.get()
                .uri("/transporte/entregas/reserva/RES-001")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("ENT-001")
                .jsonPath("$.reservaId").isEqualTo("RES-001")
                .jsonPath("$.status").isEqualTo("AGENDADA");
    }

    @Test
    @DisplayName("Deve retornar 404 quando reserva não tem entrega")
    void deveRetornar404QuandoReservaNaoTemEntrega() {
        when(service.obterPorReservaId(anyString()))
                .thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/transporte/entregas/reserva/RES-999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Deve listar entregas por motorista")
    void deveListarEntregasPorMotorista() {
        EntregaView view1 = EntregaView.builder()
                .id("ENT-001")
                .motoristaId("MOT-001")
                .status("EM_TRANSITO")
                .build();
        
        EntregaView view2 = EntregaView.builder()
                .id("ENT-002")
                .motoristaId("MOT-001")
                .status("CONCLUIDA")
                .build();

        when(service.listarPorMotorista("MOT-001"))
                .thenReturn(Flux.just(view1, view2));

        webTestClient.get()
                .uri("/transporte/entregas/motorista/MOT-001")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(EntregaView.class)
                .hasSize(2);
    }

    @Test
    @DisplayName("Deve listar entregas por status")
    void deveListarEntregasPorStatus() {
        EntregaView view1 = EntregaView.builder()
                .id("ENT-001")
                .status("AGENDADA")
                .build();
        
        EntregaView view2 = EntregaView.builder()
                .id("ENT-002")
                .status("AGENDADA")
                .build();

        when(service.listarPorStatus("AGENDADA"))
                .thenReturn(Flux.just(view1, view2));

        webTestClient.get()
                .uri("/transporte/entregas/status/AGENDADA")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(EntregaView.class)
                .hasSize(2);
    }

    @Test
    @DisplayName("Deve listar todas as entregas")
    void deveListarTodasEntregas() {
        EntregaView view1 = EntregaView.builder()
                .id("ENT-001")
                .status("AGENDADA")
                .build();
        
        EntregaView view2 = EntregaView.builder()
                .id("ENT-002")
                .status("EM_TRANSITO")
                .build();
        
        EntregaView view3 = EntregaView.builder()
                .id("ENT-003")
                .status("CONCLUIDA")
                .build();

        when(service.listarTodas())
                .thenReturn(Flux.just(view1, view2, view3));

        webTestClient.get()
                .uri("/transporte/entregas")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(EntregaView.class)
                .hasSize(3);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando motorista não tem entregas")
    void deveRetornarListaVaziaQuandoMotoristaNaoTemEntregas() {
        when(service.listarPorMotorista(anyString()))
                .thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/transporte/entregas/motorista/MOT-999")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(EntregaView.class)
                .hasSize(0);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando status não tem entregas")
    void deveRetornarListaVaziaQuandoStatusNaoTemEntregas() {
        when(service.listarPorStatus(anyString()))
                .thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/transporte/entregas/status/CANCELADA")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(EntregaView.class)
                .hasSize(0);
    }

    @Test
    @DisplayName("Deve tratar erro ao buscar por ID")
    void deveTratarErroAoBuscarPorId() {
        when(service.obterPorId(anyString()))
                .thenReturn(Mono.error(new RuntimeException("Erro no banco")));

        webTestClient.get()
                .uri("/transporte/entregas/ENT-001")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @DisplayName("Deve tratar erro ao listar por motorista")
    void deveTratarErroAoListarPorMotorista() {
        when(service.listarPorMotorista(anyString()))
                .thenReturn(Flux.error(new RuntimeException("Erro no banco")));

        webTestClient.get()
                .uri("/transporte/entregas/motorista/MOT-001")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @DisplayName("Deve tratar erro ao listar todas")
    void deveTratarErroAoListarTodas() {
        when(service.listarTodas())
                .thenReturn(Flux.error(new RuntimeException("Erro no banco")));

        webTestClient.get()
                .uri("/transporte/entregas")
                .exchange()
                .expectStatus().is5xxServerError();
    }
}

