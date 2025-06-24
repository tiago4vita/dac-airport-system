DROP TABLE IF EXISTS funcionarios;
-- Create funcionarios table
CREATE TABLE IF NOT EXISTS funcionarios (
    codigo VARCHAR(255) PRIMARY KEY,
    cpf VARCHAR(11) UNIQUE NOT NULL,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_funcionarios_cpf ON funcionarios(cpf);
CREATE INDEX IF NOT EXISTS idx_funcionarios_email ON funcionarios(email);
CREATE INDEX IF NOT EXISTS idx_funcionarios_ativo ON funcionarios(ativo); 