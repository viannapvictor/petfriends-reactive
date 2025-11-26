package com.petfriends.transporte.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do Enum StatusEntrega")
class StatusEntregaTest {

    @Test
    @DisplayName("Deve conter todos os status esperados")
    void deveConterTodosStatusEsperados() {
        StatusEntrega[] statuses = StatusEntrega.values();

        assertEquals(5, statuses.length);
        assertNotNull(StatusEntrega.valueOf("AGENDADA"));
        assertNotNull(StatusEntrega.valueOf("EM_TRANSITO"));
        assertNotNull(StatusEntrega.valueOf("CONCLUIDA"));
        assertNotNull(StatusEntrega.valueOf("DEVOLVIDA"));
        assertNotNull(StatusEntrega.valueOf("EXTRAVIADA"));
    }

    @Test
    @DisplayName("Deve converter string para enum corretamente")
    void deveConverterStringParaEnum() {
        assertEquals(StatusEntrega.AGENDADA, StatusEntrega.valueOf("AGENDADA"));
        assertEquals(StatusEntrega.EM_TRANSITO, StatusEntrega.valueOf("EM_TRANSITO"));
        assertEquals(StatusEntrega.CONCLUIDA, StatusEntrega.valueOf("CONCLUIDA"));
        assertEquals(StatusEntrega.DEVOLVIDA, StatusEntrega.valueOf("DEVOLVIDA"));
        assertEquals(StatusEntrega.EXTRAVIADA, StatusEntrega.valueOf("EXTRAVIADA"));
    }

    @Test
    @DisplayName("Deve lançar exceção para status inválido")
    void deveLancarExcecaoParaStatusInvalido() {
        assertThrows(IllegalArgumentException.class, () -> {
            StatusEntrega.valueOf("INVALIDO");
        });
    }

    @Test
    @DisplayName("Deve retornar nome correto do enum")
    void deveRetornarNomeCorretoDoEnum() {
        assertEquals("AGENDADA", StatusEntrega.AGENDADA.name());
        assertEquals("EM_TRANSITO", StatusEntrega.EM_TRANSITO.name());
        assertEquals("CONCLUIDA", StatusEntrega.CONCLUIDA.name());
        assertEquals("DEVOLVIDA", StatusEntrega.DEVOLVIDA.name());
        assertEquals("EXTRAVIADA", StatusEntrega.EXTRAVIADA.name());
    }

    @Test
    @DisplayName("Deve comparar enums corretamente")
    void deveCompararEnumsCorretamente() {
        assertEquals(StatusEntrega.AGENDADA, StatusEntrega.AGENDADA);
        assertNotEquals(StatusEntrega.AGENDADA, StatusEntrega.EM_TRANSITO);
        assertNotEquals(StatusEntrega.CONCLUIDA, StatusEntrega.DEVOLVIDA);
    }

    @Test
    @DisplayName("Deve retornar ordinal correto")
    void deveRetornarOrdinalCorreto() {
        assertEquals(0, StatusEntrega.AGENDADA.ordinal());
        assertEquals(1, StatusEntrega.EM_TRANSITO.ordinal());
        assertEquals(2, StatusEntrega.CONCLUIDA.ordinal());
        assertEquals(3, StatusEntrega.DEVOLVIDA.ordinal());
        assertEquals(4, StatusEntrega.EXTRAVIADA.ordinal());
    }
}

