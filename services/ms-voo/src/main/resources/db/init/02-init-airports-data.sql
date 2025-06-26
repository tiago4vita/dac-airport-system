-- Insert Brazilian airports
INSERT INTO aeroporto (codigo, nome, cidade, uf) VALUES
    ('GRU', 'Aeroporto Internacional de São Paulo/Guarulhos', 'Guarulhos', 'SP'),
    ('GIG', 'Aeroporto Internacional do Rio de Janeiro/Galeão', 'Rio de Janeiro', 'RJ'),
    ('BSB', 'Aeroporto Internacional de Brasília', 'Brasília', 'DF'),
    ('CWB', 'Aeroporto Internacional de Curitiba', 'Curitiba', 'PR'),
    ('POA', 'Aeroporto Internacional Salgado Filho', 'Porto Alegre', 'RS'),
    ('REC', 'Aeroporto Internacional do Recife/Guararapes', 'Recife', 'PE'),
    ('SSA', 'Aeroporto Internacional de Salvador', 'Salvador', 'BA'),
    ('FOR', 'Aeroporto Internacional Pinto Martins', 'Fortaleza', 'CE'),
    ('BEL', 'Aeroporto Internacional de Belém/Val-de-Cans', 'Belém', 'PA'),
    ('MAO', 'Aeroporto Internacional de Manaus', 'Manaus', 'AM'),
    ('FLN', 'Aeroporto Internacional Hercílio Luz', 'Florianópolis', 'SC'),
    ('NAT', 'Aeroporto Internacional de Natal', 'Natal', 'RN'),
    ('VIX', 'Aeroporto de Vitória', 'Vitória', 'ES'),
    ('GYN', 'Aeroporto de Goiânia', 'Goiânia', 'GO'),
    ('CGH', 'Aeroporto de Congonhas', 'São Paulo', 'SP'),
    ('SDU', 'Aeroporto Santos Dumont', 'Rio de Janeiro', 'RJ'),
    ('CNF', 'Aeroporto Internacional Tancredo Neves', 'Belo Horizonte', 'MG'),
    ('AJU', 'Aeroporto de Aracaju', 'Aracaju', 'SE'),
    ('JPA', 'Aeroporto Internacional de João Pessoa', 'João Pessoa', 'PB'),
    ('MCP', 'Aeroporto Internacional de Macapá', 'Macapá', 'AP')
ON CONFLICT (codigo) DO NOTHING; 