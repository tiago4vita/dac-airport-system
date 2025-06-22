-- Denormalized view for fast queries - Updated with new fields
CREATE TABLE IF NOT EXISTS reservas_view (
    id VARCHAR(255) PRIMARY KEY,
    voo_id VARCHAR(255) NOT NULL,
    data_hora_res TIMESTAMP NOT NULL,
    valor DOUBLE PRECISION NOT NULL,
    quantidade_poltronas INTEGER NOT NULL,
    milhas_utilizadas INTEGER NOT NULL,
    estado_codigo VARCHAR(50) NOT NULL,
    estado_sigla VARCHAR(10) NOT NULL,
    estado_descricao VARCHAR(255) NOT NULL
); 