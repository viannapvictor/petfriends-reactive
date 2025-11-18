package com.petfriends.transporte.commands;

public class IniciarTransporteCommand extends BaseCommand<String> {

    public final String motoristaId;
    public final String veiculoId;

    public IniciarTransporteCommand(String id, String motoristaId, String veiculoId) {
        super(id);
        this.motoristaId = motoristaId;
        this.veiculoId = veiculoId;
    }
}