# ACME Employee Salary Management

Backend foundation for the ACME Employee Salary Management MVP.

## Prerequisites

- Java 21
- Maven 3.9+ (or a Maven wrapper, once generated)
- Docker Desktop for local PostgreSQL

## Configure environment

Copy `.env.example` to `.env` and review the local values. The Spring Boot application reads database, JWT, CORS, and server settings from environment variables. Never commit real secrets.

PowerShell example:

```powershell
Copy-Item .env.example .env
$env:DB_URL = "jdbc:postgresql://localhost:5432/salarymanagement"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "postgres"
$env:JWT_SECRET = "replace-with-a-long-random-secret-for-local-development"
$env:SPRING_PROFILES_ACTIVE = "local"
```

## Start PostgreSQL

From the repository root:

```powershell
docker compose up -d postgres
```

The local database is available at `localhost:5432`. Flyway runs automatically when the backend starts. No domain migration is included in this foundation phase.

## Start the backend

```powershell
cd backend
mvn spring-boot:run
```

The application starts on `http://localhost:8080`. Health is available at `GET http://localhost:8080/actuator/health`.

## Run tests

```powershell
cd backend
mvn test
```

Tests use the `test` profile and an in-memory H2 database, so they do not require PostgreSQL or Docker.

## Current scope

This phase establishes the Spring Boot runtime, environment-based configuration, Flyway/JPA/PostgreSQL wiring, validation and error foundations, CORS, stateless security scaffolding, and application-startup testing. Employee CRUD, salary management, dashboard logic, and authentication business logic are intentionally deferred to later phases.
