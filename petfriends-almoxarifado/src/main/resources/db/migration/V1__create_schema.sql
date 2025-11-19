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

CREATE TABLE IF NOT EXISTS reserva_estoque_view (
    id VARCHAR(255) PRIMARY KEY,
    pedido_id VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    endereco_rua VARCHAR(255),
    endereco_numero VARCHAR(50),
    endereco_complemento VARCHAR(255),
    endereco_bairro VARCHAR(100),
    endereco_cidade VARCHAR(100),
    endereco_estado VARCHAR(2),
    endereco_cep VARCHAR(9),
    operador_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_reserva_estoque_view_pedido_id ON reserva_estoque_view(pedido_id);
CREATE INDEX idx_reserva_estoque_view_status ON reserva_estoque_view(status);
CREATE INDEX idx_reserva_endereco_cidade ON reserva_estoque_view(endereco_cidade);
CREATE INDEX idx_reserva_endereco_estado ON reserva_estoque_view(endereco_estado);

CREATE TABLE IF NOT EXISTS reserva_estoque_item_view (
    id UUID PRIMARY KEY,
    reserva_id VARCHAR(255) NOT NULL,
    produto_id VARCHAR(255) NOT NULL,
    quantidade INTEGER NOT NULL,
    FOREIGN KEY (reserva_id) REFERENCES reserva_estoque_view(id) ON DELETE CASCADE
);

CREATE INDEX idx_reserva_item_reserva_id ON reserva_estoque_item_view(reserva_id);
CREATE INDEX idx_reserva_item_produto_id ON reserva_estoque_item_view(produto_id);

COMMENT ON TABLE event_store IS 'Event Store - armazena todos os eventos de domínio de forma imutável';
COMMENT ON TABLE reserva_estoque_view IS 'Read Model otimizado para consultas de reservas de estoque';
COMMENT ON TABLE reserva_estoque_item_view IS 'Itens das reservas de estoque (Read Model)';

