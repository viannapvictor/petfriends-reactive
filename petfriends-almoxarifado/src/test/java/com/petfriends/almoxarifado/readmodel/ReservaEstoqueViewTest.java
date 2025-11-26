package com.petfriends.almoxarifado.readmodel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do ReservaEstoqueView")
class ReservaEstoqueViewTest {

    @Test
    @DisplayName("Deve criar view com builder")
    void deveCriarViewComBuilder() {
        LocalDateTime agora = LocalDateTime.now();

        ReservaEstoqueView view = ReservaEstoqueView.builder()
                .id("RES-001")
                .pedidoId("PED-001")
                .status("CONFIRMADA")
                .enderecoRua("Rua A")
                .enderecoNumero("100")
                .enderecoComplemento("Apto 10")
                .enderecoBairro("Centro")
                .enderecoCidade("São Paulo")
                .enderecoEstado("SP")
                .enderecoCep("01000-000")
                .operadorId("OP-001")
                .createdAt(agora)
                .updatedAt(agora)
                .build();

        assertEquals("RES-001", view.getId());
        assertEquals("PED-001", view.getPedidoId());
        assertEquals("CONFIRMADA", view.getStatus());
        assertEquals("Rua A", view.getEnderecoRua());
        assertEquals("100", view.getEnderecoNumero());
        assertEquals("OP-001", view.getOperadorId());
    }

    @Test
    @DisplayName("Deve marcar view como existente")
    void deveMarcarViewComoExistente() {
        ReservaEstoqueView view = ReservaEstoqueView.builder()
                .id("RES-001")
                .build();

        assertTrue(view.isNew());

        view.markAsExisting();

        assertFalse(view.isNew());
    }

    @Test
    @DisplayName("Deve criar view com construtor padrão")
    void deveCriarViewComConstrutorPadrao() {
        ReservaEstoqueView view = new ReservaEstoqueView();

        assertNull(view.getId());
        assertTrue(view.isNew());
    }

    @Test
    @DisplayName("Deve criar view com construtor completo")
    void deveCriarViewComConstrutorCompleto() {
        LocalDateTime agora = LocalDateTime.now();

        ReservaEstoqueView view = new ReservaEstoqueView(
                "RES-001", true, "PED-001", "SEPARADA",
                "Rua B", "200", null, "Jardim", "Campinas", "SP", "13000-000",
                "OP-002", agora, agora
        );

        assertEquals("RES-001", view.getId());
        assertEquals("PED-001", view.getPedidoId());
        assertEquals("SEPARADA", view.getStatus());
        assertNull(view.getEnderecoComplemento());
    }

    @Test
    @DisplayName("Deve modificar campos da view")
    void deveModificarCamposDaView() {
        ReservaEstoqueView view = ReservaEstoqueView.builder()
                .id("RES-001")
                .status("PENDENTE")
                .build();

        view.setStatus("CONFIRMADA");
        view.setOperadorId("OP-100");

        assertEquals("CONFIRMADA", view.getStatus());
        assertEquals("OP-100", view.getOperadorId());
    }

    @Test
    @DisplayName("Deve comparar views com equals")
    void deveCompararViewsComEquals() {
        ReservaEstoqueView view1 = ReservaEstoqueView.builder()
                .id("RES-001")
                .pedidoId("PED-001")
                .status("CONFIRMADA")
                .build();

        ReservaEstoqueView view2 = ReservaEstoqueView.builder()
                .id("RES-001")
                .pedidoId("PED-001")
                .status("CONFIRMADA")
                .build();

        assertEquals(view1, view2);
    }

    @Test
    @DisplayName("Deve gerar hashCode consistente")
    void deveGerarHashCodeConsistente() {
        ReservaEstoqueView view1 = ReservaEstoqueView.builder()
                .id("RES-001")
                .pedidoId("PED-001")
                .build();

        ReservaEstoqueView view2 = ReservaEstoqueView.builder()
                .id("RES-001")
                .pedidoId("PED-001")
                .build();

        assertEquals(view1.hashCode(), view2.hashCode());
    }
}

