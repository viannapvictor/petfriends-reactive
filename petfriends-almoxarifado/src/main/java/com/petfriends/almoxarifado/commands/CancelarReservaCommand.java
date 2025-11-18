package com.petfriends.almoxarifado.commands;

public class CancelarReservaCommand extends BaseCommand<String> {

    public final String motivo;

    public CancelarReservaCommand(String id, String motivo) {
        super(id);
        this.motivo = motivo;
    }
}