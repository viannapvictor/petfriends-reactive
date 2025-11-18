package com.petfriends.transporte.commands;

import java.time.LocalDateTime;

public class ConcluirEntregaCommand extends BaseCommand<String> {

    public final String recebedor;
    public final LocalDateTime dataRecebimento;
    public final String observacoes;

    public ConcluirEntregaCommand(String id, String recebedor,
                                  LocalDateTime dataRecebimento, String observacoes) {
        super(id);
        this.recebedor = recebedor;
        this.dataRecebimento = dataRecebimento;
        this.observacoes = observacoes;
    }
}