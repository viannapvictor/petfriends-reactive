package com.petfriends.transporte.readmodel;

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
@Table("entrega_view")
public class EntregaView implements Persistable<String> {
    
    @Id
    private String id;
    
    @Transient
    @Builder.Default
    private boolean isNew = true;
    
    @Column("pedido_id")
    private String pedidoId;
    
    @Column("reserva_id")
    private String reservaId;
    
    @Column("status")
    private String status;
    
    @Column("endereco_completo")
    private String enderecoCompleto;
    
    @Column("data_previsao_entrega")
    private String dataPrevisaoEntrega;
    
    @Column("motorista_id")
    private String motoristaId;
    
    @Column("veiculo_id")
    private String veiculoId;
    
    @Column("recebedor")
    private String recebedor;
    
    @Column("data_hora_recebimento")
    private String dataHoraRecebimento;

    @Column("motivo_devolucao")
    private String motivoDevolucao;

    @Column("data_devolucao")
    private String dataDevolucao;

    @Column("responsavel_devolucao")
    private String responsavelDevolucao;

    @Column("motivo_extravio")
    private String motivoExtravio;

    @Column("data_extravio")
    private String dataExtravio;

    @Column("local_ultimo_registro")
    private String localUltimoRegistro;

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

