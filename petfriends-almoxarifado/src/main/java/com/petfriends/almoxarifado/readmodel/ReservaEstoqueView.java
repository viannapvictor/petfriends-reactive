package com.petfriends.almoxarifado.readmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("reserva_estoque_view")
public class ReservaEstoqueView implements Persistable<String> {
    
    @Id
    private String id;
    
    @Transient
    @Builder.Default
    private boolean isNew = true;
    
    @Column("pedido_id")
    private String pedidoId;
    
    @Column("status")
    private String status;
    
    @Column("endereco_rua")
    private String enderecoRua;
    
    @Column("endereco_numero")
    private String enderecoNumero;
    
    @Column("endereco_complemento")
    private String enderecoComplemento;
    
    @Column("endereco_bairro")
    private String enderecoBairro;
    
    @Column("endereco_cidade")
    private String enderecoCidade;
    
    @Column("endereco_estado")
    private String enderecoEstado;
    
    @Column("endereco_cep")
    private String enderecoCep;
    
    @Column("operador_id")
    private String operadorId;
    
    @Column("created_at")
    private LocalDateTime createdAt;
    
    @Column("updated_at")
    private LocalDateTime updatedAt;
    
    @Override
    public boolean isNew() {
        return isNew;
    }
    
    public void markAsExisting() {
        this.isNew = false;
    }
}

