-- Insert sample funcionario data
INSERT INTO funcionarios (codigo, cpf, nome, email, telefone, data_criacao, ativo) VALUES
('7b9a4f9c-b01c-4733-bb64-f848f0d76952', '90769281001', 'Teste', 'func_pre@gmail.com', '11997020405', '2025-06-22 12:00:27.329735', true)
ON CONFLICT (codigo) DO NOTHING; 