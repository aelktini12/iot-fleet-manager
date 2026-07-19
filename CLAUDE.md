# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

A Spring Boot 3 REST API backend for managing a fleet of IoT devices (sensors, actuators, gateways) — centralizing telemetry and status monitoring for hardware deployed in the field. Designed to be consumed by an Angular frontend (CORS is pre-configured for `http://localhost:4200`).

## Commands

**Database** (PostgreSQL required, not bundled — must be running before the app starts):
```bash
docker run --name pg-iot -e POSTGRES_USER=myuser -e POSTGRES_PASSWORD=mypassword -e POSTGRES_DB=iot_fleet -p 5432:5432 -d postgres
```
Note: `src/main/resources/application.properties` currently points at `jdbc:postgresql://localhost:5432/iot_db` with user/password `postgres`/`password` — reconcile these with whatever container/credentials you actually run locally.

**Build & run:**
```bash
./mvnw spring-boot:run       # run the app (localhost:8080)
./mvnw clean install         # full build
./mvnw test                  # run all tests
./mvnw test -Dtest=ClassName#methodName   # run a single test
```

**API docs:** `http://localhost:8080/swagger-ui/index.html` (springdoc-openapi).

**Manual API testing:** `src/main/resources/device-requests.http` has example requests (usable with an HTTP client plugin). `docs/device-create.json` has a sample device payload.

- Java 21, `spring.jpa.hibernate.ddl-auto=update` — schema is auto-managed by Hibernate from entity annotations, there are no migration scripts (no Flyway/Liquibase).
- `spring.jpa.show-sql=true` and SQL bind-param TRACE logging are on by default for local dev.

## Architecture

Standard layered Spring MVC structure, single-entity domain (`Device`) at `com.elktini.iotfleet`:

```
controller/  → DeviceController      REST endpoints under /api/v1/devices
service/     → DeviceService         business rules, orchestrates repository
repository/  → DeviceRepository      Spring Data JPA, derived queries only
model/       → Device                JPA entity + Jakarta Validation annotations
exception/   → GlobalExceptionHandler + custom exceptions
config/      → CorsConfig
```

Key behaviors worth knowing before changing service/controller code:

- **Validation lives on the entity itself** (`Device`), via Jakarta annotations (`@NotBlank`, `@Pattern`, `@Size`). Serial numbers must match `^SN-[A-Z0-9\-]{3,15}$`; `type` must be one of `SENSOR`, `ACTUATOR`, `GATEWAY`. `@Valid` on controller request bodies triggers these.
- **`DeviceService.createDevice`**: defaults `status` to `OFFLINE` when absent, and rejects duplicate serial numbers via `DeviceRepository.existsBySerialNumber` (throws `DeviceStateConflictException` → HTTP 409) rather than relying on the DB unique-constraint failure.
- **`DeviceService.deleteDevice`**: refuses to delete a device whose `status` is `ONLINE` (also `DeviceStateConflictException` → 409). This is a deliberate business rule, not an oversight — don't remove it without confirming with the user.
- **`lastCommunication`** is a Hibernate `@UpdateTimestamp` field — it's set automatically on every save, not manually assigned in service code.
- **Error handling is centralized** in `GlobalExceptionHandler` (`@RestControllerAdvice`): `MethodArgumentNotValidException` → 400 with a field→message map, `ResourceNotFoundException` → 404, `DeviceStateConflictException` → 409. New service-layer failure modes should generally get a new custom exception + handler here rather than ad-hoc try/catch in controllers.
- **Repository queries are Spring Data derived methods only** (`findBySerialNumber`, `existsBySerialNumber`) — no `@Query`/JPQL yet.
