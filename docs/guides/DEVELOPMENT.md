# Development Guide

This document explains how to run the Smart Clinic Management System locally and using Docker.

## Prerequisites

Before running the application, install:

- Java 17
- Maven 3.9+
- Docker
- Docker Compose
- MySQL (only for local execution)
- MongoDB (only for local execution)

---

## Running with Docker

### 1. Configure environment variables

Create a `.env` file in the project root

You can use `.env.example` as a template:

```bash
cp .env.example .env
```

Update the values according to your environment.


### 2. Build and start containers

Run:
```bash
docker compose up --build
```

or run in background:
```bash
docker compose up --build -d
```

The following services will start:

| Services    | Port  |
|-------------|-------|
| Spring Boot | 8080  |
| MySQL       | 3306  |
| MongoDB     | 27017 |


### 3. Check running containers
```bash
docker ps
```

Expected containers:
```
smart-clinic-app
smart-clinic-mysql
smart-clinic-mongodb
```

### 4. Application URL

Open the application in the browser
```
http://localhost:8080
```

Health check:
```
http://localhost:8080/actuator/health
```

## Docker Useful Commands

**View Logs**

Application:
```bash
docker logs -f smart-clinic-app
```

All services:
```bash
docker compose logs -f
```

### Stop Application
```bash
docker compose down
```

### Remove containers and databases
⚠️This deletes persisted data
```bash
docker compose down -v
```

## Running Locally

### 1. Start Databases

Start MySQL and MongoDB locally

Required services:
```
MySQL
localhost:3306

MongoDB
localhost:27017
```

## 2. Configure environment variables

**Without Spring Profiles**

Configure environment variables
Export variables:
Mac/Linux:
```bash
export DB_URL=jdbc:mysql://localhost:3306/cms
export DB_USER=root
export DB_PASSWORD=root
export MONGO_URI=mongodb://localhost:27017/prescriptions
export JWT_SECRET=your_base64_secret
```
**Using spring dev profile**

Update the _application-dev.properties_ file with your local configuration.

### 3. Run application

Using Maven (variables exported):
```bash
mvn spring-boot:run
```

With dev profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Running Tests

### Unit Tests
Run:
```
mvn test
```

### Integration Tests
Docker daemon must be running because integration tests use Testcontainers.

Run:
```bash
mvn verify
```

## Makefile

A Makefile is available to simplify Docker commands.

### Start
Build and start the application in the background
```
make up
```

### Stop
```bash
make down
```

### Restart
```bash
make restart
```

### Logs
```bash
make logs
```

### Check containers
```bash
make ps
```

### Clean
Remove the containers and volumes
```bash
make clean
```
⚠️This deletes persisted data

## Miscellaneous

> The following container names are fixed in `docker-compose.yml`:
>
> - `smart-clinic-app`
> - `smart-clinic-mysql`
> - `smart-clinic-mongodb`

---

### Generate JWT Secret
Generate a Base64 encoded secret:
Mac/Linux:
```bash
openssl rand -base64 32
```

---

### Manage MySQL container
Connect to the MySQL container:
```bash
docker exec -it smart-clinic-mysql mysql -uroot -p
```

### Manage MongoDB container

Connect to the MongoDB container
```bash
docker exec -it smart-clinic-mongodb mongosh
```

List databases:
```bash
show dbs
```

Change database:
```bash
use prescriptions
```

---

### Start Application Container Shell

Open a shell inside the application container:

```bash
docker exec -it smart-clinic-app sh
```

Inside the container, you can check environment variables:

```bash
echo $JWT_SECRET
```

You can also inspect other configuration values:

```bash
echo $DB_URL
echo $DB_USER
echo $MONGO_URI
```
