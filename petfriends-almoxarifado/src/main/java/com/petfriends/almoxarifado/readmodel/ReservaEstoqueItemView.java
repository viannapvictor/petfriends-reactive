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

import java.util.UUID;

/**
 * Read Model para Itens da Reserva de Estoque
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("reserva_estoque_item_view")
public class ReservaEstoqueItemView implements Persistable<UUID> {
    
    @Id
    private UUID id;
    
    @Transient
    @Builder.Default
    private boolean isNew = true;
    
    @Column("reserva_id")
    private String reservaId;
    
    @Column("produto_id")
    private String produtoId;
    
    @Column("quantidade")
    private Integer quantidade;
    
    @Override
    public boolean isNew() {
        return isNew;
    }
}

