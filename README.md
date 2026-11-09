# Tech Challenge - Fase 3 | Sistema Hospitalar

**FIAP - Pós Tech | Arquitetura e Desenvolvimento Java**

Sistema backend para gerenciamento de consultas hospitalares, com agendamento, controle de acesso por perfil e notificações assíncronas.

---

## Arquitetura

O projeto é composto por **2 microserviços** que se comunicam de forma assíncrona via **RabbitMQ**:

```
┌──────────────────────┐                        ┌─────────────────────────┐
│  scheduling-service   │   ── RabbitMQ ──►     │  notification-service    │
│  (porta 8080)         │                        │  (porta 8081)            │
│                       │                        │                          │
│  - Spring Security    │                        │  - Consome mensagens     │
│  - GraphQL            │                        │  - Registra logs         │
│  - CRUD de consultas  │                        │  - API REST de consulta  │
│  - PostgreSQL (5432)  │                        │  - PostgreSQL (5433)     │
└──────────────────────┘                        └─────────────────────────┘
```

### Fluxo principal

1. Um médico ou enfermeiro cria/edita uma consulta via **GraphQL** no `scheduling-service`
2. O `scheduling-service` salva no banco e publica uma mensagem na fila do **RabbitMQ**
3. O `notification-service` consome a mensagem da fila automaticamente
4. O `notification-service` registra um log da notificação no seu banco de dados

---

## Tecnologias utilizadas

| Tecnologia | Finalidade |
|---|---|
| Java 17 | Linguagem principal |
| Spring Boot 3.2.5 | Framework backend |
| Spring Security | Autenticação e autorização (HTTP Basic) |
| Spring GraphQL | Consultas flexíveis ao histórico de consultas |
| Spring AMQP | Integração com RabbitMQ (mensageria assíncrona) |
| Spring Data JPA | Persistência no banco de dados |
| PostgreSQL 15 | Banco de dados relacional |
| RabbitMQ 3 | Message broker para comunicação entre serviços |
| Lombok | Redução de boilerplate (getters, setters, builders) |
| Docker Compose | Orquestração da infraestrutura (banco + RabbitMQ) |

---

## Controle de Acesso (Roles)

O sistema utiliza **Spring Security** com autenticação HTTP Basic e 3 perfis de usuário:

| Role | Permissões |
|---|---|
| **MEDICO** | Visualizar e editar histórico de consultas, criar novas consultas |
| **ENFERMEIRO** | Registrar consultas, acessar histórico completo |
| **PACIENTE** | Visualizar apenas as suas próprias consultas |

---

## Estrutura do Projeto

### scheduling-service (porta 8080)

Serviço principal — responsável por autenticação, agendamento de consultas e exposição via GraphQL.

```
scheduling-service/src/main/java/com/hospital/scheduling/
├── SchedulingApplication.java          # Classe main — ponto de entrada da aplicação
├── config/
│   ├── SecurityConfig.java             # Configuração do Spring Security (autenticação, roles, HTTP Basic)
│   └── RabbitMQConfig.java             # Declara a fila "appointment.notifications" no RabbitMQ
├── model/
│   ├── User.java                       # Entidade JPA — representa a tabela "users" no banco
│   ├── Role.java                       # Enum com os 3 tipos de usuário (MEDICO, ENFERMEIRO, PACIENTE)
│   └── Appointment.java                # Entidade JPA — representa a tabela "appointments" no banco
├── repository/
│   ├── UserRepository.java             # Interface JPA — operações de banco para User (findByEmail, etc.)
│   └── AppointmentRepository.java      # Interface JPA — operações de banco para Appointment (findByPatientId, etc.)
├── dto/
│   ├── UserDTO.java                    # Data Transfer Object — dados de entrada/saída para User
│   └── AppointmentDTO.java             # Data Transfer Object — dados de entrada/saída para Appointment
├── service/
│   ├── UserService.java                # Lógica de negócio para usuários + implementa UserDetailsService (Spring Security)
│   └── AppointmentService.java         # Lógica de negócio para consultas (criar, atualizar, buscar)
├── controller/
│   └── AuthController.java             # REST Controller — endpoints /auth/register e /auth/me
├── graphql/
│   └── AppointmentResolver.java        # Resolver GraphQL — mapeia queries e mutations do schema
└── messaging/
    └── AppointmentMessageProducer.java  # Publica mensagens na fila do RabbitMQ ao criar/editar consultas
```

#### Detalhamento das classes

| Classe | Responsabilidade |
|---|---|
| `SchedulingApplication` | Classe main com `@SpringBootApplication` — inicializa o Spring Boot |
| `SecurityConfig` | Configura Spring Security: desabilita CSRF, define sessão stateless, protege endpoints, habilita HTTP Basic Auth e define o encoder BCrypt para senhas |
| `RabbitMQConfig` | Declara o bean da fila `appointment.notifications` como durável no RabbitMQ |
| `User` | Entidade JPA mapeada para a tabela `users` — campos: id, email (único), password (hash BCrypt), name, role |
| `Role` | Enum com valores MEDICO, ENFERMEIRO, PACIENTE — armazenado como String no banco |
| `Appointment` | Entidade JPA mapeada para a tabela `appointments` — campos: id, doctor (FK), patient (FK), dateTime, description, status |
| `UserRepository` | Interface que estende `JpaRepository` — fornece findByEmail() e existsByEmail() além dos métodos padrão do JPA |
| `AppointmentRepository` | Interface que estende `JpaRepository` — fornece findByPatientId(), findByDoctorId() e findByPatientIdAndDateTimeAfter() |
| `UserDTO` | DTO para transferência de dados de usuário entre camadas (sem expor a entidade diretamente) |
| `AppointmentDTO` | DTO para transferência de dados de consulta — inclui nomes de médico e paciente para exibição |
| `UserService` | Implementa `UserDetailsService` (obrigatório para Spring Security) — método `loadUserByUsername()` carrega o usuário do banco e converte para `UserDetails`. Também registra novos usuários com senha criptografada |
| `AppointmentService` | Lógica de negócio: cria consultas (com status AGENDADA), atualiza consultas, busca por paciente, busca futuras. Após cada criação/edição, publica mensagem no RabbitMQ |
| `AuthController` | Endpoint público POST `/auth/register` para registro de usuários. Retorna o DTO sem a senha |
| `AppointmentResolver` | Resolve as queries e mutations definidas no schema GraphQL. Usa `@PreAuthorize` para controlar acesso por role |
| `AppointmentMessageProducer` | Usa `RabbitTemplate` para enviar mensagens (String) para a fila `appointment.notifications` |

#### Schema GraphQL

```graphql
type Query {
    appointmentsByPatient(patientId: ID!): [Appointment]         # Consultas de um paciente
    futureAppointmentsByPatient(patientId: ID!): [Appointment]   # Apenas consultas futuras
    allAppointments: [Appointment]                                # Todas (só médicos/enfermeiros)
}

type Mutation {
    createAppointment(doctorId: ID!, patientId: ID!, dateTime: String!, description: String): Appointment
    updateAppointment(id: ID!, dateTime: String, description: String, status: String): Appointment
}
```

---

### notification-service (porta 8081)

Serviço de notificações — consome mensagens do RabbitMQ e registra logs.

```
notification-service/src/main/java/com/hospital/notification/
├── NotificationApplication.java        # Classe main — ponto de entrada da aplicação
├── config/
│   └── RabbitMQConfig.java             # Declara a fila "appointment.notifications" no RabbitMQ
├── model/
│   └── NotificationLog.java            # Entidade JPA — registra cada notificação recebida
├── repository/
│   └── NotificationLogRepository.java  # Interface JPA — operações de banco para NotificationLog
├── service/
│   └── NotificationService.java        # Lógica de negócio — processa notificações e salva logs
├── messaging/
│   └── AppointmentMessageConsumer.java # Listener do RabbitMQ — consome mensagens da fila
└── controller/
    └── NotificationController.java     # REST Controller — endpoints para consultar logs de notificações
```

#### Detalhamento das classes

| Classe | Responsabilidade |
|---|---|
| `NotificationApplication` | Classe main com `@SpringBootApplication` — inicializa o Spring Boot na porta 8081 |
| `RabbitMQConfig` | Declara o bean da mesma fila `appointment.notifications` usada pelo scheduling-service |
| `NotificationLog` | Entidade JPA mapeada para a tabela `notification_logs` — campos: id, message, receivedAt, status |
| `NotificationLogRepository` | Interface que estende `JpaRepository` — fornece findByStatus() |
| `NotificationService` | Processa notificações recebidas: salva o log no banco com status ENVIADA e data/hora atual |
| `AppointmentMessageConsumer` | Usa `@RabbitListener` para escutar a fila — quando chega uma mensagem, delega para o NotificationService |
| `NotificationController` | Endpoints REST: GET `/notifications` (lista todos os logs), GET `/notifications/status/{status}` (filtra por status) |

---

## Pré-requisitos

- Java 17+
- Maven 3.8+
- Docker e Docker Compose

---

## Como executar

### 1. Subir a infraestrutura (PostgreSQL + RabbitMQ)

```bash
# Na raiz do projeto (onde está o docker-compose.yml)
docker-compose up -d
```

Isso inicia:
- **PostgreSQL** (scheduling) na porta `5432`
- **PostgreSQL** (notification) na porta `5433`
- **RabbitMQ** na porta `5672` (painel web em `http://localhost:15672`, login: guest/guest)

### 2. Iniciar o scheduling-service

```bash
cd scheduling-service
mvn spring-boot:run
```

O serviço estará disponível em `http://localhost:8080`

### 3. Iniciar o notification-service

```bash
cd notification-service
mvn spring-boot:run
```

O serviço estará disponível em `http://localhost:8081`

---

## Endpoints da API

### scheduling-service (porta 8080)

#### Autenticação

| Método | Endpoint | Descrição | Autenticação |
|---|---|---|---|
| POST | `/auth/register` | Registra um novo usuário | Pública |

**Exemplo — Registrar usuário:**

```json
POST http://localhost:8080/auth/register
Content-Type: application/json

{
    "email": "dr.silva@hospital.com",
    "password": "123456",
    "name": "Dr. Silva",
    "role": "MEDICO"
}
```

#### GraphQL

| Endpoint | Descrição |
|---|---|
| POST `/graphql` | Endpoint GraphQL (requer autenticação HTTP Basic) |
| GET `/graphiql` | Interface web para testar queries GraphQL |

**Exemplo — Criar consulta (via GraphQL):**

```graphql
mutation {
    createAppointment(
        doctorId: 1,
        patientId: 3,
        dateTime: "2024-12-25T10:30:00",
        description: "Consulta de rotina"
    ) {
        id
        doctorName
        patientName
        dateTime
        status
    }
}
```

**Exemplo — Listar consultas de um paciente:**

```graphql
query {
    appointmentsByPatient(patientId: 3) {
        id
        doctorName
        dateTime
        description
        status
    }
}
```

**Exemplo — Listar consultas futuras:**

```graphql
query {
    futureAppointmentsByPatient(patientId: 3) {
        id
        doctorName
        dateTime
        status
    }
}
```

**Exemplo — Atualizar consulta:**

```graphql
mutation {
    updateAppointment(
        id: 1,
        status: "REALIZADA"
    ) {
        id
        status
    }
}
```

### notification-service (porta 8081)

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/notifications` | Lista todas as notificações registradas |
| GET | `/notifications/status/{status}` | Lista notificações filtradas por status (ENVIADA, FALHA, PENDENTE) |

---

## Configuração dos bancos de dados

| Serviço | Banco | Porta | Usuário | Senha |
|---|---|---|---|---|
| scheduling-service | scheduling_db | 5432 | admin | admin123 |
| notification-service | notification_db | 5433 | admin | admin123 |

---

## Comunicação assíncrona (RabbitMQ)

- **Fila:** `appointment.notifications`
- **Producer:** `AppointmentMessageProducer` (scheduling-service) — publica mensagens ao criar ou editar consultas
- **Consumer:** `AppointmentMessageConsumer` (notification-service) — consome mensagens e registra logs

**Painel de gerenciamento:** `http://localhost:15672` (login: guest/guest)

---

## Testes

Para compilar e rodar os testes (requer que a infraestrutura Docker esteja rodando):

```bash
# scheduling-service
cd scheduling-service
mvn test

# notification-service
cd notification-service
mvn test
```

Para compilar sem rodar os testes:

```bash
mvn install -DskipTests
```
