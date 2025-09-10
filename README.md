# Transfer Microservice

This repository contains the Transfer Spring Boot microservices:

- **Transfer Service** – Handles client-facing transfer requests and delegates to the Ledger Service.

Please note this service cannot run without running the ledger service first.

---

## Prerequisites

- [Maven 3.9+](https://maven.apache.org/)
- [Java 21](https://adoptium.net/)
- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

---

## Building the JARs

From the project root, run:
This will build the JARs for both services under:

```bash
  mvn clean install 
```

This will generate the executable jar file transfer-service/target/transfer-service-<version>.jar

from there you run

```bash
  docker-compose up --build 
```

This will start:

Postgres on port 5432

Transfer Service on port 8080

Then you will be able to access the api docs on http://localhost:8080/swagger-ui.html