# Sistema de Aeroporto - DAC

Repositório do trabalho de DAC sobre uma aplicação de um sistema de aeroporto feito em arquitetura de microsserviços. (TADS-N UFPR)

**Disciplina:** Desenvolvimento de Aplicações Corporativas  
**Professor:** Dr. Razer A N R Montaño  
**Curso:** TADS - UFPR  
**Ano:** 2025

**Alunos:**
- GRR20230988 - André Luiz Morais de Brito
- GRR20221103 - Carlos Eduardo Camargo Viana
- GRR20221105 - Mariane Roesler
- GRR20230984 - Pedro Felipe Ribeiro da Silva
- GRR20234975 - Tiago Pareja Vita

## Sub-equipes
### Configuração inicial das sub-equipes
- Frontend: André, Carlos e Mariane
- API Gateway e MS Auth: Pedro e Tiago

## Estrutura do Projeto
dac-airport-system/\
    &emsp;├── frontend/ # Aplicação React\
    &emsp;├── api-gateway/ # API Gateway em Node.js\
    &emsp;└── services/ # Microsserviços\
    &emsp;&emsp;    ├──ms-reserva\
    &emsp;&emsp;    ├──ms-cliente\
    &emsp;&emsp;    ├──ms-voos\
    &emsp;&emsp;    ├──ms-auth\
    &emsp;&emsp;    ├──ms-funcionario\
    &emsp;&emsp;    └──SAGAs\

## Padrões de Branch

- main : Produção.
- componente/rfxx/nome da branch : Criar uma branch para cada regra de negócio.
    - Ex. 'frontend/rf01/tela-autocadastro.
    - Se possível pedir para +1 pessoa revisa a Pull Request antes do merge.
    - Deletar branch após PR.
