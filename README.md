# IoT Fleet Manager - Backend API

A robust REST API for managing a fleet of connected devices (sensors, actuators, gateways). This project serves as a backend foundation to centralize telemetry and status monitoring for hardware deployed in the field.

## Features
* **Full CRUD operations** for device lifecycle management.
* **Strict data validation** (Jakarta Validation) to ensure database integrity.
* **Global exception handling** providing standardized JSON error responses.
* **Interactive API documentation** powered by Swagger/OpenAPI.
* **Automated traceability** for the latest sensor communications using Hibernate Entity Lifecycle.

## Tech Stack
* **Language:** Java
* **Framework:** Spring Boot 3 (Web, Data JPA, Validation)
* **Database:** PostgreSQL
* **Tools:** Docker, Maven

## How to run locally
1. **Start the database via Docker:**
   ```bash
   docker run --name pg-iot -e POSTGRES_USER=myuser -e POSTGRES_PASSWORD=mypassword -e POSTGRES_DB=iot_fleet -p 5432:5432 -d postgres
   ```

2. **Build and run the Spring Boot application:**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Access the Swagger documentation:**
   Open your browser and navigate to: `http://localhost:8080/swagger-ui/index.html`