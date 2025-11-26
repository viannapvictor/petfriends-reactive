package com.petfriends.almoxarifado.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do Enum StatusReserva")
class StatusReservaTest {

    @Test
    @DisplayName("Deve conter todos os status esperados")
    void deveConterTodosStatusEsperados() {
        StatusReserva[] statuses = StatusReserva.values();

        assertEquals(6, statuses.length);
        assertNotNull(StatusReserva.valueOf("PENDENTE"));
        assertNotNull(StatusReserva.valueOf("CONFIRMADA"));
        assertNotNull(StatusReserva.valueOf("INSUFICIENTE"));
        assertNotNull(StatusReserva.valueOf("EM_SEPARACAO"));
        assertNotNull(StatusReserva.valueOf("SEPARADA"));
        assertNotNull(StatusReserva.valueOf("CANCELADA"));
    }

    @Test
    @DisplayName("Deve converter string para enum corretamente")
    void deveConverterStringParaEnum() {
        assertEquals(StatusReserva.PENDENTE, StatusReserva.valueOf("PENDENTE"));
        assertEquals(StatusReserva.CONFIRMADA, StatusReserva.valueOf("CONFIRMADA"));
        assertEquals(StatusReserva.INSUFICIENTE, StatusReserva.valueOf("INSUFICIENTE"));
        assertEquals(StatusReserva.EM_SEPARACAO, StatusReserva.valueOf("EM_SEPARACAO"));
        assertEquals(StatusReserva.SEPARADA, StatusReserva.valueOf("SEPARADA"));
        assertEquals(StatusReserva.CANCELADA, StatusReserva.valueOf("CANCELADA"));
    }

    @Test
    @DisplayName("Deve lançar exceção para status inválido")
    void deveLancarExcecaoParaStatusInvalido() {
        assertThrows(IllegalArgumentException.class, () -> {
            StatusReserva.valueOf("INVALIDO");
        });
    }

    @Test
    @DisplayName("Deve retornar nome correto do enum")
    void deveRetornarNomeCorretoDoEnum() {
        assertEquals("PENDENTE", StatusReserva.PENDENTE.name());
        assertEquals("CONFIRMADA", StatusReserva.CONFIRMADA.name());
        assertEquals("INSUFICIENTE", StatusReserva.INSUFICIENTE.name());
        assertEquals("EM_SEPARACAO", StatusReserva.EM_SEPARACAO.name());
        assertEquals("SEPARADA", StatusReserva.SEPARADA.name());
        assertEquals("CANCELADA", StatusReserva.CANCELADA.name());
    }

    @Test
    @DisplayName("Deve comparar enums corretamente")
    void deveCompararEnumsCorretamente() {
        assertEquals(StatusReserva.PENDENTE, StatusReserva.PENDENTE);
        assertNotEquals(StatusReserva.PENDENTE, StatusReserva.CONFIRMADA);
        assertNotEquals(StatusReserva.SEPARADA, StatusReserva.CANCELADA);
    }

    @Test
    @DisplayName("Deve retornar ordinal correto")
    void deveRetornarOrdinalCorreto() {
        assertEquals(0, StatusReserva.PENDENTE.ordinal());
        assertEquals(1, StatusReserva.CONFIRMADA.ordinal());
        assertEquals(2, StatusReserva.INSUFICIENTE.ordinal());
        assertEquals(3, StatusReserva.EM_SEPARACAO.ordinal());
        assertEquals(4, StatusReserva.SEPARADA.ordinal());
        assertEquals(5, StatusReserva.CANCELADA.ordinal());
    }
}

