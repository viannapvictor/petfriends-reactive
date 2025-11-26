package com.petfriends.transporte.controllers;

import com.petfriends.transporte.services.EntregaCommandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@WebFluxTest(EntregaCommandController.class)
@DisplayName("Testes do EntregaCommandController")
class EntregaCommandControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private EntregaCommandService service;

    @Test
    @DisplayName("Deve iniciar transporte com sucesso")
    void deveIniciarTransporteComSucesso() {
        // Arrange
        String request = """
            {
                "motoristaId": "MOT-001",
                "veiculoId": "VEI-001"
            }
            """;

        when(service.iniciarTransporte("ENT-001", "MOT-001", "VEI-001"))
                .thenReturn(Mono.just("ENT-001"));

        // Act & Assert
        webTestClient.put()
                .uri("/transporte/entregas/ENT-001/iniciar")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.entregaId").isEqualTo("ENT-001")
                .jsonPath("$.message").isEqualTo("Transporte iniciado com sucesso");

        verify(service).iniciarTransporte("ENT-001", "MOT-001", "VEI-001");
    }

    @Test
    @DisplayName("Deve retornar erro ao iniciar transporte com entrega inválida")
    void deveRetornarErroAoIniciarTransporteComEntregaInvalida() {
        // Arrange
        String request = """
            {
                "motoristaId": "MOT-001",
                "veiculoId": "VEI-001"
            }
            """;

        when(service.iniciarTransporte("ENT-999", "MOT-001", "VEI-001"))
                .thenReturn(Mono.error(new IllegalArgumentException("Entrega não encontrada")));

        // Act & Assert
        webTestClient.put()
                .uri("/transporte/entregas/ENT-999/iniciar")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Entrega não encontrada");

        verify(service).iniciarTransporte("ENT-999", "MOT-001", "VEI-001");
    }

    @Test
    @DisplayName("Deve concluir entrega com sucesso")
    void deveConcluirEntregaComSucesso() {
        // Arrange
        String request = """
            {
                "recebedor": "Cliente",
                "dataRecebimento": "2025-12-01T14:30:00",
                "observacoes": "Entrega realizada com sucesso"
            }
            """;

        when(service.concluirEntrega("ENT-001", "Cliente", "2025-12-01T14:30:00", "Entrega realizada com sucesso"))
                .thenReturn(Mono.just("ENT-001"));

        // Act & Assert
        webTestClient.put()
                .uri("/transporte/entregas/ENT-001/concluir")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.entregaId").isEqualTo("ENT-001")
                .jsonPath("$.message").isEqualTo("Entrega concluída com sucesso");

        verify(service).concluirEntrega("ENT-001", "Cliente", "2025-12-01T14:30:00", "Entrega realizada com sucesso");
    }

    @Test
    @DisplayName("Deve retornar erro ao concluir entrega não iniciada")
    void deveRetornarErroAoConcluirEntregaNaoIniciada() {
        // Arrange
        String request = """
            {
                "recebedor": "Cliente",
                "dataRecebimento": "2025-12-01T14:30:00",
                "observacoes": "OK"
            }
            """;

        when(service.concluirEntrega(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Mono.error(new IllegalStateException("Entrega não está em trânsito")));

        // Act & Assert
        webTestClient.put()
                .uri("/transporte/entregas/ENT-001/concluir")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Entrega não está em trânsito");

        verify(service).concluirEntrega("ENT-001", "Cliente", "2025-12-01T14:30:00", "OK");
    }

    @Test
    @DisplayName("Deve devolver entrega com sucesso")
    void deveDevolverEntregaComSucesso() {
        // Arrange
        String request = """
            {
                "motivo": "Cliente ausente",
                "dataDevolucao": "2025-12-01T14:30:00",
                "responsavel": "MOT-001"
            }
            """;

        when(service.devolverEntrega("ENT-001", "Cliente ausente", "2025-12-01T14:30:00", "MOT-001"))
                .thenReturn(Mono.just("ENT-001"));

        // Act & Assert
        webTestClient.put()
                .uri("/transporte/entregas/ENT-001/devolver")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.entregaId").isEqualTo("ENT-001")
                .jsonPath("$.message").isEqualTo("Entrega devolvida com sucesso");

        verify(service).devolverEntrega("ENT-001", "Cliente ausente", "2025-12-01T14:30:00", "MOT-001");
    }

    @Test
    @DisplayName("Deve retornar erro ao devolver entrega sem motivo")
    void deveRetornarErroAoDevolverEntregaSemMotivo() {
        // Arrange
        String request = """
            {
                "motivo": "",
                "dataDevolucao": "2025-12-01T14:30:00",
                "responsavel": "MOT-001"
            }
            """;

        when(service.devolverEntrega(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Mono.error(new IllegalArgumentException("Motivo é obrigatório")));

        // Act & Assert
        webTestClient.put()
                .uri("/transporte/entregas/ENT-001/devolver")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Motivo é obrigatório");

        verify(service).devolverEntrega("ENT-001", "", "2025-12-01T14:30:00", "MOT-001");
    }

    @Test
    @DisplayName("Deve marcar entrega como extraviada com sucesso")
    void deveMarcarEntregaExtraviadaComSucesso() {
        // Arrange
        String request = """
            {
                "motivo": "Pacote perdido no centro de distribuição",
                "dataExtravio": "2025-12-01T14:30:00",
                "localUltimoRegistro": "Centro de Distribuição SP"
            }
            """;

        when(service.marcarExtraviada("ENT-001", "Pacote perdido no centro de distribuição",
                "2025-12-01T14:30:00", "Centro de Distribuição SP"))
                .thenReturn(Mono.just("ENT-001"));

        // Act & Assert
        webTestClient.put()
                .uri("/transporte/entregas/ENT-001/marcar-extraviada")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.entregaId").isEqualTo("ENT-001")
                .jsonPath("$.message").isEqualTo("Entrega marcada como extraviada");

        verify(service).marcarExtraviada("ENT-001", "Pacote perdido no centro de distribuição",
                "2025-12-01T14:30:00", "Centro de Distribuição SP");
    }

    @Test
    @DisplayName("Deve retornar erro ao marcar entrega extraviada sem local")
    void deveRetornarErroAoMarcarEntregaExtraviadaSemLocal() {
        // Arrange
        String request = """
            {
                "motivo": "Pacote perdido",
                "dataExtravio": "2025-12-01T14:30:00",
                "localUltimoRegistro": ""
            }
            """;

        when(service.marcarExtraviada(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Mono.error(new IllegalArgumentException("Local do último registro é obrigatório")));

        // Act & Assert
        webTestClient.put()
                .uri("/transporte/entregas/ENT-001/marcar-extraviada")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Local do último registro é obrigatório");

        verify(service).marcarExtraviada("ENT-001", "Pacote perdido", "2025-12-01T14:30:00", "");
    }

    @Test
    @DisplayName("Deve tratar erro genérico ao iniciar transporte")
    void deveTratarErroGenericoAoIniciarTransporte() {
        // Arrange
        String request = """
            {
                "motoristaId": "MOT-001",
                "veiculoId": "VEI-001"
            }
            """;

        when(service.iniciarTransporte(anyString(), anyString(), anyString()))
                .thenReturn(Mono.error(new RuntimeException("Erro no banco de dados")));

        // Act & Assert
        webTestClient.put()
                .uri("/transporte/entregas/ENT-001/iniciar")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Erro no banco de dados");

        verify(service).iniciarTransporte("ENT-001", "MOT-001", "VEI-001");
    }

    @Test
    @DisplayName("Deve tratar erro genérico ao concluir entrega")
    void deveTratarErroGenericoAoConcluirEntrega() {
        // Arrange
        String request = """
            {
                "recebedor": "Cliente",
                "dataRecebimento": "2025-12-01T14:30:00",
                "observacoes": "OK"
            }
            """;

        when(service.concluirEntrega(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Mono.error(new RuntimeException("Erro ao processar evento")));

        // Act & Assert
        webTestClient.put()
                .uri("/transporte/entregas/ENT-001/concluir")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Erro ao processar evento");

        verify(service).concluirEntrega("ENT-001", "Cliente", "2025-12-01T14:30:00", "OK");
    }

    @Test
    @DisplayName("Deve tratar erro genérico ao devolver entrega")
    void deveTratarErroGenericoAoDevolverEntrega() {
        // Arrange
        String request = """
            {
                "motivo": "Cliente ausente",
                "dataDevolucao": "2025-12-01T14:30:00",
                "responsavel": "MOT-001"
            }
            """;

        when(service.devolverEntrega(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Mono.error(new RuntimeException("Falha na comunicação")));

        // Act & Assert
        webTestClient.put()
                .uri("/transporte/entregas/ENT-001/devolver")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Falha na comunicação");

        verify(service).devolverEntrega("ENT-001", "Cliente ausente", "2025-12-01T14:30:00", "MOT-001");
    }

    @Test
    @DisplayName("Deve tratar erro genérico ao marcar entrega como extraviada")
    void deveTratarErroGenericoAoMarcarEntregaExtraviada() {
        // Arrange
        String request = """
            {
                "motivo": "Pacote perdido",
                "dataExtravio": "2025-12-01T14:30:00",
                "localUltimoRegistro": "Centro de Distribuição"
            }
            """;

        when(service.marcarExtraviada(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Mono.error(new RuntimeException("Serviço indisponível")));

        // Act & Assert
        webTestClient.put()
                .uri("/transporte/entregas/ENT-001/marcar-extraviada")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Serviço indisponível");

        verify(service).marcarExtraviada("ENT-001", "Pacote perdido", "2025-12-01T14:30:00", "Centro de Distribuição");
    }
}