# Architecture summary

The Smart Clinic Management System's web application follows the three-tier architecture model, achieved by combining
MVC and REST architecture styles within a Spring Boot application.

## Presentation Tier
The **Presentation tier** is the user interface layer that contains server-rendered views (HTML pages) and Rest API that for facilitate easlily integration with external consumers in a lightweight format (JSON). HTML page uses thymeleaf for rendering dynamically HTML pages by MVC Controller and Rest API uses RestControllers of SpringBoot. 
    - MVC Controller: Thymeleaf is used for entired server-side dynamic html view pages
    - Rest Controller: use for Restful API, that allows external integrations, usign lightweight HTTP and JSON.

## Application tier
The **Application tier**: is the heart of the application, it contains the controller (entry points) and the service layer (business logic). It is implemented in Spring Boot simplifying the back-end development, because it supports Spring MVC, REST API, Spring DATA JPA and Spring DATA MongoDB.
Spring Boot are also easily containerized using Docker and can be integrate with modern CI or CD pipelines. 

## Data Tier
The **Data tier** is for persitting and accessing data, and uses databases for structured Data and Document-based data.
- MySQL is used for structured, relational data (patients, doctors, appointments), ensuring consistency and integrity.
- MongoDB is used for prescription data, which benefits from a flexible, document-based schema due to its variable structure and potential evolution over time.

This separation allows the system to balance strong consistency for core entities with flexibility for less structured medical data.

## Workflow
### 1. User interface layer
    The Presentation Tier handles user interaction and external communication. It supports two interaction models:
        - Server-side rendering (MVC Controller): HTML pages rendered on the server and delivered to the browser (this is where thymeleaf comes in).
        - RESTful API (REST Controllers): frontend modules such as appointments, patient and doctors dashboard that interact with the backend via lightweight HTTP and JSON responses.

### 2. Controller layer
    Received the request from user interacting with the user interface e.g (clicking a button, submitting a form) based on the url path and http method
        - Server-rendered views handled by MVC Controllers, it uses thymeleaf for returning dynamic HTML.
        - Requests from API consumers handled by REST Controllers, return responses in json format.

### 3. Service layer:
    Controllers delegate logic to the services layer to apply business logic, coordinate workflow across the entities, and ensure a clean separation between controller logic and data access.

### 4. Repository layer:
    The service layer communicates with the repository layer to perform data access operations. In our case we have two types of repositories, one for structured and relational data (using MYSQL) and the other for Document-based data (using MongoDB).
        - Spring Data JPA is used for MySQL
        - Spring Data MongoDB is used for MongoDB

### 5. Database access: 
    Each repository interfaces interact directly with the underlying database engine (MySQL or MongoDB).
        - MySQL (structured data) such as: Patient, Appointment and Doctor
        - MongoDB (unstructured document-base data) such as: prescriptions.

### 6. Model Binding:
    Once data is retrieved from the database is mapped into Java Model Classes. In case of Structured-relational data (MySQL) is mapped into JPA Entities (annotated as @Entity), and for Document-based data (MongoDB) is loaded into document objects (typically annotated @Document), which map to BSON or JSON structures and collections.

### 7. Application model
    The bound models are used in the response layer, that are passed from the controller to thymeleaf templates for the MVC flows where they are rendered as dynamic HTML for the browser. In REST flows the same model (or DTOs) are serialized into JSON and sent back to the client.

## Reference:
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Data Mongo DB](https://spring.io/projects/spring-data-mongodb)
- [Thymeleaf](https://www.thymeleaf.org/)
- [REST API Design](https://restfulapi.net/)
