# Sistema de Autenticação JWT com Spring Security

Aplicação Spring Boot para gerenciamento de tweets com sistema de autenticação JWT, desenvolvida com arquitetura em camadas e REST API.

## 🚀 Tecnologias

- **Java 21**
- **Spring Boot 3.5.7**
- **Spring Security** - Segurança e autenticação
- **Spring Data JPA / Hibernate**
- **OAuth2 Resource Server** - Suporte a JWT
- **PostgreSQL** (desenvolvimento)
- **Docker & Docker Compose**
- **Maven**

## 📋 Funcionalidades

O sistema gerencia:
- **Autenticação**: Sistema de login com JWT e criptografia de senhas
- **Usuários**: Cadastro e gerenciamento de usuários com roles
- **Roles**: Sistema de permissões (BASIC e ADMIN)
- **Tweets**: Criação, listagem e remoção de tweets
- **Feed**: Feed paginado de tweets ordenado por data
- **Controle de Acesso**: Permissões baseadas em roles e propriedade

## 🏗️ Arquitetura

Aplicação estruturada em camadas:

```
├── entities/          # Entidades JPA (User, Tweet, Role)
├── repositories/      # Interfaces JPA Repository
├── controllers/       # Controllers REST API
├── dto/              # Data Transfer Objects
├── config/           # Configurações (Security, DataSource, Admin)
└── resources/        # Arquivos de configuração e chaves JWT
```

## 🗄️ Modelo de Dados

Principais entidades:
- **User**: Usuários do sistema com UUID como identificador
- **Role**: Perfis de acesso (admin, basic) com relação Many-to-Many
- **Tweet**: Posts dos usuários com timestamp automático

### Relacionamentos

- **User ↔ Role**: Many-to-Many (tb_users_roles)
- **User ↔ Tweet**: One-to-Many

## 🐳 Executando com Docker

1. **Clone o repositório**
```bash
git clone <url-do-repositorio>
cd springsecurity
```

2. **Execute com Docker Compose**
```bash
docker-compose up --build
```

A aplicação estará disponível em `http://localhost:8080`

O banco PostgreSQL será iniciado automaticamente na porta `5432`.

## 💻 Executando Localmente

### Pré-requisitos
- Java 21
- Maven 3.6+
- PostgreSQL
- Docker (opcional, apenas para o banco)

### Configuração

1. **Configure as variáveis de ambiente** (se necessário):
   - `SPRING_DATASOURCE_URL`: jdbc:postgresql://localhost:5432/dbspringsecurity
   - `SPRING_DATASOURCE_USERNAME`: postgres
   - `SPRING_DATASOURCE_PASSWORD`: CHANGE-ME
   - `SPRING_JPA_HIBERNATE_DDL_AUTO`: update

    
2. **Inicie o banco PostgreSQL**:
```bash
docker-compose up -d postgresdb
```

3. **Execute a aplicação**
```bash
mvn spring-boot:run
```

### Perfis

- **prod**: Configuração de produção (perfil padrão)
- **dev**: Configuração de desenvolvimento com SQL logging

Para alterar o perfil, edite `application.properties` ou use:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 📦 Build

Para gerar o JAR executável:
```bash
mvn clean package
```

O arquivo será gerado em `target/springsecurity-0.0.1-SNAPSHOT.jar`

## 🔧 Endpoints REST API

### Base URL
```
http://localhost:8080
```

### 🔐 Autenticação (`/login`)

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/login` | Realiza login e retorna JWT | Público |

**Request Body:**
```json
{
  "username": "usuario",
  "password": "senha123"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGc...",
  "expiresIn": 300
}
```

### 👤 Usuários (`/users`)

| Método | Endpoint | Descrição | Auth | Role |
|--------|----------|-----------|------|------|
| POST | `/users` | Cria novo usuário | Público | - |
| GET | `/users` | Lista todos usuários | Bearer Token | ADMIN |

### 📝 Tweets (`/tweets`)

| Método | Endpoint | Descrição | Auth | Permissão |
|--------|----------|-----------|------|-----------|
| GET | `/tweets?page=0&pageSize=10` | Feed paginado | Bearer Token | Autenticado |
| POST | `/tweets` | Cria novo tweet | Bearer Token | Autenticado |
| DELETE | `/tweets/{id}` | Remove tweet | Bearer Token | Próprio ou ADMIN |

## 🔐 Segurança

### Autenticação JWT
- Tokens RS256 com chaves RSA pública/privada
- Tempo de expiração: 5 minutos (300 segundos)
- Sessões stateless
- CSRF desabilitado para API REST - dev
- CSRF habilitado para API REST - prod

### Criptografia
- Senhas criptografadas com BCrypt
- Salt automático gerado para cada senha

### Controle de Acesso
- `@PreAuthorize` para controle de endpoints
- Roles dinâmicas no token JWT
- Validação de propriedade para DELETE de tweets

### Roles Disponíveis

| Role | ID | Descrição |
|------|----|-----------|
| ADMIN | 1 | Permissões administrativas completas |
| BASIC | 2 | Permissões padrão para usuários |

### Usuário Administrador Padrão
- **Username**: `admin`
- **Password**: `123`
- **Role**: ADMIN

Este usuário é criado automaticamente na primeira inicialização da aplicação.

## 📝 Estrutura do Projeto

```
springsecurity/
├── src/
│   ├── main/
│   │   ├── java/com/gustavo/springsecurity/
│   │   │   ├── config/        # Configurações (Security, DataSource, Admin)
│   │   │   ├── controller/    # Controllers REST (Token, Tweet, User)
│   │   │   ├── dto/          # Data Transfer Objects
│   │   │   ├── entities/     # Entidades JPA (User, Tweet, Role)
│   │   │   ├── repository/   # Repositórios Spring Data
│   │   │   └── SpringsecurityApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       ├── data.sql       # Inicialização de roles
│   │       └── app.pub        # Chave pública RSA
│   └── test/                 
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

## 🗄️ Banco de Dados

### PostgreSQL

- **Host**: localhost:5432
- **Database**: dbspringsecurity
- **User**: postgres
- **Password**: CHANGE-ME

### Tabelas

| Tabela | Descrição |
|--------|-----------|
| `tb_users` | Usuários do sistema |
| `tb_tweets` | Tweets postados |
| `tb_roles` | Roles de acesso |
| `tb_users_roles` | Relação N:N entre usuários e roles |

### Inicialização Automática

- **data.sql**: Cria roles ADMIN e BASIC na primeira execução
- **AdminUserConfig**: Cria usuário admin automaticamente

## 🛠️ Configurações Importantes

### Variáveis de Ambiente (Docker)

| Variável | Valor                                              | Descrição |
|----------|----------------------------------------------------|-----------|
| `SPRING_DATASOURCE_URL` | jdbc:postgresql://postgresdb:5432/dbspringsecurity | URL do banco |
| `SPRING_DATASOURCE_USERNAME` | postgres                                           | Usuário do banco |
| `SPRING_DATASOURCE_PASSWORD` | CHANGE-ME                                          | Senha do banco |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | create                                             | Estratégia DDL |

### Hibernate

- **DDL Mode**: Configurado via variável de ambiente
- **SQL Logging**: Ativado no perfil dev/prod
- **SQL Format**: Habilitado no perfil dev/prod

### JWT

- **Chaves**: RSA pública/privada em `src/main/resources/`
- **Algoritmo**: RS256
- **Issuer**: backend-Spring-Security
- **Token Claims**: subject (userId), scope (roles), issued_at, expires_at

### Postman

<img src="springSecurity.png" alt="Rotas do Crud!">