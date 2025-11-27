package com.petfriends.transporte.services;

import com.petfriends.transporte.readmodel.EntregaView;
import com.petfriends.transporte.readmodel.EntregaViewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do EntregaQueryServiceImpl")
class EntregaQueryServiceImplTest {

    @Mock
    private EntregaViewRepository viewRepository;

    @InjectMocks
    private EntregaQueryServiceImpl service;

    private EntregaView entregaView;

    @BeforeEach
    void setUp() {
        entregaView = EntregaView.builder()
                .id("ENT-001")
                .pedidoId("PED-001")
                .reservaId("RES-001")
                .status("AGENDADA")
                .enderecoCompleto("Rua A, 100, Apt 10, Centro, São Paulo - SP, 01000-000")
                .dataPrevisaoEntrega("2025-12-01")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve obter entrega por ID com sucesso")
    void deveObterEntregaPorIdComSucesso() {
        // Arrange
        when(viewRepository.findById("ENT-001"))
                .thenReturn(Mono.just(entregaView));

        // Act & Assert
        StepVerifier.create(service.obterPorId("ENT-001"))
                .expectNext(entregaView)
                .verifyComplete();

        verify(viewRepository).findById("ENT-001");
    }

    @Test
    @DisplayName("Deve retornar vazio quando entrega não existe")
    void deveRetornarVazioQuandoEntregaNaoExiste() {
        // Arrange
        when(viewRepository.findById("ENT-999"))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.obterPorId("ENT-999"))
                .verifyComplete();

        verify(viewRepository).findById("ENT-999");
    }

    @Test
    @DisplayName("Deve tratar erro ao buscar entrega por ID")
    void deveTratarErroAoBuscarEntregaPorId() {
        // Arrange
        when(viewRepository.findById(anyString()))
                .thenReturn(Mono.error(new RuntimeException("Erro ao buscar no banco")));

        // Act & Assert
        StepVerifier.create(service.obterPorId("ENT-001"))
                .expectError(RuntimeException.class)
                .verify();

        verify(viewRepository).findById("ENT-001");
    }

    @Test
    @DisplayName("Deve obter entrega por pedidoId com sucesso")
    void deveObterEntregaPorPedidoIdComSucesso() {
        // Arrange
        when(viewRepository.findByPedidoId("PED-001"))
                .thenReturn(Mono.just(entregaView));

        // Act & Assert
        StepVerifier.create(service.obterPorPedidoId("PED-001"))
                .expectNext(entregaView)
                .verifyComplete();

        verify(viewRepository).findByPedidoId("PED-001");
    }

    @Test
    @DisplayName("Deve retornar vazio quando pedido não possui entrega")
    void deveRetornarVazioQuandoPedidoNaoPossuiEntrega() {
        // Arrange
        when(viewRepository.findByPedidoId("PED-999"))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.obterPorPedidoId("PED-999"))
                .verifyComplete();

        verify(viewRepository).findByPedidoId("PED-999");
    }

    @Test
    @DisplayName("Deve tratar erro ao buscar entrega por pedidoId")
    void deveTratarErroAoBuscarEntregaPorPedidoId() {
        // Arrange
        when(viewRepository.findByPedidoId(anyString()))
                .thenReturn(Mono.error(new RuntimeException("Erro ao buscar no banco")));

        // Act & Assert
        StepVerifier.create(service.obterPorPedidoId("PED-001"))
                .expectError(RuntimeException.class)
                .verify();

        verify(viewRepository).findByPedidoId("PED-001");
    }

    @Test
    @DisplayName("Deve obter entrega por reservaId com sucesso")
    void deveObterEntregaPorReservaIdComSucesso() {
        // Arrange
        when(viewRepository.findByReservaId("RES-001"))
                .thenReturn(Mono.just(entregaView));

        // Act & Assert
        StepVerifier.create(service.obterPorReservaId("RES-001"))
                .expectNext(entregaView)
                .verifyComplete();

        verify(viewRepository).findByReservaId("RES-001");
    }

    @Test
    @DisplayName("Deve retornar vazio quando reserva não possui entrega")
    void deveRetornarVazioQuandoReservaNaoPossuiEntrega() {
        // Arrange
        when(viewRepository.findByReservaId("RES-999"))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.obterPorReservaId("RES-999"))
                .verifyComplete();

        verify(viewRepository).findByReservaId("RES-999");
    }

    @Test
    @DisplayName("Deve tratar erro ao buscar entrega por reservaId")
    void deveTratarErroAoBuscarEntregaPorReservaId() {
        // Arrange
        when(viewRepository.findByReservaId(anyString()))
                .thenReturn(Mono.error(new RuntimeException("Erro ao buscar no banco")));

        // Act & Assert
        StepVerifier.create(service.obterPorReservaId("RES-001"))
                .expectError(RuntimeException.class)
                .verify();

        verify(viewRepository).findByReservaId("RES-001");
    }

    @Test
    @DisplayName("Deve listar entregas por motorista com sucesso")
    void deveListarEntregasPorMotoristaComSucesso() {
        // Arrange
        EntregaView entrega2 = EntregaView.builder()
                .id("ENT-002")
                .pedidoId("PED-002")
                .motoristaId("MOT-001")
                .status("EM_TRANSPORTE")
                .build();

        when(viewRepository.findByMotoristaId("MOT-001"))
                .thenReturn(Flux.just(entregaView, entrega2));

        // Act & Assert
        StepVerifier.create(service.listarPorMotorista("MOT-001"))
                .expectNext(entregaView)
                .expectNext(entrega2)
                .verifyComplete();

        verify(viewRepository).findByMotoristaId("MOT-001");
    }

    @Test
    @DisplayName("Deve retornar vazio quando motorista não possui entregas")
    void deveRetornarVazioQuandoMotoristaNaoPossuiEntregas() {
        // Arrange
        when(viewRepository.findByMotoristaId("MOT-999"))
                .thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(service.listarPorMotorista("MOT-999"))
                .verifyComplete();

        verify(viewRepository).findByMotoristaId("MOT-999");
    }

    @Test
    @DisplayName("Deve tratar erro ao listar entregas por motorista")
    void deveTratarErroAoListarEntregasPorMotorista() {
        // Arrange
        when(viewRepository.findByMotoristaId(anyString()))
                .thenReturn(Flux.error(new RuntimeException("Erro ao buscar no banco")));

        // Act & Assert
        StepVerifier.create(service.listarPorMotorista("MOT-001"))
                .expectError(RuntimeException.class)
                .verify();

        verify(viewRepository).findByMotoristaId("MOT-001");
    }

    @Test
    @DisplayName("Deve listar entregas por status com sucesso")
    void deveListarEntregasPorStatusComSucesso() {
        // Arrange
        EntregaView entrega2 = EntregaView.builder()
                .id("ENT-002")
                .pedidoId("PED-002")
                .status("AGENDADA")
                .build();

        when(viewRepository.findByStatus("AGENDADA"))
                .thenReturn(Flux.just(entregaView, entrega2));

        // Act & Assert
        StepVerifier.create(service.listarPorStatus("AGENDADA"))
                .expectNext(entregaView)
                .expectNext(entrega2)
                .verifyComplete();

        verify(viewRepository).findByStatus("AGENDADA");
    }

    @Test
    @DisplayName("Deve retornar vazio quando não há entregas com o status")
    void deveRetornarVazioQuandoNaoHaEntregasComStatus() {
        // Arrange
        when(viewRepository.findByStatus("CANCELADA"))
                .thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(service.listarPorStatus("CANCELADA"))
                .verifyComplete();

        verify(viewRepository).findByStatus("CANCELADA");
    }

    @Test
    @DisplayName("Deve tratar erro ao listar entregas por status")
    void deveTratarErroAoListarEntregasPorStatus() {
        // Arrange
        when(viewRepository.findByStatus(anyString()))
                .thenReturn(Flux.error(new RuntimeException("Erro ao buscar no banco")));

        // Act & Assert
        StepVerifier.create(service.listarPorStatus("AGENDADA"))
                .expectError(RuntimeException.class)
                .verify();

        verify(viewRepository).findByStatus("AGENDADA");
    }

    @Test
    @DisplayName("Deve listar todas as entregas com sucesso")
    void deveListarTodasEntregasComSucesso() {
        // Arrange
        EntregaView entrega2 = EntregaView.builder()
                .id("ENT-002")
                .pedidoId("PED-002")
                .status("EM_TRANSPORTE")
                .build();

        EntregaView entrega3 = EntregaView.builder()
                .id("ENT-003")
                .pedidoId("PED-003")
                .status("CONCLUIDA")
                .build();

        when(viewRepository.findAll())
                .thenReturn(Flux.just(entregaView, entrega2, entrega3));

        // Act & Assert
        StepVerifier.create(service.listarTodas())
                .expectNext(entregaView)
                .expectNext(entrega2)
                .expectNext(entrega3)
                .verifyComplete();

        verify(viewRepository).findAll();
    }

    @Test
    @DisplayName("Deve retornar vazio quando não há entregas cadastradas")
    void deveRetornarVazioQuandoNaoHaEntregasCadastradas() {
        // Arrange
        when(viewRepository.findAll())
                .thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(service.listarTodas())
                .verifyComplete();

        verify(viewRepository).findAll();
    }

    @Test
    @DisplayName("Deve tratar erro ao listar todas as entregas")
    void deveTratarErroAoListarTodasEntregas() {
        // Arrange
        when(viewRepository.findAll())
                .thenReturn(Flux.error(new RuntimeException("Erro ao buscar no banco")));

        // Act & Assert
        StepVerifier.create(service.listarTodas())
                .expectError(RuntimeException.class)
                .verify();

        verify(viewRepository).findAll();
    }

    @Test
    @DisplayName("Deve buscar entrega com todos os campos preenchidos")
    void deveBuscarEntregaComTodosCamposPreenchidos() {
        EntregaView view = EntregaView.builder()
                .id("ENT-001")
                .pedidoId("PED-001")
                .reservaId("RES-001")
                .status("CONCLUIDA")
                .enderecoCompleto("Rua A, 100 - Centro, SP/SP")
                .dataPrevisaoEntrega("2025-12-31")
                .motoristaId("MOT-001")
                .veiculoId("VEI-001")
                .recebedor("João Silva")
                .dataHoraRecebimento("2025-12-25T10:00:00")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(viewRepository.findById("ENT-001")).thenReturn(Mono.just(view));

        StepVerifier.create(service.obterPorId("ENT-001"))
                .expectNext(view)
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve processar grande volume de entregas")
    void deveProcessarGrandeVolumeDeEntregas() {
        EntregaView[] views = new EntregaView[50];
        for (int i = 0; i < 50; i++) {
            views[i] = EntregaView.builder().id("ENT-" + i).motoristaId("MOT-001").build();
        }

        when(viewRepository.findByMotoristaId("MOT-001")).thenReturn(Flux.just(views));

        StepVerifier.create(service.listarPorMotorista("MOT-001"))
                .expectNextCount(50)
                .verifyComplete();
    }
}