CREATE TABLE IF NOT EXISTS event_store (
    id UUID PRIMARY KEY,
    aggregate_id VARCHAR(255) NOT NULL,
    aggregate_type VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    event_data TEXT NOT NULL,
    version BIGINT NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    metadata TEXT,
    CONSTRAINT uk_aggregate_version UNIQUE (aggregate_id, version)
);

CREATE INDEX idx_event_store_aggregate_id ON event_store(aggregate_id);
CREATE INDEX idx_event_store_aggregate_type ON event_store(aggregate_type);
CREATE INDEX idx_event_store_timestamp ON event_store(timestamp);

CREATE TABLE IF NOT EXISTS entrega_view (
    id VARCHAR(255) PRIMARY KEY,
    pedido_id VARCHAR(255) NOT NULL,
    reserva_id VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    endereco_completo TEXT NOT NULL,
    data_previsao_entrega VARCHAR(50),
    motorista_id VARCHAR(255),
    veiculo_id VARCHAR(255),
    recebedor VARCHAR(255),
    data_hora_recebimento VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_entrega_view_pedido_id ON entrega_view(pedido_id);
CREATE INDEX idx_entrega_view_reserva_id ON entrega_view(reserva_id);
CREATE INDEX idx_entrega_view_status ON entrega_view(status);
CREATE INDEX idx_entrega_view_motorista_id ON entrega_view(motorista_id);

COMMENT ON TABLE event_store IS 'Event Store - armazena todos os eventos de domínio de forma imutável';
COMMENT ON TABLE entrega_view IS 'Read Model otimizado para consultas de entregas';

