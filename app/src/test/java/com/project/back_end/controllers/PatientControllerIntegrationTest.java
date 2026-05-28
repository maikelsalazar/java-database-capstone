package com.project.back_end.controllers;

import com.project.back_end.models.Patient;
import com.project.back_end.repo.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class PatientControllerIntegrationTest {

    private static final String PATIENT_API_URL = "/api/patient";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("cms")
            .withUsername("root")
            .withPassword("password");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @BeforeEach
    void setUp() {
        patientRepository.deleteAll();
    }

    @Test
    void shouldCreatePatient() throws Exception {
        mockMvc.perform(post(PATIENT_API_URL + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPatientJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").isNotEmpty());

        assertTrue(patientRepository.existsByEmail("jane.doe@email.com"));
    }

    @Test
    void shouldNotCreateAPatientWhenEmailAlreadyExists() throws Exception {
        Patient patient = new Patient();
        patient.setName("Jane Doe");
        patient.setEmail("jane.doe@email.com");
        patient.setPassword(passwordEncoder.encode("patient@1234"));
        patient.setPhone("1234567890");
        patient.setAddress("Av. 145, NY");

        patientRepository.save(patient);

        mockMvc.perform(post(PATIENT_API_URL + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPatientJson()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isNotEmpty());

        assertEquals(1, patientRepository.count());
    }

    @Test
    void shouldNotCreatePatientWhenValidationFails() throws Exception {
        String patientData = """
                {
                    "name": "J",
                    "email": "jane.doe@email.com",
                    "password": "pa",
                    "phone": "12345",
                    "address": "Av"
                }
                """;

        mockMvc.perform(post(PATIENT_API_URL + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientData))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.*").isNotEmpty());

        assertEquals(0, patientRepository.count());
    }

    private static String validPatientJson() {
        return """
            {
                "name": "Jane Doe",
                "email": "jane.doe@email.com",
                "password": "patient@1234",
                "phone": "1234567890",
                "address": "Av. 14 Park, NY"
            }
        """;
    }
}
