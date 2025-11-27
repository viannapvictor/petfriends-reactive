package com.petfriends.almoxarifado.eventstore;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do EventStoreEntry")
class EventStoreEntryTest {

    @Test
    @DisplayName("Deve criar EventStoreEntry com builder")
    void deveCriarEventStoreEntryComBuilder() {
        UUID id = UUID.randomUUID();
        LocalDateTime timestamp = LocalDateTime.now();

        EventStoreEntry entry = EventStoreEntry.builder()
                .id(id)
                .aggregateId("AGG-001")
                .aggregateType("ReservaEstoque")
                .eventType("EstoqueReservado")
                .eventData("{\"id\":\"RES-001\"}")
                .version(1L)
                .timestamp(timestamp)
                .metadata("{\"user\":\"system\"}")
                .build();

        assertEquals(id, entry.getId());
        assertEquals("AGG-001", entry.getAggregateId());
        assertEquals("ReservaEstoque", entry.getAggregateType());
        assertEquals("EstoqueReservado", entry.getEventType());
        assertEquals("{\"id\":\"RES-001\"}", entry.getEventData());
        assertEquals(1L, entry.getVersion());
        assertEquals(timestamp, entry.getTimestamp());
        assertEquals("{\"user\":\"system\"}", entry.getMetadata());
        assertTrue(entry.isNew());
    }

    @Test
    @DisplayName("Deve criar EventStoreEntry com construtor padrão")
    void deveCriarEventStoreEntryComConstrutorPadrao() {
        EventStoreEntry entry = new EventStoreEntry();

        assertNull(entry.getId());
        assertTrue(entry.isNew());
    }

    @Test
    @DisplayName("Deve criar EventStoreEntry com construtor completo")
    void deveCriarEventStoreEntryComConstrutorCompleto() {
        UUID id = UUID.randomUUID();
        LocalDateTime timestamp = LocalDateTime.now();

        EventStoreEntry entry = new EventStoreEntry(
                id, true, "AGG-002", "Entrega", "EntregaAgendada",
                "{\"id\":\"ENT-001\"}", 2L, timestamp, null
        );

        assertEquals(id, entry.getId());
        assertEquals("AGG-002", entry.getAggregateId());
        assertEquals("Entrega", entry.getAggregateType());
        assertEquals("EntregaAgendada", entry.getEventType());
        assertEquals(2L, entry.getVersion());
        assertNull(entry.getMetadata());
    }

    @Test
    @DisplayName("Deve criar EventStoreEntry sem metadata")
    void deveCriarEventStoreEntrySemMetadata() {
        EventStoreEntry entry = EventStoreEntry.builder()
                .aggregateId("AGG-001")
                .aggregateType("ReservaEstoque")
                .eventType("EstoqueReservado")
                .eventData("{}")
                .version(1L)
                .timestamp(LocalDateTime.now())
                .build();

        assertNull(entry.getMetadata());
    }

    @Test
    @DisplayName("Deve modificar campos do EventStoreEntry")
    void deveModificarCamposDoEventStoreEntry() {
        EventStoreEntry entry = EventStoreEntry.builder()
                .aggregateId("AGG-001")
                .version(1L)
                .build();

        entry.setVersion(2L);
        entry.setMetadata("{\"updated\":true}");

        assertEquals(2L, entry.getVersion());
        assertEquals("{\"updated\":true}", entry.getMetadata());
    }

    @Test
    @DisplayName("Deve criar EventStoreEntry com versão zero")
    void deveCriarEventStoreEntryComVersaoZero() {
        EventStoreEntry entry = EventStoreEntry.builder()
                .aggregateId("AGG-001")
                .version(0L)
                .build();

        assertEquals(0L, entry.getVersion());
    }

    @Test
    @DisplayName("Deve comparar EventStoreEntry com equals")
    void deveCompararEventStoreEntryComEquals() {
        UUID id = UUID.randomUUID();

        EventStoreEntry entry1 = EventStoreEntry.builder()
                .id(id)
                .aggregateId("AGG-001")
                .version(1L)
                .build();

        EventStoreEntry entry2 = EventStoreEntry.builder()
                .id(id)
                .aggregateId("AGG-001")
                .version(1L)
                .build();

        assertEquals(entry1, entry2);
    }

    @Test
    @DisplayName("Deve gerar hashCode consistente")
    void deveGerarHashCodeConsistente() {
        UUID id = UUID.randomUUID();

        EventStoreEntry entry1 = EventStoreEntry.builder()
                .id(id)
                .aggregateId("AGG-001")
                .build();

        EventStoreEntry entry2 = EventStoreEntry.builder()
                .id(id)
                .aggregateId("AGG-001")
                .build();

        assertEquals(entry1.hashCode(), entry2.hashCode());
    }

    @Test
    @DisplayName("Deve aceitar eventData JSON complexo")
    void deveAceitarEventDataJsonComplexo() {
        String complexJson = "{\"id\":\"RES-001\",\"items\":[{\"produtoId\":\"PROD-001\",\"quantidade\":5}]}";

        EventStoreEntry entry = EventStoreEntry.builder()
                .aggregateId("AGG-001")
                .eventData(complexJson)
                .build();

        assertEquals(complexJson, entry.getEventData());
    }
}

