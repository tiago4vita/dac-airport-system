-- Insert sample client data
INSERT INTO clientes (codigo, cpf, nome, email, rua, numero, complemento, bairro, cep, cidade, uf, milhas, data_criacao) VALUES
('a3773666-379c-4273-ab10-22632f56d21b', '62529933014', 'Joao Melara', 'joaomelara@xerocopiadora.com.br', 'Rua das Palmeiras', '567', 'Apartamento', 'PBandeirantes', '29142120', 'Cariacica', 'ES', 0, '2025-06-22 09:21:22.508404')
ON CONFLICT (codigo) DO NOTHING; 