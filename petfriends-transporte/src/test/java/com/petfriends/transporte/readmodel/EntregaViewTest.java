package com.petfriends.transporte.readmodel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do EntregaView")
class EntregaViewTest {

    @Test
    @DisplayName("Deve criar view com builder")
    void deveCriarViewComBuilder() {
        LocalDateTime agora = LocalDateTime.now();

        EntregaView view = EntregaView.builder()
                .id("ENT-001")
                .pedidoId("PED-001")
                .reservaId("RES-001")
                .status("EM_TRANSITO")
                .enderecoCompleto("Rua A, 100 - Centro, SP/SP")
                .dataPrevisaoEntrega("2025-12-25")
                .motoristaId("MOT-001")
                .veiculoId("VEI-001")
                .recebedor("João Silva")
                .dataHoraRecebimento("2025-12-25T14:30:00")
                .motivoDevolucao(null)
                .dataDevolucao(null)
                .responsavelDevolucao(null)
                .motivoExtravio(null)
                .dataExtravio(null)
                .localUltimoRegistro(null)
                .createdAt(agora)
                .updatedAt(agora)
                .build();

        assertEquals("ENT-001", view.getId());
        assertEquals("PED-001", view.getPedidoId());
        assertEquals("RES-001", view.getReservaId());
        assertEquals("EM_TRANSITO", view.getStatus());
        assertEquals("MOT-001", view.getMotoristaId());
    }

    @Test
    @DisplayName("Deve marcar view como existente")
    void deveMarcarViewComoExistente() {
        EntregaView view = EntregaView.builder()
                .id("ENT-001")
                .build();

        assertTrue(view.isNew());

        view.markAsExisting();

        assertFalse(view.isNew());
    }

    @Test
    @DisplayName("Deve criar view com construtor padrão")
    void deveCriarViewComConstrutorPadrao() {
        EntregaView view = new EntregaView();

        assertNull(view.getId());
        assertTrue(view.isNew());
    }

    @Test
    @DisplayName("Deve criar view para entrega agendada")
    void deveCriarViewParaEntregaAgendada() {
        LocalDateTime agora = LocalDateTime.now();

        EntregaView view = EntregaView.builder()
                .id("ENT-001")
                .pedidoId("PED-001")
                .reservaId("RES-001")
                .status("AGENDADA")
                .enderecoCompleto("Av Paulista, 1000 - Bela Vista, São Paulo/SP")
                .dataPrevisaoEntrega("2025-12-30")
                .createdAt(agora)
                .updatedAt(agora)
                .build();

        assertEquals("AGENDADA", view.getStatus());
        assertNull(view.getMotoristaId());
        assertNull(view.getVeiculoId());
    }

    @Test
    @DisplayName("Deve criar view para entrega concluída")
    void deveCriarViewParaEntregaConcluida() {
        EntregaView view = EntregaView.builder()
                .id("ENT-001")
                .status("CONCLUIDA")
                .recebedor("Maria Santos")
                .dataHoraRecebimento("2025-12-25T10:30:00")
                .build();

        assertEquals("CONCLUIDA", view.getStatus());
        assertEquals("Maria Santos", view.getRecebedor());
        assertEquals("2025-12-25T10:30:00", view.getDataHoraRecebimento());
    }

    @Test
    @DisplayName("Deve criar view para entrega devolvida")
    void deveCriarViewParaEntregaDevolvida() {
        EntregaView view = EntregaView.builder()
                .id("ENT-001")
                .status("DEVOLVIDA")
                .motivoDevolucao("Cliente ausente")
                .dataDevolucao("2025-12-25T15:00:00")
                .responsavelDevolucao("MOT-001")
                .build();

        assertEquals("DEVOLVIDA", view.getStatus());
        assertEquals("Cliente ausente", view.getMotivoDevolucao());
        assertEquals("MOT-001", view.getResponsavelDevolucao());
    }

    @Test
    @DisplayName("Deve criar view para entrega extraviada")
    void deveCriarViewParaEntregaExtraviada() {
        EntregaView view = EntregaView.builder()
                .id("ENT-001")
                .status("EXTRAVIADA")
                .motivoExtravio("Pacote perdido")
                .dataExtravio("2025-12-25T12:00:00")
                .localUltimoRegistro("Centro de Distribuição")
                .build();

        assertEquals("EXTRAVIADA", view.getStatus());
        assertEquals("Pacote perdido", view.getMotivoExtravio());
        assertEquals("Centro de Distribuição", view.getLocalUltimoRegistro());
    }

    @Test
    @DisplayName("Deve modificar campos da view")
    void deveModificarCamposDaView() {
        EntregaView view = EntregaView.builder()
                .id("ENT-001")
                .status("AGENDADA")
                .build();

        view.setStatus("EM_TRANSITO");
        view.setMotoristaId("MOT-100");
        view.setVeiculoId("VEI-100");

        assertEquals("EM_TRANSITO", view.getStatus());
        assertEquals("MOT-100", view.getMotoristaId());
        assertEquals("VEI-100", view.getVeiculoId());
    }

    @Test
    @DisplayName("Deve comparar views com equals")
    void deveCompararViewsComEquals() {
        EntregaView view1 = EntregaView.builder()
                .id("ENT-001")
                .pedidoId("PED-001")
                .status("AGENDADA")
                .build();

        EntregaView view2 = EntregaView.builder()
                .id("ENT-001")
                .pedidoId("PED-001")
                .status("AGENDADA")
                .build();

        assertEquals(view1, view2);
    }

    @Test
    @DisplayName("Deve gerar hashCode consistente")
    void deveGerarHashCodeConsistente() {
        EntregaView view1 = EntregaView.builder()
                .id("ENT-001")
                .pedidoId("PED-001")
                .build();

        EntregaView view2 = EntregaView.builder()
                .id("ENT-001")
                .pedidoId("PED-001")
                .build();

        assertEquals(view1.hashCode(), view2.hashCode());
    }
}

