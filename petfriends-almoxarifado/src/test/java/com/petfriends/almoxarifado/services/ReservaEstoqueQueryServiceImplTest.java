package com.petfriends.almoxarifado.services;

import com.petfriends.almoxarifado.readmodel.ReservaEstoqueView;
import com.petfriends.almoxarifado.readmodel.ReservaEstoqueViewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ReservaEstoqueQueryServiceImpl")
class ReservaEstoqueQueryServiceImplTest {

    @Mock
    private ReservaEstoqueViewRepository viewRepository;

    @InjectMocks
    private ReservaEstoqueQueryServiceImpl service;

    private ReservaEstoqueView reservaView;

    @BeforeEach
    void setUp() {
        reservaView = ReservaEstoqueView.builder()
                .id("RES-001")
                .pedidoId("PED-001")
                .status("RESERVADO")
                .enderecoRua("Rua A")
                .enderecoNumero("100")
                .enderecoComplemento("Apt 10")
                .enderecoBairro("Centro")
                .enderecoCidade("São Paulo")
                .enderecoEstado("SP")
                .enderecoCep("01000-000")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve obter reserva por ID com sucesso")
    void deveObterReservaPorIdComSucesso() {
        // Arrange
        when(viewRepository.findById("RES-001"))
                .thenReturn(Mono.just(reservaView));

        // Act & Assert
        StepVerifier.create(service.obterPorId("RES-001"))
                .expectNext(reservaView)
                .verifyComplete();

        verify(viewRepository).findById("RES-001");
    }

    @Test
    @DisplayName("Deve retornar vazio quando reserva não existe")
    void deveRetornarVazioQuandoReservaNaoExiste() {
        // Arrange
        when(viewRepository.findById("RES-999"))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.obterPorId("RES-999"))
                .verifyComplete();

        verify(viewRepository).findById("RES-999");
    }

    @Test
    @DisplayName("Deve tratar erro ao buscar reserva por ID")
    void deveTratarErroAoBuscarReservaPorId() {
        // Arrange
        when(viewRepository.findById(anyString()))
                .thenReturn(Mono.error(new RuntimeException("Erro ao buscar no banco")));

        // Act & Assert
        StepVerifier.create(service.obterPorId("RES-001"))
                .expectError(RuntimeException.class)
                .verify();

        verify(viewRepository).findById("RES-001");
    }

    @Test
    @DisplayName("Deve obter reserva por pedidoId com sucesso")
    void deveObterReservaPorPedidoIdComSucesso() {
        // Arrange
        when(viewRepository.findByPedidoId("PED-001"))
                .thenReturn(Mono.just(reservaView));

        // Act & Assert
        StepVerifier.create(service.obterPorPedidoId("PED-001"))
                .expectNext(reservaView)
                .verifyComplete();

        verify(viewRepository).findByPedidoId("PED-001");
    }

    @Test
    @DisplayName("Deve retornar vazio quando pedido não possui reserva")
    void deveRetornarVazioQuandoPedidoNaoPossuiReserva() {
        // Arrange
        when(viewRepository.findByPedidoId("PED-999"))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.obterPorPedidoId("PED-999"))
                .verifyComplete();

        verify(viewRepository).findByPedidoId("PED-999");
    }

    @Test
    @DisplayName("Deve tratar erro ao buscar reserva por pedidoId")
    void deveTratarErroAoBuscarReservaPorPedidoId() {
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
    @DisplayName("Deve lidar com ID nulo ao buscar por ID")
    void deveLidarComIdNuloAoBuscarPorId() {
        // Arrange
        when(viewRepository.findById((String) null))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.obterPorId(null))
                .verifyComplete();

        verify(viewRepository).findById((String) null);
    }

    @Test
    @DisplayName("Deve lidar com pedidoId nulo ao buscar por pedidoId")
    void deveLidarComPedidoIdNuloAoBuscarPorPedidoId() {
        // Arrange
        when(viewRepository.findByPedidoId(null))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.obterPorPedidoId(null))
                .verifyComplete();

        verify(viewRepository).findByPedidoId(null);
    }
}