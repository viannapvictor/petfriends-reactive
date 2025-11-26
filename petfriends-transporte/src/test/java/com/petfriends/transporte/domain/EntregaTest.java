package com.petfriends.transporte.domain;

import com.petfriends.transporte.commands.*;
import com.petfriends.transporte.events.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do Agregado Entrega")
class EntregaTest {

    private Entrega entrega;
    private AgendarEntregaCommand.EnderecoDTO enderecoTeste;

    @BeforeEach
    void setUp() {
        entrega = new Entrega("ENT-001");
        enderecoTeste = new AgendarEntregaCommand.EnderecoDTO(
                "Av Paulista", "1000", "Conj 42", "Bela Vista", "São Paulo", "SP", "01310-100"
        );
    }

    @Test
    @DisplayName("Deve criar entrega vazia com construtor padrão")
    void deveCriarEntregaVazia() {
        Entrega novaEntrega = new Entrega();

        assertNotNull(novaEntrega);
    }

    @Test
    @DisplayName("Deve criar entrega com ID")
    void deveCriarEntregaComId() {
        Entrega novaEntrega = new Entrega("ENT-002");

        assertEquals("ENT-002", novaEntrega.getId());
    }

    @Test
    @DisplayName("Deve agendar entrega com sucesso")
    void deveAgendarEntregaComSucesso() {

        LocalDate dataPrevisao = LocalDate.of(2025, 12, 1);
        AgendarEntregaCommand comando = new AgendarEntregaCommand(
                "ENT-001",
                "PED-001",
                "RES-001",
                enderecoTeste,
                dataPrevisao
        );

        BaseEvent<?> evento = entrega.agendarEntrega(comando);

        assertInstanceOf(EntregaAgendada.class, evento);
        EntregaAgendada entregaAgendada = (EntregaAgendada) evento;
        assertEquals("ENT-001", entregaAgendada.id);
        assertEquals("PED-001", entregaAgendada.pedidoId);
        assertEquals("RES-001", entregaAgendada.reservaId);
        assertTrue(entregaAgendada.enderecoCompleto.contains("Av Paulista"));
        assertTrue(entregaAgendada.enderecoCompleto.contains("1000"));
        assertTrue(entregaAgendada.enderecoCompleto.contains("São Paulo"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao agendar entrega sem endereço")
    void deveLancarExcecaoAoAgendarSemEndereco() {

        AgendarEntregaCommand comando = new AgendarEntregaCommand(
                "ENT-001",
                "PED-001",
                "RES-001",
                null,
                LocalDate.now()
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            entrega.agendarEntrega(comando);
        });

        assertEquals("Endereço de entrega é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve formatar endereço corretamente sem complemento")
    void deveFormatarEnderecoSemComplemento() {
        AgendarEntregaCommand.EnderecoDTO enderecoSemComplemento = new AgendarEntregaCommand.EnderecoDTO(
                "Rua A", "100", null, "Centro", "Rio de Janeiro", "RJ", "20000-000"
        );
        AgendarEntregaCommand comando = new AgendarEntregaCommand(
                "ENT-001",
                "PED-001",
                "RES-001",
                enderecoSemComplemento,
                LocalDate.now()
        );

        BaseEvent<?> evento = entrega.agendarEntrega(comando);

        EntregaAgendada entregaAgendada = (EntregaAgendada) evento;
        assertFalse(entregaAgendada.enderecoCompleto.contains("null"));
    }

    @Test
    @DisplayName("Deve iniciar transporte com sucesso")
    void deveIniciarTransporteComSucesso() {

        entrega.setStatus(StatusEntrega.AGENDADA.toString());
        IniciarTransporteCommand comando = new IniciarTransporteCommand("ENT-001", "MOT-001", "VEI-001");

        BaseEvent<?> evento = entrega.iniciarTransporte(comando);

        assertInstanceOf(TransporteIniciado.class, evento);
        TransporteIniciado transporteIniciado = (TransporteIniciado) evento;
        assertEquals("ENT-001", transporteIniciado.id);
        assertEquals("MOT-001", transporteIniciado.motoristaId);
        assertEquals("VEI-001", transporteIniciado.veiculoId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao iniciar transporte de entrega não agendada")
    void deveLancarExcecaoAoIniciarTransporteNaoAgendado() {

        entrega.setStatus(StatusEntrega.EM_TRANSITO.toString());
        IniciarTransporteCommand comando = new IniciarTransporteCommand("ENT-001", "MOT-001", "VEI-001");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            entrega.iniciarTransporte(comando);
        });

        assertEquals("Só é possível iniciar transporte de entregas agendadas", exception.getMessage());
    }

    @Test
    @DisplayName("Deve concluir entrega com sucesso")
    void deveConcluirEntregaComSucesso() {

        entrega.setStatus(StatusEntrega.EM_TRANSITO.toString());
        entrega.setPedidoId("PED-001");
        LocalDateTime dataRecebimento = LocalDateTime.of(2025, 12, 1, 14, 30);
        ConcluirEntregaCommand comando = new ConcluirEntregaCommand(
                "ENT-001",
                "João Silva",
                dataRecebimento,
                "Entregue com sucesso"
        );

        BaseEvent<?> evento = entrega.concluirEntrega(comando);

        assertInstanceOf(EntregaConcluida.class, evento);
        EntregaConcluida entregaConcluida = (EntregaConcluida) evento;
        assertEquals("ENT-001", entregaConcluida.id);
        assertEquals("PED-001", entregaConcluida.pedidoId);
        assertEquals("João Silva", entregaConcluida.recebedor);
    }

    @Test
    @DisplayName("Deve lançar exceção ao concluir entrega que não está em trânsito")
    void deveLancarExcecaoAoConcluirEntregaNaoEmTransito() {

        entrega.setStatus(StatusEntrega.AGENDADA.toString());
        ConcluirEntregaCommand comando = new ConcluirEntregaCommand(
                "ENT-001",
                "João Silva",
                LocalDateTime.now(),
                "Observações"
        );

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            entrega.concluirEntrega(comando);
        });

        assertEquals("Só é possível concluir entregas em trânsito", exception.getMessage());
    }

    @Test
    @DisplayName("Deve devolver entrega com sucesso")
    void deveDevolverEntregaComSucesso() {

        entrega.setStatus(StatusEntrega.CONCLUIDA.toString());
        entrega.setPedidoId("PED-001");
        LocalDateTime dataDevolucao = LocalDateTime.of(2025, 12, 2, 16, 0);
        DevolverEntregaCommand comando = new DevolverEntregaCommand(
                "ENT-001",
                "Cliente recusou",
                dataDevolucao,
                "MOT-001"
        );

        BaseEvent<?> evento = entrega.devolverEntrega(comando);

        assertInstanceOf(EntregaDevolvida.class, evento);
        EntregaDevolvida entregaDevolvida = (EntregaDevolvida) evento;
        assertEquals("ENT-001", entregaDevolvida.id);
        assertEquals("PED-001", entregaDevolvida.pedidoId);
        assertEquals("Cliente recusou", entregaDevolvida.motivo);
        assertEquals("MOT-001", entregaDevolvida.responsavel);
    }

    @Test
    @DisplayName("Deve lançar exceção ao devolver entrega não concluída")
    void deveLancarExcecaoAoDevolverEntregaNaoConcluida() {

        entrega.setStatus(StatusEntrega.EM_TRANSITO.toString());
        DevolverEntregaCommand comando = new DevolverEntregaCommand(
                "ENT-001",
                "Motivo",
                LocalDateTime.now(),
                "MOT-001"
        );

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            entrega.devolverEntrega(comando);
        });

        assertEquals("Só é possível devolver entregas concluídas", exception.getMessage());
    }

    @Test
    @DisplayName("Deve marcar entrega como extraviada com sucesso")
    void deveMarcarEntregaExtraviadaComSucesso() {

        entrega.setStatus(StatusEntrega.EM_TRANSITO.toString());
        entrega.setPedidoId("PED-001");
        LocalDateTime dataExtravio = LocalDateTime.of(2025, 12, 1, 18, 0);
        MarcarEntregaExtraviadaCommand comando = new MarcarEntregaExtraviadaCommand(
                "ENT-001",
                "Acidente na rodovia",
                dataExtravio,
                "Rodovia Anhanguera KM 45"
        );

        BaseEvent<?> evento = entrega.marcarExtraviada(comando);

        assertInstanceOf(EntregaExtraviada.class, evento);
        EntregaExtraviada entregaExtraviada = (EntregaExtraviada) evento;
        assertEquals("ENT-001", entregaExtraviada.id);
        assertEquals("PED-001", entregaExtraviada.pedidoId);
        assertEquals("Acidente na rodovia", entregaExtraviada.motivo);
        assertEquals("Rodovia Anhanguera KM 45", entregaExtraviada.localUltimoRegistro);
    }

    @Test
    @DisplayName("Deve lançar exceção ao marcar como extraviada entrega que não está em trânsito")
    void deveLancarExcecaoAoMarcarExtraviadaNaoEmTransito() {

        entrega.setStatus(StatusEntrega.AGENDADA.toString());
        MarcarEntregaExtraviadaCommand comando = new MarcarEntregaExtraviadaCommand(
                "ENT-001",
                "Motivo",
                LocalDateTime.now(),
                "Local"
        );

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            entrega.marcarExtraviada(comando);
        });

        assertEquals("Só é possível marcar como extraviada entregas em trânsito", exception.getMessage());
    }

    @Test
    @DisplayName("Deve aplicar evento EntregaAgendada corretamente")
    void deveAplicarEventoEntregaAgendada() {

        LocalDate dataPrevisao = LocalDate.of(2025, 12, 1);
        EntregaAgendada evento = new EntregaAgendada(
                "ENT-001",
                "PED-001",
                "RES-001",
                "Av Paulista, 1000",
                dataPrevisao
        );

        entrega.apply(evento);

        assertEquals("ENT-001", entrega.getId());
        assertEquals("PED-001", entrega.getPedidoId());
        assertEquals("RES-001", entrega.getReservaId());
        assertEquals(StatusEntrega.AGENDADA.toString(), entrega.getStatus());
        assertEquals("Av Paulista, 1000", entrega.getEnderecoCompleto());
        assertEquals(dataPrevisao.toString(), entrega.getDataPrevisaoEntrega());
    }

    @Test
    @DisplayName("Deve aplicar evento TransporteIniciado corretamente")
    void deveAplicarEventoTransporteIniciado() {

        TransporteIniciado evento = new TransporteIniciado("ENT-001", "MOT-001", "VEI-001");

        entrega.apply(evento);

        assertEquals(StatusEntrega.EM_TRANSITO.toString(), entrega.getStatus());
        assertEquals("MOT-001", entrega.getMotoristaId());
        assertEquals("VEI-001", entrega.getVeiculoId());
    }

    @Test
    @DisplayName("Deve aplicar evento EntregaConcluida corretamente")
    void deveAplicarEventoEntregaConcluida() {

        LocalDateTime dataRecebimento = LocalDateTime.of(2025, 12, 1, 14, 30);
        EntregaConcluida evento = new EntregaConcluida(
                "ENT-001",
                "PED-001",
                "João Silva",
                dataRecebimento,
                "Entregue com sucesso"
        );

        entrega.apply(evento);

        assertEquals(StatusEntrega.CONCLUIDA.toString(), entrega.getStatus());
        assertEquals("João Silva", entrega.getRecebedor());
        assertEquals(dataRecebimento.toString(), entrega.getDataHoraRecebimento());
    }

    @Test
    @DisplayName("Deve aplicar evento EntregaDevolvida corretamente")
    void deveAplicarEventoEntregaDevolvida() {

        EntregaDevolvida evento = new EntregaDevolvida(
                "ENT-001",
                "PED-001",
                "Cliente recusou",
                LocalDateTime.now(),
                "MOT-001"
        );

        entrega.apply(evento);

        assertEquals(StatusEntrega.DEVOLVIDA.toString(), entrega.getStatus());
    }

    @Test
    @DisplayName("Deve aplicar evento EntregaExtraviada corretamente")
    void deveAplicarEventoEntregaExtraviada() {
        EntregaExtraviada evento = new EntregaExtraviada(
                "ENT-001",
                "PED-001",
                "Acidente",
                LocalDateTime.now(),
                "Rodovia"
        );

        entrega.apply(evento);

        assertEquals(StatusEntrega.EXTRAVIADA.toString(), entrega.getStatus());
    }
}