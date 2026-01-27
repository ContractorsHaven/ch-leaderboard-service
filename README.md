# ch-leaderboard-service

A Spring Boot microservice for the ContractorsHaven platform.

## Overview

This service provides leaderboard functionality for the ContractorsHaven platform.

## Tech Stack

- Java 21
- Spring Boot 4
- Spring WebFlux (reactive)
- Apache Kafka
- Kubernetes/Helm

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- Docker (for local development)

### Local Development

```bash
# Build
./mvnw clean install

# Run with dev profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Run tests
./mvnw test
```

### Docker

```bash
# Build image
docker build -t ch-leaderboard-service:latest .

# Run with docker-compose
docker-compose up -d
```

## API Documentation

- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Scalar UI: `http://localhost:8080/docs`

## Configuration

| Property | Description | Default |
|----------|-------------|---------|
| `server.port` | HTTP server port | 8080 |
| `spring.kafka.bootstrap-servers` | Kafka bootstrap servers | localhost:9092 |

## Deployment

### Kubernetes

```bash
helm install ch-leaderboard-service helmchart -n contractors-haven
```

### ArgoCD

Apply the ArgoCD application manifest:

```bash
kubectl apply -f argocd/application.yaml
```

## Health Checks

- Liveness: `GET /actuator/health/liveness`
- Readiness: `GET /actuator/health/readiness`
- Metrics: `GET /actuator/prometheus`
