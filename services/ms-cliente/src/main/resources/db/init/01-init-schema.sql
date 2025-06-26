-- Create clientes table
CREATE TABLE IF NOT EXISTS clientes (
    codigo VARCHAR(255) PRIMARY KEY,
    cpf VARCHAR(11) UNIQUE NOT NULL,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    rua VARCHAR(255) NOT NULL,
    numero VARCHAR(255) NOT NULL,
    complemento VARCHAR(255),
    bairro VARCHAR(255) NOT NULL,
    cep VARCHAR(8) NOT NULL,
    cidade VARCHAR(255) NOT NULL,
    uf VARCHAR(255) NOT NULL,
    milhas BIGINT NOT NULL DEFAULT 0,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP
);

-- Create transacoes_milhas table
CREATE TABLE IF NOT EXISTS transacoes_milhas (
    codigo BIGSERIAL PRIMARY KEY,
    cliente_codigo VARCHAR(255) NOT NULL,
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valor_reais BIGINT NOT NULL,
    quantidade BIGINT NOT NULL,
    tipo VARCHAR(10) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    codigo_reserva VARCHAR(255),
    FOREIGN KEY (cliente_codigo) REFERENCES clientes(codigo)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_clientes_cpf ON clientes(cpf);
CREATE INDEX IF NOT EXISTS idx_clientes_email ON clientes(email);
CREATE INDEX IF NOT EXISTS idx_transacoes_cliente ON transacoes_milhas(cliente_codigo);
CREATE INDEX IF NOT EXISTS idx_transacoes_data ON transacoes_milhas(data_hora); 