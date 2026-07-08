package com.project.back_end.integration.shared;

import com.project.back_end.config.TestContainersConfig;
import com.project.back_end.repo.*;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
@ActiveProfiles("test")
public abstract class IntegrationTest {

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", TestContainersConfig.MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", TestContainersConfig.MYSQL::getUsername);
        registry.add("spring.datasource.password", TestContainersConfig.MYSQL::getPassword);

        registry.add("spring.data.mongodb.uri", TestContainersConfig.MONGO::getReplicaSetUrl);
    }

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

    @BeforeEach
    void cleanDatabase() {
        appointmentRepository.deleteAll();
        prescriptionRepository.deleteAll();
        doctorRepository.deleteAll();
        patientRepository.deleteAll();
        adminRepository.deleteAll();
    }
}
