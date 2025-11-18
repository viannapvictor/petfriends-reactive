package com.petfriends.transporte.commands;

import java.time.LocalDate;

public class AgendarEntregaCommand extends BaseCommand<String> {

    public final String pedidoId;
    public final String reservaId;
    public final EnderecoDTO endereco;
    public final LocalDate dataPrevisaoEntrega;

    public AgendarEntregaCommand(String id, String pedidoId, String reservaId,
                                 EnderecoDTO endereco, LocalDate dataPrevisaoEntrega) {
        super(id);
        this.pedidoId = pedidoId;
        this.reservaId = reservaId;
        this.endereco = endereco;
        this.dataPrevisaoEntrega = dataPrevisaoEntrega;
    }

    public static class EnderecoDTO {
        public final String rua;
        public final String numero;
        public final String complemento;
        public final String bairro;
        public final String cidade;
        public final String estado;
        public final String cep;

        public EnderecoDTO(String rua, String numero, String complemento,
                           String bairro, String cidade, String estado, String cep) {
            this.rua = rua;
            this.numero = numero;
            this.complemento = complemento;
            this.bairro = bairro;
            this.cidade = cidade;
            this.estado = estado;
            this.cep = cep;
        }
    }
}