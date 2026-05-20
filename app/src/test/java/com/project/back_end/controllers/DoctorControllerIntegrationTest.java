package com.project.back_end.controllers;

import com.project.back_end.models.Doctor;
import com.project.back_end.repo.DoctorRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class DoctorControllerIntegrationTest {
    private static final String LOGIN_URI = "/api/doctors/login";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DoctorRepository doctorRepository;

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
        doctorRepository.deleteAllInBatch();
    }

    @Test
    void shouldReturnJwtTokenOnLogin() throws Exception {
        Doctor doctor = new Doctor();
        doctor.setName("John Doe");
        doctor.setEmail("doctor@email.com");
        doctor.setPhone("5553334444");
        doctor.setSpecialty("Neurologist");
        doctor.setPassword(passwordEncoder.encode("doctor@1234"));

        doctorRepository.save(doctor);

        String credentials = """
                {
                     "email": "doctor@email.com",
                     "password": "doctor@1234"
                }
                """;

        mockMvc.perform(post(LOGIN_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentials)
            )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").isBoolean())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.message")
                        .value("Login successful"));
    }

    @Test
    void shouldReturnUnauthorizedOnInvalidCredentials() throws Exception {
        Doctor doctor = new Doctor();
        doctor.setName("John Doe");
        doctor.setEmail("doctor@email.com");
        doctor.setPhone("5553334444");
        doctor.setSpecialty("Neurologist");
        doctor.setPassword(passwordEncoder.encode("doctor@1234"));

        doctorRepository.save(doctor);

        String credentials = """
                {
                     "email": "doctor@email.com",
                     "password": "doctor@1235"
                }
                """;

        mockMvc.perform(post(LOGIN_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentials)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").isBoolean())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.message")
                        .value("Invalid credentials"));
    }

    @Test
    void shouldReturnUnauthorizedWhenUsernameDoesNotExist() throws Exception {
        String credentials = """
                {
                     "email": "non-existing-user@email.com",
                     "password": "doctor@1234"
                }
                """;

        mockMvc.perform(post(LOGIN_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentials)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").isBoolean())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.message")
                        .value("Invalid credentials"));
    }

    @Test
    void shouldReturnBadRequestOnInvalidBody() throws Exception {
        String credentials = """
                {
                     "email": "",
                     "password": ""
                }
                """;

        mockMvc.perform(post(LOGIN_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentials)
                )
                .andExpect(status().isBadRequest());
    }
}
