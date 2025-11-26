package com.petfriends.almoxarifado.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do BaseEvent")
class BaseEventTest {

    @Test
    @DisplayName("Deve criar BaseEvent com ID String")
    void deveCriarBaseEventComIdString() {
        BaseEvent<String> event = new BaseEvent<>("EVT-001");

        assertEquals("EVT-001", event.getAggregateId());
        assertEquals("EVT-001", event.id);
    }

    @Test
    @DisplayName("Deve criar BaseEvent com ID Integer")
    void deveCriarBaseEventComIdInteger() {
        BaseEvent<Integer> event = new BaseEvent<>(123);

        assertEquals(123, event.getAggregateId());
        assertEquals(123, event.id);
    }

    @Test
    @DisplayName("Deve criar BaseEvent com construtor padrão")
    void deveCriarBaseEventComConstrutorPadrao() {
        BaseEvent<String> event = new BaseEvent<>();

        assertNull(event.getAggregateId());
        assertNull(event.id);
    }

    @Test
    @DisplayName("Deve criar BaseEvent com ID nulo")
    void deveCriarBaseEventComIdNulo() {
        BaseEvent<String> event = new BaseEvent<>(null);

        assertNull(event.getAggregateId());
        assertNull(event.id);
    }

    @Test
    @DisplayName("Deve permitir herança e retornar ID correto")
    void devePermitirHerancaERetornarIdCorreto() {
        class TestEvent extends BaseEvent<String> {
            public final String data;

            public TestEvent(String id, String data) {
                super(id);
                this.data = data;
            }
        }

        TestEvent event = new TestEvent("TEST-001", "test data");

        assertEquals("TEST-001", event.getAggregateId());
        assertEquals("test data", event.data);
    }
}

