-- Estados da reserva
CREATE TABLE IF NOT EXISTS estados_reserva (
    codigo_estado VARCHAR(50) PRIMARY KEY,
    sigla VARCHAR(10) NOT NULL,
    descricao VARCHAR(255) NOT NULL
);

-- Insert default states
INSERT INTO estados_reserva (codigo_estado, sigla, descricao) VALUES
('CRIADA', 'CR', 'Reserva criada'),
('CHECK-IN', 'CI', 'Check-in realizado'),
('CANCELADA', 'CA', 'Reserva cancelada'),
('CANCELADA VOO', 'CV', 'Reserva cancelada devido ao cancelamento do voo'),
('EMBARCADA', 'EM', 'Passageiro embarcado'),
('REALIZADA', 'RE', 'Reserva realizada com sucesso'),
('NÃO REALIZADA', 'NR', 'Reserva não realizada')
ON CONFLICT (codigo_estado) DO NOTHING;

-- Reservas (normalized) - Updated with new fields
CREATE TABLE IF NOT EXISTS reservas (
    id VARCHAR(255) PRIMARY KEY,
    voo_id VARCHAR(255) NOT NULL,
    cliente_id VARCHAR(255) NOT NULL,
    data_hora_res TIMESTAMP NOT NULL,
    valor DOUBLE PRECISION NOT NULL,
    quantidade_poltronas INTEGER NOT NULL,
    milhas_utilizadas INTEGER NOT NULL,
    codigo_estado VARCHAR(50) NOT NULL,
    FOREIGN KEY (codigo_estado) REFERENCES estados_reserva(codigo_estado)
);

-- Audit trail for state changes
CREATE TABLE IF NOT EXISTS alteracoes_estado_reserva (
    id VARCHAR(255) PRIMARY KEY,
    reserva_id VARCHAR(255) NOT NULL,
    estado_origem_id VARCHAR(50) NOT NULL,
    estado_destino_id VARCHAR(50) NOT NULL,
    data_hora_alteracao TIMESTAMP NOT NULL,
    FOREIGN KEY (reserva_id) REFERENCES reservas(id),
    FOREIGN KEY (estado_origem_id) REFERENCES estados_reserva(codigo_estado),
    FOREIGN KEY (estado_destino_id) REFERENCES estados_reserva(codigo_estado)
); 