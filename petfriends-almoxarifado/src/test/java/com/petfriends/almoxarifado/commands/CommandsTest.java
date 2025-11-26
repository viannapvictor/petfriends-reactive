package com.petfriends.almoxarifado.commands;

import com.petfriends.almoxarifado.domain.Endereco;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes dos Commands do Almoxarifado")
class CommandsTest {

    @Test
    @DisplayName("Deve criar ReservarEstoqueCommand com todos os campos")
    void deveCriarReservarEstoqueCommand() {
        Endereco endereco = new Endereco("Rua A", "1", null, "Centro", "SP", "SP", "01000-000");
        List<ReservarEstoqueCommand.ItemReservaDTO> itens = Arrays.asList(
                new ReservarEstoqueCommand.ItemReservaDTO("PROD-001", 5),
                new ReservarEstoqueCommand.ItemReservaDTO("PROD-002", 3)
        );

        ReservarEstoqueCommand command = new ReservarEstoqueCommand("RES-001", "PED-001", endereco, itens);

        assertEquals("RES-001", command.id);
        assertEquals("PED-001", command.pedidoId);
        assertEquals(endereco, command.enderecoEntrega);
        assertEquals(2, command.itens.size());
        assertEquals("PROD-001", command.itens.get(0).produtoId);
        assertEquals(5, command.itens.get(0).quantidade);
    }

    @Test
    @DisplayName("Deve criar ItemReservaDTO com campos válidos")
    void deveCriarItemReservaDTO() {
        ReservarEstoqueCommand.ItemReservaDTO item = new ReservarEstoqueCommand.ItemReservaDTO("PROD-100", 10);

        assertEquals("PROD-100", item.produtoId);
        assertEquals(10, item.quantidade);
    }

    @Test
    @DisplayName("Deve criar ConfirmarReservaCommand")
    void deveCriarConfirmarReservaCommand() {
        ConfirmarReservaCommand command = new ConfirmarReservaCommand("RES-001");

        assertEquals("RES-001", command.id);
    }

    @Test
    @DisplayName("Deve criar CancelarReservaCommand")
    void deveCriarCancelarReservaCommand() {
        CancelarReservaCommand command = new CancelarReservaCommand("RES-001", "Cliente cancelou");

        assertEquals("RES-001", command.id);
        assertEquals("Cliente cancelou", command.motivo);
    }

    @Test
    @DisplayName("Deve criar SepararItensCommand")
    void deveCriarSepararItensCommand() {
        SepararItensCommand command = new SepararItensCommand("RES-001", "OP-001");

        assertEquals("RES-001", command.id);
        assertEquals("OP-001", command.operadorId);
    }

    @Test
    @DisplayName("Deve criar ReservarEstoqueCommand com lista vazia de itens")
    void deveCriarReservarEstoqueCommandComListaVazia() {
        Endereco endereco = new Endereco("Rua A", "1", null, "Centro", "SP", "SP", "01000-000");
        List<ReservarEstoqueCommand.ItemReservaDTO> itens = Arrays.asList();

        ReservarEstoqueCommand command = new ReservarEstoqueCommand("RES-001", "PED-001", endereco, itens);

        assertEquals(0, command.itens.size());
    }

    @Test
    @DisplayName("Deve criar ItemReservaDTO com quantidade zero")
    void deveCriarItemReservaDTOComQuantidadeZero() {
        ReservarEstoqueCommand.ItemReservaDTO item = new ReservarEstoqueCommand.ItemReservaDTO("PROD-001", 0);

        assertEquals("PROD-001", item.produtoId);
        assertEquals(0, item.quantidade);
    }

    @Test
    @DisplayName("Deve criar ItemReservaDTO com quantidade negativa")
    void deveCriarItemReservaDTOComQuantidadeNegativa() {
        ReservarEstoqueCommand.ItemReservaDTO item = new ReservarEstoqueCommand.ItemReservaDTO("PROD-001", -5);

        assertEquals("PROD-001", item.produtoId);
        assertEquals(-5, item.quantidade);
    }

    @Test
    @DisplayName("Deve criar CancelarReservaCommand com motivo nulo")
    void deveCriarCancelarReservaCommandComMotivoNulo() {
        CancelarReservaCommand command = new CancelarReservaCommand("RES-001", null);

        assertEquals("RES-001", command.id);
        assertNull(command.motivo);
    }

    @Test
    @DisplayName("Deve criar SepararItensCommand com operadorId nulo")
    void deveCriarSepararItensCommandComOperadorIdNulo() {
        SepararItensCommand command = new SepararItensCommand("RES-001", null);

        assertEquals("RES-001", command.id);
        assertNull(command.operadorId);
    }
}

