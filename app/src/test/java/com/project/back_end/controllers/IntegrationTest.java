package com.project.back_end.controllers;

import com.project.back_end.repo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public abstract class IntegrationTest {

    @Autowired
    protected AdminRepository adminRepository;

    @Autowired
    protected AppointmentRepository appointmentRepository;

    @Autowired
    protected DoctorRepository doctorRepository;

    @Autowired
    protected PatientRepository patientRepository;

    @Autowired
    protected PrescriptionRepository prescriptionRepository;

    static MySQLContainer<?> mysql =
            new MySQLContainer<>("mysql:8.0")
                    .withDatabaseName("cms")
                    .withUsername("root")
                    .withPassword("password");

    static MongoDBContainer mongo =
            new MongoDBContainer("mongo:7.0");

    static {
        mysql.start();
        mongo.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);

        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @BeforeEach
    void cleanDatabase() {
        prescriptionRepository.deleteAll();
        appointmentRepository.deleteAll();
        doctorRepository.deleteAll();
        patientRepository.deleteAll();
        adminRepository.deleteAll();
    }
}
