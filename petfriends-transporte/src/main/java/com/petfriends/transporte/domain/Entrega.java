package com.petfriends.transporte.domain;

import com.petfriends.transporte.commands.*;
import com.petfriends.transporte.events.*;
import lombok.Data;

/**
 * Agregado Entrega - DDD Aggregate Root
 * Gerencia o ciclo de vida de entregas
 * Arquitetura: Pure POJO (sem Axon/JPA) + Event Sourcing
 */
@Data
public class Entrega {

    private String id;
    private String pedidoId;
    private String reservaId;
    private String status;
    private String enderecoCompleto;
    private String dataPrevisaoEntrega;
    private String motoristaId;
    private String veiculoId;
    private String recebedor;
    private String dataHoraRecebimento;

    public Entrega() {
    }

    public Entrega(String id) {
        this.id = id;
    }

    /**
     * Command: Agendar Entrega
     */
    public BaseEvent<?> agendarEntrega(AgendarEntregaCommand comando) {
        if (comando.endereco == null) {
            throw new IllegalArgumentException("Endereço de entrega é obrigatório");
        }

        String enderecoCompleto = formatarEndereco(comando.endereco);

        return new EntregaAgendada(
                comando.id,
                comando.pedidoId,
                comando.reservaId,
                enderecoCompleto,
                comando.dataPrevisaoEntrega
        );
    }

    /**
     * Command: Iniciar Transporte
     */
    public BaseEvent<?> iniciarTransporte(IniciarTransporteCommand comando) {
        if (!StatusEntrega.AGENDADA.toString().equals(this.status)) {
            throw new IllegalStateException("Só é possível iniciar transporte de entregas agendadas");
        }

        return new TransporteIniciado(comando.id, comando.motoristaId, comando.veiculoId);
    }

    /**
     * Command: Concluir Entrega
     */
    public BaseEvent<?> concluirEntrega(ConcluirEntregaCommand comando) {
        if (!StatusEntrega.EM_TRANSITO.toString().equals(this.status)) {
            throw new IllegalStateException("Só é possível concluir entregas em trânsito");
        }

        return new EntregaConcluida(
                comando.id,
                this.pedidoId,
                comando.recebedor,
                comando.dataRecebimento,
                comando.observacoes
        );
    }

    /**
     * Aplica evento ao agregado (Event Sourcing)
     */
    public void apply(BaseEvent<?> evento) {
        if (evento instanceof EntregaAgendada) {
            on((EntregaAgendada) evento);
        } else if (evento instanceof TransporteIniciado) {
            on((TransporteIniciado) evento);
        } else if (evento instanceof EntregaConcluida) {
            on((EntregaConcluida) evento);
        }
    }

    // Event Handlers (reconstroem o estado)
    protected void on(EntregaAgendada evento) {
        this.id = evento.id;
        this.pedidoId = evento.pedidoId;
        this.reservaId = evento.reservaId;
        this.status = StatusEntrega.AGENDADA.toString();
        this.enderecoCompleto = evento.enderecoCompleto;
        this.dataPrevisaoEntrega = evento.dataPrevisao.toString();
    }

    protected void on(TransporteIniciado evento) {
        this.status = StatusEntrega.EM_TRANSITO.toString();
        this.motoristaId = evento.motoristaId;
        this.veiculoId = evento.veiculoId;
    }

    protected void on(EntregaConcluida evento) {
        this.status = StatusEntrega.CONCLUIDA.toString();
        this.recebedor = evento.recebedor;
        this.dataHoraRecebimento = evento.dataHoraRecebimento.toString();
    }

    private String formatarEndereco(AgendarEntregaCommand.EnderecoDTO endereco) {
        return String.format("%s, %s %s - %s, %s/%s - CEP: %s",
                endereco.rua,
                endereco.numero,
                endereco.complemento != null ? endereco.complemento : "",
                endereco.bairro,
                endereco.cidade,
                endereco.estado,
                endereco.cep);
    }
}
