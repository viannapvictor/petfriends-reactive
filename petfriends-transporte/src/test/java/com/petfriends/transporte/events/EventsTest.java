package com.petfriends.transporte.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes dos Events do Transporte")
class EventsTest {

    @Test
    @DisplayName("Deve criar EntregaAgendada com todos os campos")
    void deveCriarEntregaAgendada() {
        LocalDate dataPrevisao = LocalDate.of(2025, 12, 25);

        EntregaAgendada evento = new EntregaAgendada(
                "ENT-001", "PED-001", "RES-001", 
                "Rua A, 100 - Centro, SP/SP", dataPrevisao
        );

        assertEquals("ENT-001", evento.getAggregateId());
        assertEquals("PED-001", evento.pedidoId);
        assertEquals("RES-001", evento.reservaId);
        assertEquals("Rua A, 100 - Centro, SP/SP", evento.enderecoCompleto);
        assertEquals(dataPrevisao, evento.dataPrevisao);
    }

    @Test
    @DisplayName("Deve criar EntregaAgendada vazia via construtor padrão")
    void deveCriarEntregaAgendadaVazia() {
        EntregaAgendada evento = new EntregaAgendada();

        assertNull(evento.getAggregateId());
        assertNull(evento.pedidoId);
        assertNull(evento.reservaId);
        assertNull(evento.enderecoCompleto);
        assertNull(evento.dataPrevisao);
    }

    @Test
    @DisplayName("Deve criar TransporteIniciado com todos os campos")
    void deveCriarTransporteIniciado() {
        LocalDateTime dataSaida = LocalDateTime.now();

        TransporteIniciado evento = new TransporteIniciado(
                "ENT-001", "MOT-001", "VEI-001", dataSaida
        );

        assertEquals("ENT-001", evento.getAggregateId());
        assertEquals("MOT-001", evento.motoristaId);
        assertEquals("VEI-001", evento.veiculoId);
        assertEquals(dataSaida, evento.dataHoraSaida);
    }

    @Test
    @DisplayName("Deve criar TransporteIniciado sem data e usar data atual")
    void deveCriarTransporteIniciadoSemData() {
        TransporteIniciado evento = new TransporteIniciado("ENT-001", "MOT-001", "VEI-001");

        assertNotNull(evento.dataHoraSaida);
        assertEquals("ENT-001", evento.getAggregateId());
    }

    @Test
    @DisplayName("Deve criar TransporteIniciado com data null e usar data atual")
    void deveCriarTransporteIniciadoComDataNullUsandoDataAtual() {
        TransporteIniciado evento = new TransporteIniciado("ENT-001", "MOT-001", "VEI-001", null);

        assertNotNull(evento.dataHoraSaida);
        assertTrue(evento.dataHoraSaida.isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("Deve criar TransporteIniciado vazio via construtor padrão")
    void deveCriarTransporteIniciadoVazio() {
        TransporteIniciado evento = new TransporteIniciado();

        assertNull(evento.getAggregateId());
        assertNull(evento.motoristaId);
        assertNull(evento.veiculoId);
        assertNull(evento.dataHoraSaida);
    }

    @Test
    @DisplayName("Deve criar EntregaConcluida com todos os campos")
    void deveCriarEntregaConcluida() {
        LocalDateTime dataRecebimento = LocalDateTime.now();

        EntregaConcluida evento = new EntregaConcluida(
                "ENT-001", "PED-001", "João Silva", dataRecebimento, "Entregue com sucesso"
        );

        assertEquals("ENT-001", evento.getAggregateId());
        assertEquals("PED-001", evento.pedidoId);
        assertEquals("João Silva", evento.recebedor);
        assertEquals(dataRecebimento, evento.dataHoraRecebimento);
        assertEquals("Entregue com sucesso", evento.observacoes);
    }

    @Test
    @DisplayName("Deve criar EntregaConcluida vazia via construtor padrão")
    void deveCriarEntregaConcluidaVazia() {
        EntregaConcluida evento = new EntregaConcluida();

        assertNull(evento.getAggregateId());
        assertNull(evento.pedidoId);
        assertNull(evento.recebedor);
        assertNull(evento.dataHoraRecebimento);
        assertNull(evento.observacoes);
    }

    @Test
    @DisplayName("Deve criar EntregaDevolvida com todos os campos")
    void deveCriarEntregaDevolvida() {
        LocalDateTime dataDevolucao = LocalDateTime.now();

        EntregaDevolvida evento = new EntregaDevolvida(
                "ENT-001", "PED-001", "Cliente ausente", dataDevolucao, "MOT-001"
        );

        assertEquals("ENT-001", evento.getAggregateId());
        assertEquals("PED-001", evento.pedidoId);
        assertEquals("Cliente ausente", evento.motivo);
        assertEquals(dataDevolucao, evento.dataDevolucao);
        assertEquals("MOT-001", evento.responsavel);
    }

    @Test
    @DisplayName("Deve criar EntregaDevolvida vazia via construtor padrão")
    void deveCriarEntregaDevolvidaVazia() {
        EntregaDevolvida evento = new EntregaDevolvida();

        assertNull(evento.getAggregateId());
        assertNull(evento.pedidoId);
        assertNull(evento.motivo);
        assertNull(evento.dataDevolucao);
        assertNull(evento.responsavel);
    }

    @Test
    @DisplayName("Deve criar EntregaExtraviada com todos os campos")
    void deveCriarEntregaExtraviada() {
        LocalDateTime dataExtravio = LocalDateTime.now();

        EntregaExtraviada evento = new EntregaExtraviada(
                "ENT-001", "PED-001", "Pacote perdido", dataExtravio, "Centro de Distribuição"
        );

        assertEquals("ENT-001", evento.getAggregateId());
        assertEquals("PED-001", evento.pedidoId);
        assertEquals("Pacote perdido", evento.motivo);
        assertEquals(dataExtravio, evento.dataExtravio);
        assertEquals("Centro de Distribuição", evento.localUltimoRegistro);
    }

    @Test
    @DisplayName("Deve criar EntregaExtraviada vazia via construtor padrão")
    void deveCriarEntregaExtraviadaVazia() {
        EntregaExtraviada evento = new EntregaExtraviada();

        assertNull(evento.getAggregateId());
        assertNull(evento.pedidoId);
        assertNull(evento.motivo);
        assertNull(evento.dataExtravio);
        assertNull(evento.localUltimoRegistro);
    }

    @Test
    @DisplayName("Deve criar EntregaAgendada com endereço completo nulo")
    void deveCriarEntregaAgendadaComEnderecoNulo() {
        EntregaAgendada evento = new EntregaAgendada(
                "ENT-001", "PED-001", "RES-001", null, LocalDate.now()
        );

        assertNull(evento.enderecoCompleto);
        assertEquals("ENT-001", evento.getAggregateId());
    }

    @Test
    @DisplayName("Deve criar EntregaConcluida sem observações")
    void deveCriarEntregaConcluidaSemObservacoes() {
        LocalDateTime dataRecebimento = LocalDateTime.now();

        EntregaConcluida evento = new EntregaConcluida(
                "ENT-001", "PED-001", "Maria Santos", dataRecebimento, null
        );

        assertNull(evento.observacoes);
        assertEquals("Maria Santos", evento.recebedor);
    }

    @Test
    @DisplayName("Deve criar TransporteIniciado com todos os campos preenchidos")
    void deveCriarTransporteIniciadoCompleto() {
        LocalDateTime dataSaida = LocalDateTime.of(2025, 12, 25, 10, 30);

        TransporteIniciado evento = new TransporteIniciado(
                "ENT-001", "MOT-123", "VEI-ABC-1234", dataSaida
        );

        assertEquals("ENT-001", evento.getAggregateId());
        assertEquals("MOT-123", evento.motoristaId);
        assertEquals("VEI-ABC-1234", evento.veiculoId);
        assertEquals(dataSaida, evento.dataHoraSaida);
    }
}

