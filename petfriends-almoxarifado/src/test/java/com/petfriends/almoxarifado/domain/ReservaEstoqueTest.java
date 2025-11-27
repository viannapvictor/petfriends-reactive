package com.petfriends.almoxarifado.domain;

import com.petfriends.almoxarifado.commands.*;
import com.petfriends.almoxarifado.events.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do Agregado ReservaEstoque")
class ReservaEstoqueTest {

    private ReservaEstoque reserva;
    private Endereco enderecoTeste;

    @BeforeEach
    void setUp() {
        reserva = new ReservaEstoque("RES-001");
        enderecoTeste = new Endereco("Rua A", "100", "Apt 10", "Centro", "São Paulo", "SP", "01000-000");
    }

    @Test
    @DisplayName("Deve criar reserva vazia com construtor padrão")
    void deveCriarReservaVazia() {
        ReservaEstoque novaReserva = new ReservaEstoque();

        assertNotNull(novaReserva.getItens());
        assertTrue(novaReserva.getItens().isEmpty());
    }

    @Test
    @DisplayName("Deve criar reserva com ID")
    void deveCriarReservaComId() {
        ReservaEstoque novaReserva = new ReservaEstoque("RES-002");

        assertEquals("RES-002", novaReserva.getId());
        assertNotNull(novaReserva.getItens());
    }

    @Test
    @DisplayName("Deve reservar estoque com sucesso quando há disponibilidade")
    void deveReservarEstoqueComSucesso() {
        List<ReservarEstoqueCommand.ItemReservaDTO> itens = Arrays.asList(
                new ReservarEstoqueCommand.ItemReservaDTO("PROD-001", 10),
                new ReservarEstoqueCommand.ItemReservaDTO("PROD-002", 5)
        );
        ReservarEstoqueCommand comando = new ReservarEstoqueCommand("RES-001", "PED-001", enderecoTeste, itens);

        BaseEvent<?> evento = reserva.reservarEstoque(comando);

        assertInstanceOf(EstoqueReservado.class, evento);
        EstoqueReservado estoqueReservado = (EstoqueReservado) evento;
        assertEquals("RES-001", estoqueReservado.id);
        assertEquals("PED-001", estoqueReservado.pedidoId);
        assertEquals(2, estoqueReservado.itens.size());
    }

    @Test
    @DisplayName("Deve lançar exceção ao reservar estoque sem itens")
    void deveLancarExcecaoAoReservarSemItens() {
        ReservarEstoqueCommand comando = new ReservarEstoqueCommand("RES-001", "PED-001", enderecoTeste, Collections.emptyList());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reserva.reservarEstoque(comando);
        });

        assertEquals("Não é possível reservar estoque sem itens", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao reservar estoque com itens nulos")
    void deveLancarExcecaoAoReservarComItensNulos() {

        ReservarEstoqueCommand comando = new ReservarEstoqueCommand("RES-001", "PED-001", enderecoTeste, null);

        assertThrows(IllegalArgumentException.class, () -> {
            reserva.reservarEstoque(comando);
        });
    }

    @Test
    @DisplayName("Deve gerar evento de estoque insuficiente quando quantidade é zero")
    void deveGerarEventoEstoqueInsuficiente() {

        List<ReservarEstoqueCommand.ItemReservaDTO> itens = Arrays.asList(
                new ReservarEstoqueCommand.ItemReservaDTO("PROD-001", 0)
        );
        ReservarEstoqueCommand comando = new ReservarEstoqueCommand("RES-001", "PED-001", enderecoTeste, itens);

        BaseEvent<?> evento = reserva.reservarEstoque(comando);

        assertInstanceOf(EstoqueInsuficiente.class, evento);
        EstoqueInsuficiente estoqueInsuficiente = (EstoqueInsuficiente) evento;
        assertEquals("RES-001", estoqueInsuficiente.id);
        assertEquals(1, estoqueInsuficiente.produtosIndisponiveis.size());
    }

    @Test
    @DisplayName("Deve confirmar reserva com sucesso")
    void deveConfirmarReservaComSucesso() {
        // Arrange
        reserva.setStatus(StatusReserva.PENDENTE.toString());
        reserva.setPedidoId("PED-001");
        ConfirmarReservaCommand comando = new ConfirmarReservaCommand("RES-001");

        BaseEvent<?> evento = reserva.confirmarReserva(comando);

        assertInstanceOf(ReservaConfirmada.class, evento);
        ReservaConfirmada reservaConfirmada = (ReservaConfirmada) evento;
        assertEquals("RES-001", reservaConfirmada.id);
        assertEquals("PED-001", reservaConfirmada.pedidoId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao confirmar reserva com estoque insuficiente")
    void deveLancarExcecaoAoConfirmarReservaComEstoqueInsuficiente() {
        reserva.setStatus(StatusReserva.INSUFICIENTE.toString());
        ConfirmarReservaCommand comando = new ConfirmarReservaCommand("RES-001");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            reserva.confirmarReserva(comando);
        });

        assertEquals("Não é possível confirmar reserva com estoque insuficiente", exception.getMessage());
    }

    @Test
    @DisplayName("Deve cancelar reserva com sucesso")
    void deveCancelarReservaComSucesso() {

        reserva.setStatus(StatusReserva.PENDENTE.toString());
        reserva.setPedidoId("PED-001");
        CancelarReservaCommand comando = new CancelarReservaCommand("RES-001", "Cliente desistiu");

        BaseEvent<?> evento = reserva.cancelarReserva(comando);

        assertInstanceOf(ReservaCancelada.class, evento);
        ReservaCancelada reservaCancelada = (ReservaCancelada) evento;
        assertEquals("RES-001", reservaCancelada.id);
        assertEquals("Cliente desistiu", reservaCancelada.motivo);
    }

    @Test
    @DisplayName("Deve lançar exceção ao cancelar reserva já separada")
    void deveLancarExcecaoAoCancelarReservaSeparada() {

        reserva.setStatus(StatusReserva.SEPARADA.toString());
        CancelarReservaCommand comando = new CancelarReservaCommand("RES-001", "Motivo qualquer");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            reserva.cancelarReserva(comando);
        });

        assertEquals("Não é possível cancelar reserva já separada", exception.getMessage());
    }

    @Test
    @DisplayName("Deve separar itens com sucesso")
    void deveSepararItensComSucesso() {

        reserva.setStatus(StatusReserva.CONFIRMADA.toString());
        reserva.setPedidoId("PED-001");
        reserva.setEnderecoEntrega(enderecoTeste);
        SepararItensCommand comando = new SepararItensCommand("RES-001", "OP-001");

        BaseEvent<?> evento = reserva.separarItens(comando);

        assertInstanceOf(ItensSeparados.class, evento);
        ItensSeparados itensSeparados = (ItensSeparados) evento;
        assertEquals("RES-001", itensSeparados.id);
        assertEquals("OP-001", itensSeparados.operadorId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao separar itens de reserva não confirmada")
    void deveLancarExcecaoAoSepararItensNaoConfirmados() {

        reserva.setStatus(StatusReserva.PENDENTE.toString());
        SepararItensCommand comando = new SepararItensCommand("RES-001", "OP-001");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            reserva.separarItens(comando);
        });

        assertEquals("Só é possível separar itens de reservas confirmadas", exception.getMessage());
    }

    @Test
    @DisplayName("Deve aplicar evento EstoqueReservado corretamente")
    void deveAplicarEventoEstoqueReservado() {

        List<EstoqueReservado.ItemReservado> itens = Arrays.asList(
                new EstoqueReservado.ItemReservado("PROD-001", 10),
                new EstoqueReservado.ItemReservado("PROD-002", 5)
        );
        EstoqueReservado evento = new EstoqueReservado("RES-001", "PED-001", enderecoTeste, itens);

        reserva.apply(evento);

        assertEquals("RES-001", reserva.getId());
        assertEquals("PED-001", reserva.getPedidoId());
        assertEquals(StatusReserva.PENDENTE.toString(), reserva.getStatus());
        assertEquals(2, reserva.getItens().size());
        assertEquals(enderecoTeste, reserva.getEnderecoEntrega());
    }

    @Test
    @DisplayName("Deve aplicar evento EstoqueInsuficiente corretamente")
    void deveAplicarEventoEstoqueInsuficiente() {

        EstoqueInsuficiente evento = new EstoqueInsuficiente(
                "RES-001",
                "PED-001",
                Arrays.asList("PROD-001"),
                "Estoque insuficiente"
        );

        reserva.apply(evento);

        assertEquals("RES-001", reserva.getId());
        assertEquals("PED-001", reserva.getPedidoId());
        assertEquals(StatusReserva.INSUFICIENTE.toString(), reserva.getStatus());
    }

    @Test
    @DisplayName("Deve aplicar evento ReservaConfirmada corretamente")
    void deveAplicarEventoReservaConfirmada() {

        ReservaConfirmada evento = new ReservaConfirmada("RES-001", "PED-001");

        reserva.apply(evento);

        assertEquals(StatusReserva.CONFIRMADA.toString(), reserva.getStatus());
    }

    @Test
    @DisplayName("Deve aplicar evento ReservaCancelada corretamente")
    void deveAplicarEventoReservaCancelada() {

        ReservaCancelada evento = new ReservaCancelada("RES-001", "PED-001", "Cliente desistiu");

        reserva.apply(evento);

        assertEquals(StatusReserva.CANCELADA.toString(), reserva.getStatus());
    }

    @Test
    @DisplayName("Deve aplicar evento ItensSeparados corretamente")
    void deveAplicarEventoItensSeparados() {

        ItensSeparados evento = new ItensSeparados("RES-001", "PED-001", enderecoTeste, "OP-001");

        reserva.apply(evento);

        assertEquals(StatusReserva.SEPARADA.toString(), reserva.getStatus());
        assertEquals("OP-001", reserva.getOperadorId());
    }

    @Test
    @DisplayName("Deve criar ItemReserva com construtor padrão")
    void deveCriarItemReservaComConstrutorPadrao() {
        ReservaEstoque.ItemReserva item = new ReservaEstoque.ItemReserva();

        assertNotNull(item);
    }

    @Test
    @DisplayName("Deve criar ItemReserva com parâmetros")
    void deveCriarItemReservaComParametros() {
        ReservaEstoque.ItemReserva item = new ReservaEstoque.ItemReserva("PROD-001", 10);

        assertEquals("PROD-001", item.getProdutoId());
        assertEquals(10, item.getQuantidade());
    }

    @Test
    @DisplayName("Deve retornar operadorId após separar itens")
    void deveRetornarOperadorIdAposSepararItens() {
        ItensSeparados evento = new ItensSeparados("RES-001", "PED-001", enderecoTeste, "OP-123");

        reserva.apply(evento);

        assertEquals("OP-123", reserva.getOperadorId());
    }

    @Test
    @DisplayName("Deve retornar EstoqueInsuficiente quando quantidade é zero")
    void deveRetornarEstoqueInsuficienteQuandoQuantidadeEZero() {
        Endereco endereco = new Endereco("Rua Teste", "123", null, "Centro", "São Paulo", "SP", "01000-000");
        List<ReservarEstoqueCommand.ItemReservaDTO> itens = Arrays.asList(
                new ReservarEstoqueCommand.ItemReservaDTO("PROD-001", 0)
        );
        ReservarEstoqueCommand comando = new ReservarEstoqueCommand("RES-001", "PED-001", endereco, itens);
        
        ReservaEstoque novaReserva = new ReservaEstoque("RES-001");
        BaseEvent<?> evento = novaReserva.reservarEstoque(comando);

        assertTrue(evento instanceof EstoqueInsuficiente);
    }

    @Test
    @DisplayName("Deve retornar EstoqueInsuficiente quando quantidade é negativa")
    void deveRetornarEstoqueInsuficienteQuandoQuantidadeENegativa() {
        Endereco endereco = new Endereco("Rua Teste", "123", null, "Centro", "São Paulo", "SP", "01000-000");
        List<ReservarEstoqueCommand.ItemReservaDTO> itens = Arrays.asList(
                new ReservarEstoqueCommand.ItemReservaDTO("PROD-001", -5)
        );
        ReservarEstoqueCommand comando = new ReservarEstoqueCommand("RES-001", "PED-001", endereco, itens);
        
        ReservaEstoque novaReserva = new ReservaEstoque("RES-001");
        BaseEvent<?> evento = novaReserva.reservarEstoque(comando);

        assertTrue(evento instanceof EstoqueInsuficiente);
    }

    @Test
    @DisplayName("Deve retornar EstoqueInsuficiente quando algum item tem quantidade inválida")
    void deveRetornarEstoqueInsuficienteQuandoAlgumItemTemQuantidadeInvalida() {
        Endereco endereco = new Endereco("Rua Teste", "123", null, "Centro", "São Paulo", "SP", "01000-000");
        List<ReservarEstoqueCommand.ItemReservaDTO> itens = Arrays.asList(
                new ReservarEstoqueCommand.ItemReservaDTO("PROD-001", 10),
                new ReservarEstoqueCommand.ItemReservaDTO("PROD-002", 0),
                new ReservarEstoqueCommand.ItemReservaDTO("PROD-003", 5)
        );
        ReservarEstoqueCommand comando = new ReservarEstoqueCommand("RES-001", "PED-001", endereco, itens);
        
        ReservaEstoque novaReserva = new ReservaEstoque("RES-001");
        BaseEvent<?> evento = novaReserva.reservarEstoque(comando);

        assertTrue(evento instanceof EstoqueInsuficiente);
        EstoqueInsuficiente eventoInsuficiente = (EstoqueInsuficiente) evento;
        assertEquals(3, eventoInsuficiente.produtosIndisponiveis.size());
    }
}