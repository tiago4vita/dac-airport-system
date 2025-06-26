-- Create aeroporto table
CREATE TABLE IF NOT EXISTS aeroporto (
    codigo VARCHAR(3) PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    uf VARCHAR(2) NOT NULL
);

-- Create voo table
CREATE TABLE IF NOT EXISTS voo (
    codigo VARCHAR(255) PRIMARY KEY,
    data_hora TIMESTAMP NOT NULL,
    origem VARCHAR(3) NOT NULL,
    destino VARCHAR(3) NOT NULL,
    preco_em_reais DECIMAL(10,2) NOT NULL,
    quantidade_poltronas_total INTEGER NOT NULL,
    quantidade_poltronas_ocupadas INTEGER NOT NULL DEFAULT 0,
    estado VARCHAR(20) NOT NULL DEFAULT 'CONFIRMADO',
    FOREIGN KEY (origem) REFERENCES aeroporto(codigo),
    FOREIGN KEY (destino) REFERENCES aeroporto(codigo)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_aeroporto_codigo ON aeroporto(codigo);
CREATE INDEX IF NOT EXISTS idx_voo_origem ON voo(origem);
CREATE INDEX IF NOT EXISTS idx_voo_destino ON voo(destino);
CREATE INDEX IF NOT EXISTS idx_voo_data_hora ON voo(data_hora);
CREATE INDEX IF NOT EXISTS idx_voo_estado ON voo(estado); 