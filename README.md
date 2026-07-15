# Smart Clinic Management System

A full-stack clinic management application built with Java and Spring Boot. It provides role-based workflows for administrators, doctors, and patients, including doctor management, appointment scheduling, availability management, and electronic prescriptions.

This project was developed as the capstone for the IBM Java Developer Professional Certificate and extended with additional business rules, automated tests, containerization, and CI-oriented build configuration.

## Features

### Administrator

- Sign in to a protected admin dashboard
- Create, update, delete, list, and filter doctors
- Manage doctor contact information, specialties, and available time slots

### Doctor

- Sign in to a protected doctor dashboard
- View appointments by date and patient name
- Manage available time slots
- Create a prescription for an assigned appointment

### Patient

- Register and sign in
- Browse and filter doctors by name, specialty, and availability
- Book and reschedule one-hour appointments
- View appointments and prescriptions

### Business rules

- Appointments cannot be scheduled in the past
- A doctor must be available during the selected time slot
- Doctors and patients cannot have overlapping appointments
- Only the patient who owns an appointment can update it
- Only the assigned doctor can create its prescription
- Email addresses are unique per user type
- Request data is validated and API errors use consistent responses

## Tech Stack

| Area | Technologies |
| --- | --- |
| Backend | Java 17, Spring Boot 3.4.4, Spring MVC |
| Persistence | Spring Data JPA, Hibernate, MySQL 8, Spring Data MongoDB, MongoDB 6 |
| Security | Spring Security, BCrypt, JWT (JJWT 0.12.6) |
| Frontend | Thymeleaf, HTML, CSS, JavaScript |
| Testing | JUnit 5, Spring Boot Test, MockMvc, Testcontainers |
| Infrastructure | Maven, Docker, Docker Compose, Spring Boot Actuator |

## Architecture

The application follows a layered architecture:

- **Presentation layer:** Thymeleaf views, static JavaScript modules, MVC controllers, and REST controllers
- **Application layer:** services, business rules, DTO mapping, validation, and exception handling
- **Data layer:** JPA repositories for relational data and MongoDB repositories for prescriptions

MySQL stores administrators, doctors, patients, appointments, and doctor availability. MongoDB stores prescription documents associated with appointments.

## Project Structure

```text
java-database-capstone/
├── app/
│   ├── src/main/java/          # Controllers, services, models, DTOs and repositories
│   ├── src/main/resources/
│   │   ├── static/             # CSS and JavaScript
│   │   ├── templates/          # Thymeleaf views
│   │   └── application*.properties
│   ├── src/test/java/          # Unit and integration tests
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── pom.xml
├── user_stories.md
└── README.md
```

## Getting Started with Docker

### Prerequisites

- Docker Engine or Docker Desktop
- Docker Compose

### 1. Clone the repository

```bash
git clone https://github.com/maikelsalazar/java-database-capstone.git
cd java-database-capstone/app
```

### 2. Create the environment file

Create an `app/.env` file:

```dotenv
DB_URL=jdbc:mysql://mysql:3306/cms
DB_USER=root
DB_PASSWORD=root
MONGO_URI=mongodb://mongodb:27017/prescriptions
JWT_SECRET=replace-with-a-base64-encoded-256-bit-secret
ADMIN_USERNAME=admin
ADMIN_PASSWORD=admin@1234
```

Generate a suitable JWT secret on macOS or Linux:

```bash
openssl rand -base64 32
```

Do not commit the `.env` file or real credentials.

### 3. Start the application

```bash
docker compose up --build
```

Open [http://localhost:8080](http://localhost:8080).

The default services are:

| Service | Address |
| --- | --- |
| Application | `http://localhost:8080` |
| MySQL | `localhost:3306` |
| MongoDB | `localhost:27017` |

To run in the background or stop the stack:

```bash
docker compose up --build -d
docker compose down
```

Use `docker compose down -v` only when you also want to delete the database volumes.

## Local Development

### Prerequisites

- JDK 17
- Maven 3.9+
- MySQL 8
- MongoDB 6

From the `app` directory, configure the required environment variables and run:

```bash
./mvnw spring-boot:run
```

If the Maven wrapper is unavailable, use:

```bash
mvn spring-boot:run
```

## Testing

Docker must be running because the integration tests use Testcontainers for MySQL and MongoDB.

Run unit tests:

```bash
mvn test
```

Run the complete test suite, including integration tests:

```bash
mvn verify
```

Integration test classes use the `*IT.java` suffix and are executed by Maven Failsafe. Unit test classes use `*Test.java` and are executed by Maven Surefire.

## Health Check

When the application is running, its health information is available at:

```text
GET http://localhost:8080/actuator/health
```

## API Overview

The application exposes REST endpoints under `/api/` for:

- Authentication for administrators, doctors, and patients
- Doctor creation, update, deletion, listing, and filtering
- Patient registration and appointment retrieval
- Appointment booking, filtering, and rescheduling
- Prescription creation and retrieval

The frontend consumes these endpoints and uses Thymeleaf for server-rendered dashboard pages.

## Security Note

Some authenticated routes receive the JWT as a path parameter because this was part of the original capstone specification. In a production system, tokens should be sent through the `Authorization: Bearer <token>` header, HTTPS should be mandatory, secrets should be managed outside the repository, and production database credentials should use least privilege.

## Documentation

Detailed functional requirements, Gherkin scenarios, acceptance criteria, priorities, and story points are available in [user_stories.md](user_stories.md).

## Author

**Maikel José Salazar Fermín**

- [GitHub](https://github.com/maikelsalazar)
- [LinkedIn](https://www.linkedin.com/in/maikelsalazar/)
