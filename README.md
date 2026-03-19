# TestMentor Backend

Backend part of the **TestMentor** project.  
Built with **Spring Boot**, **Spring Security**, **JPA**, **JWT**, and **PostgreSQL**.

## Overview

The backend provides REST API endpoints for:

- user registration and authentication
- role-based access control
- quiz creation and retrieval
- AI-based quiz generation
- mentor moderation workflow
- quiz submission and result calculation
- attempt statistics
- admin management features

## Tech stack

- Java 17
- Spring Boot 4
- Spring Web
- Spring Data JPA
- Spring Security
- JWT (`jjwt`)
- ModelMapper
- PostgreSQL
- Maven

## Requirements

Before running the backend, install:

- **Java 17**
- **Maven**
- **PostgreSQL**

Check versions:

```bash
java -version
mvn -version
```

## Database setup

Create a PostgreSQL database named `testmentor`.

```sql
CREATE DATABASE testmentor;
```

The backend uses these database settings from `application.properties`:

- host: `localhost`
- port: `5432`
- database: `testmentor`
- username: `postgres`
- password: `${DB_PASSWORD}`

## Environment variables

Set these variables before starting the backend:

- `DB_PASSWORD` — PostgreSQL password
- `JWT_SECRET` — secret key used for JWT signing
- `OPENROUTER_API_KEY` — API key for AI quiz generation

### PowerShell example

```powershell
$env:DB_PASSWORD="your_postgres_password"
$env:JWT_SECRET="your_jwt_secret"
$env:OPENROUTER_API_KEY="your_openrouter_api_key"
```

### CMD example

```cmd
set DB_PASSWORD=your_postgres_password
set JWT_SECRET=your_jwt_secret
set OPENROUTER_API_KEY=your_openrouter_api_key
```

## How to run

From the backend folder:

```bash
cd testmentor-backend
mvn spring-boot:run
```

The server starts on:

```text
http://localhost:8080
```

## Build the project

```bash
mvn clean package
```

Generated JAR will be placed in:

```text
target/
```

## Main configuration

Important properties from `src/main/resources/application.properties`:

- `server.port=8080`
- `spring.datasource.url=jdbc:postgresql://localhost:5432/testmentor`
- `spring.jpa.hibernate.ddl-auto=update`
- `openrouter.model=meta-llama/llama-3.1-8b-instruct`

## Authentication and roles

Authentication is based on **JWT**.

Supported roles in the project:

- `ROLE_STUDENT`
- `ROLE_MENTOR`
- `ROLE_ADMIN`

If a new user is registered without an explicit role, the backend assigns **student** by default.

## API overview

### Auth
- `POST /api/auth/addNewUser` — register user
- `POST /api/auth/signing` — login and receive token

### Quizzes
- `GET /api/quizzes` — list quizzes
- `GET /api/quizzes/{id}` — get a quiz by id
- `POST /api/quizzes` — create a quiz
- `POST /api/quizzes/{id}/submit` — submit quiz answers

### AI
- `POST /api/ai/quizzes/generate` — generate quiz content with AI

### Mentor moderation
- `GET /api/mentor/review` — quizzes pending review
- `POST /api/mentor/review/{quizId}/vote` — approve/reject vote

### Attempts and stats
- `POST /api/attempts` — save user attempt
- `GET /api/attempts/stats?quizId={id}` — get statistics for a quiz

### Admin
- role management endpoints
- quiz/user management endpoints

## Development notes

- Hibernate schema update is enabled with `ddl-auto=update`
- CORS and security are configured in the `config` package
- AI generation depends on a valid OpenRouter key
- PostgreSQL is the main expected database for local development

## Common issues

### 1. Database connection error
Check that:

- PostgreSQL is running
- database `testmentor` exists
- `DB_PASSWORD` is correct

### 2. Java version error
Make sure Java 17 is installed:

```bash
java -version
```

### 3. AI generation does not work
Usually this means:

- `OPENROUTER_API_KEY` is missing
- the key is invalid
- the external API is unavailable

## Author

Backend for the project **TestMentor** by **Saya Amangeldinova**.
