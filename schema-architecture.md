# Architecture summary

This Spring Boot application uses both MVC and REST controllers. Thymeleaf templates are used for 
the Admin and Doctor dashboards, while REST APIs serve all other modules. 
The application interacts with two databases—MySQL (for patient, doctor, appointment, and admin data) 
and MongoDB (for prescriptions). All controllers route requests through a common service layer, which in turn delegates 
to the appropriate repositories. MySQL uses JPA entities while MongoDB uses document models.

1. User accesses AdminDashboard or Appointment pages.
2. The action is routed to the appropriate Thymeleaf or REST controller.
3. The controller calls the service layer, which applies business rules and validations and also coordinates the workflow across multiple entities.
4. The service layer interacts with the repository layer which includes RDBMS and NoSQL databases.
5. The RDBMS chosen is MySQL and NoSQL database is MongoDB. MySQL stores core entities in a normalized relational schema and constraints. And MongoDB stores unstructured data, e.g. prescriptions.
6. Data is persisted and the response is returned to the client as HTML or JSON.