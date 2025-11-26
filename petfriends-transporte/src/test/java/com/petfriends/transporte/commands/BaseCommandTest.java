package com.petfriends.transporte.commands;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do BaseCommand")
class BaseCommandTest {

    @Test
    @DisplayName("Deve criar BaseCommand com ID String")
    void deveCriarBaseCommandComIdString() {
        BaseCommand<String> command = new BaseCommand<>("CMD-001");

        assertEquals("CMD-001", command.id);
    }

    @Test
    @DisplayName("Deve criar BaseCommand com ID Integer")
    void deveCriarBaseCommandComIdInteger() {
        BaseCommand<Integer> command = new BaseCommand<>(456);

        assertEquals(456, command.id);
    }

    @Test
    @DisplayName("Deve criar BaseCommand com ID nulo")
    void deveCriarBaseCommandComIdNulo() {
        BaseCommand<String> command = new BaseCommand<>(null);

        assertNull(command.id);
    }

    @Test
    @DisplayName("Deve permitir herança com ID correto")
    void devePermitirHerancaComIdCorreto() {
        class TestCommand extends BaseCommand<String> {
            public final String action;

            public TestCommand(String id, String action) {
                super(id);
                this.action = action;
            }
        }

        TestCommand command = new TestCommand("TEST-001", "execute");

        assertEquals("TEST-001", command.id);
        assertEquals("execute", command.action);
    }
}

