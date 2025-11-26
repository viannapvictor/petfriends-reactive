package com.petfriends.almoxarifado.readmodel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do ReservaEstoqueItemView")
class ReservaEstoqueItemViewTest {

    @Test
    @DisplayName("Deve criar item view com builder")
    void deveCriarItemViewComBuilder() {
        UUID id = UUID.randomUUID();

        ReservaEstoqueItemView itemView = ReservaEstoqueItemView.builder()
                .id(id)
                .reservaId("RES-001")
                .produtoId("PROD-001")
                .quantidade(5)
                .build();

        assertEquals(id, itemView.getId());
        assertEquals("RES-001", itemView.getReservaId());
        assertEquals("PROD-001", itemView.getProdutoId());
        assertEquals(5, itemView.getQuantidade());
        assertTrue(itemView.isNew());
    }

    @Test
    @DisplayName("Deve criar item view com construtor padrão")
    void deveCriarItemViewComConstrutorPadrao() {
        ReservaEstoqueItemView itemView = new ReservaEstoqueItemView();

        assertNull(itemView.getId());
        assertTrue(itemView.isNew());
    }

    @Test
    @DisplayName("Deve criar item view com construtor completo")
    void deveCriarItemViewComConstrutorCompleto() {
        UUID id = UUID.randomUUID();

        ReservaEstoqueItemView itemView = new ReservaEstoqueItemView(
                id, true, "RES-001", "PROD-002", 10
        );

        assertEquals(id, itemView.getId());
        assertEquals("RES-001", itemView.getReservaId());
        assertEquals("PROD-002", itemView.getProdutoId());
        assertEquals(10, itemView.getQuantidade());
    }

    @Test
    @DisplayName("Deve modificar campos do item view")
    void deveModificarCamposDoItemView() {
        ReservaEstoqueItemView itemView = ReservaEstoqueItemView.builder()
                .reservaId("RES-001")
                .produtoId("PROD-001")
                .quantidade(5)
                .build();

        itemView.setQuantidade(10);
        itemView.setProdutoId("PROD-999");

        assertEquals(10, itemView.getQuantidade());
        assertEquals("PROD-999", itemView.getProdutoId());
    }

    @Test
    @DisplayName("Deve aceitar quantidade zero")
    void deveAceitarQuantidadeZero() {
        ReservaEstoqueItemView itemView = ReservaEstoqueItemView.builder()
                .reservaId("RES-001")
                .produtoId("PROD-001")
                .quantidade(0)
                .build();

        assertEquals(0, itemView.getQuantidade());
    }

    @Test
    @DisplayName("Deve aceitar quantidade nula")
    void deveAceitarQuantidadeNula() {
        ReservaEstoqueItemView itemView = ReservaEstoqueItemView.builder()
                .reservaId("RES-001")
                .produtoId("PROD-001")
                .quantidade(null)
                .build();

        assertNull(itemView.getQuantidade());
    }

    @Test
    @DisplayName("Deve comparar item views com equals")
    void deveCompararItemViewsComEquals() {
        UUID id = UUID.randomUUID();

        ReservaEstoqueItemView itemView1 = ReservaEstoqueItemView.builder()
                .id(id)
                .reservaId("RES-001")
                .produtoId("PROD-001")
                .quantidade(5)
                .build();

        ReservaEstoqueItemView itemView2 = ReservaEstoqueItemView.builder()
                .id(id)
                .reservaId("RES-001")
                .produtoId("PROD-001")
                .quantidade(5)
                .build();

        assertEquals(itemView1, itemView2);
    }

    @Test
    @DisplayName("Deve gerar hashCode consistente")
    void deveGerarHashCodeConsistente() {
        UUID id = UUID.randomUUID();

        ReservaEstoqueItemView itemView1 = ReservaEstoqueItemView.builder()
                .id(id)
                .reservaId("RES-001")
                .build();

        ReservaEstoqueItemView itemView2 = ReservaEstoqueItemView.builder()
                .id(id)
                .reservaId("RES-001")
                .build();

        assertEquals(itemView1.hashCode(), itemView2.hashCode());
    }
}

