package com.petfriends.almoxarifado.commands;

import java.util.List;

public class ReservarEstoqueCommand extends BaseCommand<String> {

    public final String pedidoId;
    public final List<ItemReservaDTO> itens;

    public ReservarEstoqueCommand(String id, String pedidoId, List<ItemReservaDTO> itens) {
        super(id);
        this.pedidoId = pedidoId;
        this.itens = itens;
    }

    public static class ItemReservaDTO {
        public final String produtoId;
        public final int quantidade;

        public ItemReservaDTO(String produtoId, int quantidade) {
            this.produtoId = produtoId;
            this.quantidade = quantidade;
        }
    }
}