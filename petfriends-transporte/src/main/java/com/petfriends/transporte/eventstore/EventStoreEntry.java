package com.petfriends.transporte.eventstore;

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
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("event_store")
public class EventStoreEntry implements Persistable<UUID> {
    
    @Id
    private UUID id;
    
    @Transient
    @Builder.Default
    private boolean isNew = true;
    
    @Column("aggregate_id")
    private String aggregateId;
    
    @Column("aggregate_type")
    private String aggregateType;
    
    @Column("event_type")
    private String eventType;
    
    @Column("event_data")
    private String eventData;  // JSON serialized event
    
    @Column("version")
    private Long version;
    
    @Column("timestamp")
    private LocalDateTime timestamp;
    
    @Column("metadata")
    private String metadata;  // Optional metadata as JSON
    
    @Override
    public boolean isNew() {
        return isNew;
    }
}

