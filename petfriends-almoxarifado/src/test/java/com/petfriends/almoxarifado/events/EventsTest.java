package com.petfriends.almoxarifado.events;

import com.petfriends.almoxarifado.domain.Endereco;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes dos Events do Almoxarifado")
class EventsTest {

    @Test
    @DisplayName("Deve criar EstoqueReservado com todos os campos")
    void deveCriarEstoqueReservado() {
        Endereco endereco = new Endereco("Rua A", "1", null, "Centro", "SP", "SP", "01000-000");
        List<EstoqueReservado.ItemReservado> itens = Arrays.asList(
                new EstoqueReservado.ItemReservado("PROD-001", 2),
                new EstoqueReservado.ItemReservado("PROD-002", 5)
        );
        LocalDateTime data = LocalDateTime.now();

        EstoqueReservado evento = new EstoqueReservado("RES-001", "PED-001", endereco, itens, data);

        assertEquals("RES-001", evento.getAggregateId());
        assertEquals("PED-001", evento.pedidoId);
        assertEquals(endereco, evento.enderecoEntrega);
        assertEquals(2, evento.itens.size());
        assertEquals(data, evento.dataReserva);
    }

    @Test
    @DisplayName("Deve criar EstoqueReservado sem data específica")
    void deveCriarEstoqueReservadoSemData() {
        Endereco endereco = new Endereco("Rua A", "1", null, "Centro", "SP", "SP", "01000-000");
        List<EstoqueReservado.ItemReservado> itens = Arrays.asList(
                new EstoqueReservado.ItemReservado("PROD-001", 2)
        );

        EstoqueReservado evento = new EstoqueReservado("RES-001", "PED-001", endereco, itens);

        assertNotNull(evento.dataReserva);
        assertEquals("RES-001", evento.getAggregateId());
    }

    @Test
    @DisplayName("Deve criar ItemReservado com campos válidos")
    void deveCriarItemReservado() {
        EstoqueReservado.ItemReservado item = new EstoqueReservado.ItemReservado("PROD-001", 10);

        assertEquals("PROD-001", item.produtoId);
        assertEquals(10, item.quantidade);
    }

    @Test
    @DisplayName("Deve criar EstoqueInsuficiente com todos os campos")
    void deveCriarEstoqueInsuficiente() {
        List<String> produtosIndisponiveis = Arrays.asList("PROD-001", "PROD-003");

        EstoqueInsuficiente evento = new EstoqueInsuficiente(
                "RES-001", "PED-001", produtosIndisponiveis, "Produtos sem estoque"
        );

        assertEquals("RES-001", evento.getAggregateId());
        assertEquals("PED-001", evento.pedidoId);
        assertEquals(2, evento.produtosIndisponiveis.size());
        assertEquals("Produtos sem estoque", evento.mensagem);
        assertTrue(evento.produtosIndisponiveis.contains("PROD-001"));
    }

    @Test
    @DisplayName("Deve criar ReservaConfirmada com campos válidos")
    void deveCriarReservaConfirmada() {
        ReservaConfirmada evento = new ReservaConfirmada("RES-001", "PED-001");

        assertEquals("RES-001", evento.getAggregateId());
        assertEquals("PED-001", evento.pedidoId);
    }

    @Test
    @DisplayName("Deve criar ReservaCancelada com campos válidos")
    void deveCriarReservaCancelada() {
        ReservaCancelada evento = new ReservaCancelada("RES-001", "PED-001", "Cliente cancelou");

        assertEquals("RES-001", evento.getAggregateId());
        assertEquals("PED-001", evento.pedidoId);
        assertEquals("Cliente cancelou", evento.motivo);
    }

    @Test
    @DisplayName("Deve criar ItensSeparados com todos os campos")
    void deveCriarItensSeparados() {
        Endereco endereco = new Endereco("Rua A", "1", null, "Centro", "SP", "SP", "01000-000");
        LocalDateTime data = LocalDateTime.now();

        ItensSeparados evento = new ItensSeparados("RES-001", "PED-001", endereco, "OP-001", data);

        assertEquals("RES-001", evento.getAggregateId());
        assertEquals("PED-001", evento.pedidoId);
        assertEquals(endereco, evento.enderecoEntrega);
        assertEquals("OP-001", evento.operadorId);
        assertEquals(data, evento.dataSeparacao);
    }

    @Test
    @DisplayName("Deve criar ItensSeparados sem data específica")
    void deveCriarItensSeparadosSemData() {
        Endereco endereco = new Endereco("Rua A", "1", null, "Centro", "SP", "SP", "01000-000");

        ItensSeparados evento = new ItensSeparados("RES-001", "PED-001", endereco, "OP-001");

        assertNotNull(evento.dataSeparacao);
        assertEquals("RES-001", evento.getAggregateId());
    }

    @Test
    @DisplayName("Deve criar EstoqueReservado vazio via construtor padrão")
    void deveCriarEstoqueReservadoVazio() {
        EstoqueReservado evento = new EstoqueReservado();

        assertNull(evento.getAggregateId());
        assertNull(evento.pedidoId);
        assertNull(evento.enderecoEntrega);
        assertNull(evento.itens);
        assertNull(evento.dataReserva);
    }

    @Test
    @DisplayName("Deve criar ItemReservado vazio via construtor padrão")
    void deveCriarItemReservadoVazio() {
        EstoqueReservado.ItemReservado item = new EstoqueReservado.ItemReservado();

        assertNull(item.produtoId);
        assertEquals(0, item.quantidade);
    }

    @Test
    @DisplayName("Deve criar EstoqueInsuficiente vazio via construtor padrão")
    void deveCriarEstoqueInsuficienteVazio() {
        EstoqueInsuficiente evento = new EstoqueInsuficiente();

        assertNull(evento.getAggregateId());
        assertNull(evento.pedidoId);
        assertNull(evento.produtosIndisponiveis);
        assertNull(evento.mensagem);
    }

    @Test
    @DisplayName("Deve criar ReservaConfirmada vazia via construtor padrão")
    void deveCriarReservaConfirmadaVazia() {
        ReservaConfirmada evento = new ReservaConfirmada();

        assertNull(evento.getAggregateId());
        assertNull(evento.pedidoId);
    }

    @Test
    @DisplayName("Deve criar ReservaCancelada vazia via construtor padrão")
    void deveCriarReservaCanceladaVazia() {
        ReservaCancelada evento = new ReservaCancelada();

        assertNull(evento.getAggregateId());
        assertNull(evento.pedidoId);
        assertNull(evento.motivo);
    }

    @Test
    @DisplayName("Deve criar ItensSeparados vazio via construtor padrão")
    void deveCriarItensSeparadosVazio() {
        ItensSeparados evento = new ItensSeparados();

        assertNull(evento.getAggregateId());
        assertNull(evento.pedidoId);
        assertNull(evento.enderecoEntrega);
        assertNull(evento.operadorId);
        assertNull(evento.dataSeparacao);
    }

    @Test
    @DisplayName("Deve criar EstoqueReservado com data null e usar data atual")
    void deveCriarEstoqueReservadoComDataNullUsandoDataAtual() {
        Endereco endereco = new Endereco("Rua A", "1", null, "Centro", "SP", "SP", "01000-000");
        List<EstoqueReservado.ItemReservado> itens = Arrays.asList(
                new EstoqueReservado.ItemReservado("PROD-001", 1)
        );

        EstoqueReservado evento = new EstoqueReservado("RES-001", "PED-001", endereco, itens, null);

        assertNotNull(evento.dataReserva);
        assertTrue(evento.dataReserva.isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("Deve criar ItensSeparados com data null e usar data atual")
    void deveCriarItensSeparadosComDataNullUsandoDataAtual() {
        Endereco endereco = new Endereco("Rua A", "1", null, "Centro", "SP", "SP", "01000-000");

        ItensSeparados evento = new ItensSeparados("RES-001", "PED-001", endereco, "OP-001", null);

        assertNotNull(evento.dataSeparacao);
        assertTrue(evento.dataSeparacao.isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}

