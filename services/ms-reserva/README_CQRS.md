# CQRS Implementation in MS-Reserva

This document describes the Command Query Responsibility Segregation (CQRS) pattern implementation in the ms-reserva microservice.

## Overview

The ms-reserva service implements CQRS with two separate databases:
- **Command Database** (`reserva_command_db`): Handles all write operations (commands)
- **Query Database** (`reserva_query_db`): Handles all read operations (queries)

## Architecture

### Command Side (Write Operations)
- **Database**: `reserva_command_db` (PostgreSQL)
- **Tables**: 
  - `reservas` - Main reservation entity
  - `estados_reserva` - Reservation states
  - `alteracoes_estado_reserva` - State change audit trail
- **Services**: `ReservaCommandService`
- **Repositories**: `ReservaRepository`, `EstadoReservaRepository`, `AlteracaoEstadoReservaRepository`
- **Transaction Manager**: `commandTransactionManager`

### Query Side (Read Operations)
- **Database**: `reserva_query_db` (PostgreSQL)
- **Tables**: 
  - `reservas_view` - Denormalized view for fast queries
- **Services**: `ReservaQueryService`
- **Repositories**: `ReservaViewRepository`
- **Transaction Manager**: `queryTransactionManager`

### Event Sourcing
- **Event Handler**: `ReservaEventHandler`
- **Message Queue**: RabbitMQ
- **Events**: `ReservaEvent` (RESERVA_CRIADA, RESERVA_CANCELADA, RESERVA_ESTADO_ALTERADO)

## Database Schema

### Command Database Schema
```sql
-- Estados da reserva
CREATE TABLE estados_reserva (
    codigo_estado VARCHAR(50) PRIMARY KEY,
    sigla VARCHAR(10) NOT NULL,
    descricao VARCHAR(255) NOT NULL
);

-- Reservas (normalized) - Updated with new fields
CREATE TABLE reservas (
    id VARCHAR(255) PRIMARY KEY,
    voo_id VARCHAR(255) NOT NULL,
    cliente_id VARCHAR(255) NOT NULL,
    data_hora_res TIMESTAMP NOT NULL,
    valor DOUBLE PRECISION NOT NULL,
    quantidade_poltronas INTEGER NOT NULL,
    milhas_utilizadas INTEGER NOT NULL,
    codigo_estado VARCHAR(50) NOT NULL,
    FOREIGN KEY (codigo_estado) REFERENCES estados_reserva(codigo_estado)
);

-- Audit trail for state changes
CREATE TABLE alteracoes_estado_reserva (
    id VARCHAR(255) PRIMARY KEY,
    reserva_id VARCHAR(255) NOT NULL,
    estado_anterior VARCHAR(50) NOT NULL,
    estado_novo VARCHAR(50) NOT NULL,
    data_hora_alteracao TIMESTAMP NOT NULL,
    FOREIGN KEY (reserva_id) REFERENCES reservas(id)
);
```

### Query Database Schema
```sql
-- Denormalized view for fast queries - Updated with new fields
CREATE TABLE reservas_view (
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
```

## Flow Diagram

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   API Gateway   │    │   RabbitMQ      │    │   MS-Reserva    │
│                 │    │                 │    │                 │
│ 1. Create       │───▶│ reserva.efetuar │───▶│ EfetuarReserva  │
│    Reservation  │    │                 │    │ Consumer        │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                       │
                                                       ▼
                                              ┌─────────────────┐
                                              │ Command DB      │
                                              │ (Write)         │
                                              │ - reservas      │
                                              │ - estados       │
                                              │ - alteracoes    │
                                              └─────────────────┘
                                                       │
                                                       ▼
                                              ┌─────────────────┐
                                              │ Event Publisher │
                                              │ ReservaEvent    │
                                              └─────────────────┘
                                                       │
                                                       ▼
                                              ┌─────────────────┐
                                              │ RabbitMQ        │
                                              │ reserva.eventos │
                                              └─────────────────┘
                                                       │
                                                       ▼
                                              ┌─────────────────┐
                                              │ Event Handler   │
                                              │ ReservaEventHandler │
                                              └─────────────────┘
                                                       │
                                                       ▼
                                              ┌─────────────────┐
                                              │ Query DB        │
                                              │ (Read)          │
                                              │ - reservas_view │
                                              └─────────────────┘
```

## Key Components

### 1. Database Configuration (`DatabaseConfig.java`)
- Configures separate entity managers for command and query databases
- Sets up distinct transaction managers
- Configures JPA properties for each database

### 2. Command Service (`ReservaCommandService.java`)
- Handles all write operations
- Creates and updates reservations
- Manages state transitions
- Uses command transaction manager

### 3. Query Service (`ReservaQueryService.java`)
- Handles all read operations
- Queries the denormalized view
- Uses query transaction manager
- Read-only transactions

### 4. Event Handler (`ReservaEventHandler.java`)
- Listens to RabbitMQ events
- Updates query database based on events
- Maintains consistency between command and query databases

### 5. Consumers
- `EfetuarReservaConsumer`: Creates reservations
- `CancelarReservaConsumer`: Cancels reservations
- `ConsultarReservaConsumer`: Queries reservations (uses query DB)

## Benefits of This Implementation

1. **Performance**: Read operations are fast due to denormalized view
2. **Scalability**: Command and query sides can be scaled independently
3. **Consistency**: Event sourcing ensures eventual consistency
4. **Audit Trail**: All state changes are tracked in the command database
5. **Separation of Concerns**: Clear separation between read and write operations

## Event Types

- `RESERVA_CRIADA`: When a new reservation is created
- `RESERVA_CANCELADA`: When a reservation is cancelled
- `RESERVA_ESTADO_ALTERADO`: When reservation state changes

## Database Initialization

The databases are automatically initialized with:
- `01-init-command-db.sql`: Sets up command database schema
- `02-init-query-db.sql`: Sets up query database schema

## Monitoring and Debugging

- Check RabbitMQ management console for event flow
- Monitor both databases for consistency
- Use application logs to track event processing
- Verify query database is updated after command operations

## Future Enhancements

1. **Event Store**: Implement proper event sourcing with event store
2. **Read Models**: Add more specialized read models for different query patterns
3. **Caching**: Add Redis caching for frequently accessed data
4. **Saga Pattern**: Implement saga pattern for distributed transactions
5. **Event Versioning**: Add event versioning for schema evolution 