package com.petfriends.transporte.commands;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes dos Commands do Transporte")
class CommandsTest {

    @Test
    @DisplayName("Deve criar AgendarEntregaCommand com todos os campos")
    void deveCriarAgendarEntregaCommand() {
        AgendarEntregaCommand.EnderecoDTO endereco = new AgendarEntregaCommand.EnderecoDTO(
                "Rua A", "100", "Apto 10", "Centro", "São Paulo", "SP", "01000-000"
        );
        LocalDate dataPrevisao = LocalDate.of(2025, 12, 25);

        AgendarEntregaCommand command = new AgendarEntregaCommand(
                "ENT-001", "PED-001", "RES-001", endereco, dataPrevisao
        );

        assertEquals("ENT-001", command.id);
        assertEquals("PED-001", command.pedidoId);
        assertEquals("RES-001", command.reservaId);
        assertEquals(endereco, command.endereco);
        assertEquals(dataPrevisao, command.dataPrevisaoEntrega);
    }

    @Test
    @DisplayName("Deve criar EnderecoDTO com todos os campos")
    void deveCriarEnderecoDTO() {
        AgendarEntregaCommand.EnderecoDTO endereco = new AgendarEntregaCommand.EnderecoDTO(
                "Av Paulista", "1000", null, "Bela Vista", "São Paulo", "SP", "01310-100"
        );

        assertEquals("Av Paulista", endereco.rua);
        assertEquals("1000", endereco.numero);
        assertNull(endereco.complemento);
        assertEquals("Bela Vista", endereco.bairro);
        assertEquals("São Paulo", endereco.cidade);
        assertEquals("SP", endereco.estado);
        assertEquals("01310-100", endereco.cep);
    }

    @Test
    @DisplayName("Deve criar IniciarTransporteCommand")
    void deveCriarIniciarTransporteCommand() {
        IniciarTransporteCommand command = new IniciarTransporteCommand("ENT-001", "MOT-001", "VEI-001");

        assertEquals("ENT-001", command.id);
        assertEquals("MOT-001", command.motoristaId);
        assertEquals("VEI-001", command.veiculoId);
    }

    @Test
    @DisplayName("Deve criar ConcluirEntregaCommand com todos os campos")
    void deveCriarConcluirEntregaCommand() {
        LocalDateTime dataRecebimento = LocalDateTime.now();

        ConcluirEntregaCommand command = new ConcluirEntregaCommand(
                "ENT-001", "João Silva", dataRecebimento, "Entregue com sucesso"
        );

        assertEquals("ENT-001", command.id);
        assertEquals("João Silva", command.recebedor);
        assertEquals(dataRecebimento, command.dataRecebimento);
        assertEquals("Entregue com sucesso", command.observacoes);
    }

    @Test
    @DisplayName("Deve criar DevolverEntregaCommand com todos os campos")
    void deveCriarDevolverEntregaCommand() {
        LocalDateTime dataDevolucao = LocalDateTime.now();

        DevolverEntregaCommand command = new DevolverEntregaCommand(
                "ENT-001", "Cliente ausente", dataDevolucao, "MOT-001"
        );

        assertEquals("ENT-001", command.id);
        assertEquals("Cliente ausente", command.motivo);
        assertEquals(dataDevolucao, command.dataDevolucao);
        assertEquals("MOT-001", command.responsavel);
    }

    @Test
    @DisplayName("Deve criar MarcarEntregaExtraviadaCommand")
    void deveCriarMarcarEntregaExtraviadaCommand() {
        LocalDateTime dataExtravio = LocalDateTime.now();

        MarcarEntregaExtraviadaCommand command = new MarcarEntregaExtraviadaCommand(
                "ENT-001", "Pacote perdido", dataExtravio, "Centro de Distribuição"
        );

        assertEquals("ENT-001", command.id);
        assertEquals("Pacote perdido", command.motivo);
        assertEquals(dataExtravio, command.dataExtravio);
        assertEquals("Centro de Distribuição", command.localUltimoRegistro);
    }

    @Test
    @DisplayName("Deve criar AgendarEntregaCommand sem complemento no endereço")
    void deveCriarAgendarEntregaCommandSemComplemento() {
        AgendarEntregaCommand.EnderecoDTO endereco = new AgendarEntregaCommand.EnderecoDTO(
                "Rua B", "200", null, "Jardim", "Campinas", "SP", "13000-000"
        );
        LocalDate dataPrevisao = LocalDate.of(2025, 12, 30);

        AgendarEntregaCommand command = new AgendarEntregaCommand(
                "ENT-002", "PED-002", "RES-002", endereco, dataPrevisao
        );

        assertNull(command.endereco.complemento);
        assertEquals("Rua B", command.endereco.rua);
    }

    @Test
    @DisplayName("Deve criar ConcluirEntregaCommand sem observações")
    void deveCriarConcluirEntregaCommandSemObservacoes() {
        LocalDateTime dataRecebimento = LocalDateTime.now();

        ConcluirEntregaCommand command = new ConcluirEntregaCommand(
                "ENT-001", "Maria Santos", dataRecebimento, null
        );

        assertNull(command.observacoes);
        assertEquals("Maria Santos", command.recebedor);
    }

    @Test
    @DisplayName("Deve criar IniciarTransporteCommand com IDs nulos")
    void deveCriarIniciarTransporteCommandComIdsNulos() {
        IniciarTransporteCommand command = new IniciarTransporteCommand("ENT-001", null, null);

        assertEquals("ENT-001", command.id);
        assertNull(command.motoristaId);
        assertNull(command.veiculoId);
    }

    @Test
    @DisplayName("Deve criar DevolverEntregaCommand com motivo nulo")
    void deveCriarDevolverEntregaCommandComMotivoNulo() {
        LocalDateTime dataDevolucao = LocalDateTime.now();

        DevolverEntregaCommand command = new DevolverEntregaCommand(
                "ENT-001", null, dataDevolucao, "MOT-001"
        );

        assertNull(command.motivo);
        assertEquals("ENT-001", command.id);
    }

    @Test
    @DisplayName("Deve criar MarcarEntregaExtraviadaCommand com local nulo")
    void deveCriarMarcarEntregaExtraviadaCommandComLocalNulo() {
        LocalDateTime dataExtravio = LocalDateTime.now();

        MarcarEntregaExtraviadaCommand command = new MarcarEntregaExtraviadaCommand(
                "ENT-001", "Extraviado", dataExtravio, null
        );

        assertNull(command.localUltimoRegistro);
        assertEquals("Extraviado", command.motivo);
    }
}

