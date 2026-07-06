# Task Manager API

API REST para gerenciamento de tarefas com autenticação JWT, construída com Spring Boot 3 e PostgreSQL.

## Stack

- Java 21
- Spring Boot 3
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Docker & Docker Compose
- JUnit 5 + Mockito
- Swagger/OpenAPI

## Como rodar

### 1. Clone o repositório

**Linux/Mac:**
```bash
git clone <https://github.com/cherohn/task-manager>
cd task-manager
```

**Windows (PowerShell):**
```powershell
git clone <https://github.com/cherohn/task-manager>
cd task-manager
```

### 2. Configure as variáveis de ambiente

**Linux/Mac:**
```bash
cp .env.example .env
# edite o .env com suas credenciais
```

**Windows (PowerShell):**
```powershell
Copy-Item .env.example .env
# edite o .env com suas credenciais
```

### 3. Suba os containers

Esse comando é o mesmo em qualquer sistema operacional, pois roda dentro do Docker:

```bash
docker-compose up --build
```

### 4. Acesse a aplicação

Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Uma collection do Postman pronta está disponível em `postman/task-manager-api.postman_collection.json`.

## Endpoints

| Método | Rota | Auth | Descrição |
|---|---|---|---|
| POST | /auth/register | Não | Cadastra novo usuário |
| POST | /auth/login | Não | Autentica e retorna token |
| GET | /tasks | Sim | Lista tarefas do usuário |
| POST | /tasks | Sim | Cria nova tarefa |
| GET | /tasks/{id} | Sim | Busca tarefa por ID |
| PUT | /tasks/{id} | Sim | Atualiza tarefa |
| DELETE | /tasks/{id} | Sim | Remove tarefa |
| GET | /tasks/all | Sim (ADMIN) | Lista todas as tarefas |

## Decisões Técnicas

- **JWT stateless em vez de sessão**: elimina a necessidade de armazenamento de sessão no servidor, permitindo escalar horizontalmente sem sticky sessions.
- **UUID em vez de Long sequencial para IDs**: evita expor volume de dados e previne enumeration attacks.
- **BCrypt para hash de senha**: aplica salt automático por registro, tornando ataques de rainbow table inviáveis.
- **Atualização parcial (PUT com campos opcionais)**: evita forçar o cliente a reenviar o objeto inteiro para alterar um único campo.
- **`ddl-auto=update` em vez de migrations**: aceitável para portfólio; próxima melhoria seria migrar para Flyway/Liquibase antes de qualquer uso em produção real.
