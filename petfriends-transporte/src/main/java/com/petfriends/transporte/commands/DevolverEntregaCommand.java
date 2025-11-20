package com.petfriends.transporte.commands;

import java.time.LocalDateTime;

public class DevolverEntregaCommand extends BaseCommand<String> {

    public final String motivo;
    public final LocalDateTime dataDevolucao;
    public final String responsavel;

    public DevolverEntregaCommand(String id, String motivo,
                                  LocalDateTime dataDevolucao, String responsavel) {
        super(id);
        this.motivo = motivo;
        this.dataDevolucao = dataDevolucao;
        this.responsavel = responsavel;
    }
}