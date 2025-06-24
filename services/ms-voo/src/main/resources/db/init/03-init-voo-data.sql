-- Insert sample voo data
INSERT INTO voo (codigo, data_hora, origem, destino, preco_em_reais, quantidade_poltronas_total, quantidade_poltronas_ocupadas, estado) VALUES
    ('LLQL7144', '2024-10-15 14:30:00', 'CWB', 'JPA', 1200.0, 200, 100, 'CONFIRMADO'),
    ('LBLK6679', '2024-12-15 14:30:00', 'GRU', 'CWB', 2000.0, 300, 100, 'CONFIRMADO'),
    ('AEBW4012', '2025-08-10 10:30:00', 'POA', 'CWB', 1200.0, 200, 100, 'CONFIRMADO'),
    ('XJPR7423', '2025-09-11 09:30:00', 'CWB', 'GIG', 800.0, 300, 50, 'CONFIRMADO'),
    ('NBUA4925', '2025-10-12 08:30:00', 'CWB', 'POA', 400.0, 120, 50, 'CONFIRMADO')
ON CONFLICT (codigo) DO NOTHING; 