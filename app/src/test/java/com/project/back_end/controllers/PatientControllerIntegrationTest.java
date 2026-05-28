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

    private static final String PATIENT_EMAIL = "jane.doe@email.com";
    private static final String PATIENT_PASSWORD = "patient@1234";

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

    /* ---------------------------------------------- Create A Patient ---------------------------------------------- */

    @Test
    void shouldCreatePatient() throws Exception {
        mockMvc.perform(post(PATIENT_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPatientJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").isNotEmpty());

        assertTrue(patientRepository.existsByEmail(PATIENT_EMAIL));
    }

    @Test
    void shouldNotCreateAPatientWhenEmailAlreadyExists() throws Exception {
        Patient patient = new Patient();
        patient.setName("Jane Doe");
        patient.setEmail(PATIENT_EMAIL);
        patient.setPassword(passwordEncoder.encode(PATIENT_PASSWORD));
        patient.setPhone("1234567890");
        patient.setAddress("Av. 145, NY");

        patientRepository.save(patient);

        mockMvc.perform(post(PATIENT_API_URL)
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

        mockMvc.perform(post(PATIENT_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientData))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.*").isNotEmpty());

        assertEquals(0, patientRepository.count());
    }

    /* --------------------------------------------------- Login --------------------------------------------------- */

    @Test
    void shouldReturnJwtToken() throws Exception {
        patientRepository.save(aPatient());

        mockMvc.perform(post(PATIENT_API_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoginJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldFailLoginOnInvalidCredentials() throws Exception {
        patientRepository.save(aPatient());

        mockMvc.perform(post(PATIENT_API_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidCredentialsLoginJson()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldFailLoginWhenPatientDoesNotExist() throws Exception {
        mockMvc.perform(post(PATIENT_API_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoginJson()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldNotReturnJwtTokenWhenValidationFails() throws Exception {
        patientRepository.save(aPatient());

        String badLogin = """
                {
                    "email": "a1",
                    "password": "patient@1234"
                }
                """;

        mockMvc.perform(post(PATIENT_API_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badLogin))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors.*").isNotEmpty());
    }

    /* -------------------------------------------------- Helpers -------------------------------------------------- */

    private Patient aPatient() {
        Patient patient = new Patient();
        patient.setName("Jane Doe");
        patient.setEmail(PATIENT_EMAIL);
        patient.setPassword(passwordEncoder.encode(PATIENT_PASSWORD));
        patient.setPhone("1234567890");
        patient.setAddress("Av. 145, NY");

        return patient;
    }

    private static String validPatientJson() {
        return """
                    {
                        "name": "Jane Doe",
                        "email": "%s",
                        "password": "%s",
                        "phone": "1234567890",
                        "address": "Av. 14 Park, NY"
                    }
                """.formatted(PATIENT_EMAIL, PATIENT_PASSWORD);
    }

    private static String validLoginJson() {
        return """
                    {
                        "email": "%s",
                        "password": "%s"
                    }
                """.formatted(PATIENT_EMAIL, PATIENT_PASSWORD);
    }

    private static String invalidCredentialsLoginJson() {
        return """
                    {
                        "email": "%s",
                        "password": "%s"
                    }
                """.formatted(PATIENT_EMAIL, PATIENT_PASSWORD + "12");
    }
}
