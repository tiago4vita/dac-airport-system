# API Gateway - DAC UFPR

## Sobre o Projeto
Este é um API Gateway desenvolvido como parte do projeto da disciplina de Desenvolvimento de Aplicações Corporativas (DAC) do curso de Tecnologia em Análise e Desenvolvimento de Sistemas (TADS) da Universidade Federal do Paraná (UFPR).

**Disciplina:** Desenvolvimento de Aplicações Corporativas  
**Professor:** Dr. Razer A N R Montaño  
**Curso:** TADS - UFPR  
**Ano:** 2025

**Alunos:**
GRR20230988 - André Luiz Morais de Brito
GRR20221103 - Carlos Eduardo Camargo Viana
GRR20221105 - Mariane Roesler - roeslermariane@gmail.com
GRR20230984 - Pedro Felipe Ribeiro da Silva
GRR20234975 - Tiago Pareja Vita

## Funcionalidades
- Roteamento de tráfego para microsserviços
- Middleware de proxy para redirecionamento de requisições
- Endpoint de verificação de saúde (/health)
- Suporte a CORS
- Gerenciamento de variáveis de ambiente

## Tecnologias Utilizadas
- Node.js
- Express.js
- Axios
- CORS
- Dotenv
- Nodemon (desenvolvimento)

## Como Executar

1. Instale as dependências:
```bash
npm install
```

2. Configure as variáveis de ambiente:
- Crie um arquivo `.env` na raiz do projeto
- Defina as variáveis necessárias (exemplo):
```bash
PORT=3000
MICROSERVICE1_URL=http://localhost:3001
```
2.1 Novas variaveis de ambiente devem ser adicionadas no arquivo `.env.example`.


3. Execute o projeto:

Desenvolvimento:
```bash
npm run dev
```

Produção:
```bash
npm start
```

## Endpoints
- `GET /health`: Verificação de saúde do gateway
- `/service1/*`: Roteamento para o microsserviço 1

## Contribuição
Este é um projeto acadêmico desenvolvido para a disciplina de DAC da UFPR. Contribuições devem seguir as diretrizes do projeto acadêmico.