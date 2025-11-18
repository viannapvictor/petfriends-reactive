package com.petfriends.almoxarifado.domain;

import com.petfriends.almoxarifado.commands.*;
import com.petfriends.almoxarifado.events.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ReservaEstoque {

    private String id;
    private String pedidoId;
    private String status;
    private List<ItemReserva> itens;
    private String operadorId;

    public ReservaEstoque() {
        this.itens = new ArrayList<>();
    }

    public ReservaEstoque(String id) {
        this.id = id;
        this.itens = new ArrayList<>();
    }

    public BaseEvent<?> reservarEstoque(ReservarEstoqueCommand comando) {
        if (comando.itens == null || comando.itens.isEmpty()) {
            throw new IllegalArgumentException("Não é possível reservar estoque sem itens");
        }

        boolean estoqueDisponivel = validarDisponibilidadeEstoque(comando.itens);

        if (estoqueDisponivel) {
            List<EstoqueReservado.ItemReservado> itensReservados = comando.itens.stream()
                    .map(item -> new EstoqueReservado.ItemReservado(item.produtoId, item.quantidade))
                    .collect(Collectors.toList());

            return new EstoqueReservado(comando.id, comando.pedidoId, itensReservados);
        } else {
            List<String> produtosIndisponiveis = comando.itens.stream()
                    .map(item -> item.produtoId)
                    .collect(Collectors.toList());

            return new EstoqueInsuficiente(
                    comando.id,
                    comando.pedidoId,
                    produtosIndisponiveis,
                    "Estoque insuficiente para atender o pedido"
            );
        }
    }

    public BaseEvent<?> confirmarReserva(ConfirmarReservaCommand comando) {
        if (StatusReserva.INSUFICIENTE.toString().equals(this.status)) {
            throw new IllegalStateException("Não é possível confirmar reserva com estoque insuficiente");
        }

        return new ReservaConfirmada(comando.id, this.pedidoId);
    }

    public BaseEvent<?> cancelarReserva(CancelarReservaCommand comando) {
        if (StatusReserva.SEPARADA.toString().equals(this.status)) {
            throw new IllegalStateException("Não é possível cancelar reserva já separada");
        }

        return new ReservaCancelada(comando.id, this.pedidoId, comando.motivo);
    }

    public BaseEvent<?> separarItens(SepararItensCommand comando) {
        if (!StatusReserva.CONFIRMADA.toString().equals(this.status)) {
            throw new IllegalStateException("Só é possível separar itens de reservas confirmadas");
        }

        return new ItensSeparados(comando.id, this.pedidoId, comando.operadorId);
    }

    public void apply(BaseEvent<?> evento) {
        if (evento instanceof EstoqueReservado) {
            on((EstoqueReservado) evento);
        } else if (evento instanceof EstoqueInsuficiente) {
            on((EstoqueInsuficiente) evento);
        } else if (evento instanceof ReservaConfirmada) {
            on((ReservaConfirmada) evento);
        } else if (evento instanceof ReservaCancelada) {
            on((ReservaCancelada) evento);
        } else if (evento instanceof ItensSeparados) {
            on((ItensSeparados) evento);
        }
    }

    protected void on(EstoqueReservado evento) {
        this.id = evento.id;
        this.pedidoId = evento.pedidoId;
        this.status = StatusReserva.PENDENTE.toString();
        this.itens = evento.itens.stream()
                .map(item -> new ItemReserva(item.produtoId, item.quantidade))
                .collect(Collectors.toList());
    }

    protected void on(EstoqueInsuficiente evento) {
        this.id = evento.id;
        this.pedidoId = evento.pedidoId;
        this.status = StatusReserva.INSUFICIENTE.toString();
    }

    protected void on(ReservaConfirmada evento) {
        this.status = StatusReserva.CONFIRMADA.toString();
    }

    protected void on(ReservaCancelada evento) {
        this.status = StatusReserva.CANCELADA.toString();
    }

    protected void on(ItensSeparados evento) {
        this.status = StatusReserva.SEPARADA.toString();
        this.operadorId = evento.operadorId;
    }

    private boolean validarDisponibilidadeEstoque(List<ReservarEstoqueCommand.ItemReservaDTO> itens) {
        return itens.stream().allMatch(item -> item.quantidade > 0);
    }

    @Data
    public static class ItemReserva {
        private String produtoId;
        private int quantidade;

        public ItemReserva() {}

        public ItemReserva(String produtoId, int quantidade) {
            this.produtoId = produtoId;
            this.quantidade = quantidade;
        }
    }
}