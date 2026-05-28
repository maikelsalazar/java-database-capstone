package com.project.back_end.controllers;

import com.project.back_end.models.Doctor;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.security.Role;
import com.project.back_end.services.Service;
import com.project.back_end.services.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class DoctorControllerIntegrationTest {
    private static final String LOGIN_URI = "/api/doctors/login";
    private static final String SAVE_DOCTORS_URI = "/api/doctors/";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private Service service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;

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
        UserDetails user = User.builder()
                .username("admin")
                .password("admin@1234")
                .roles(Role.ADMIN)
                .build();

        adminToken = tokenService.generateToken(user);
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
                        .content(credentials))
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
                        .content(credentials))
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
                        .content(credentials))
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

    @Test
    void shouldSaveDoctor() throws Exception {
        String doctorData = """
                {
                    "name": "John Doe",
                    "specialty": "Pediatrician",
                    "email": "john.doe@email.com",
                    "password": "123456",
                    "phone": "5554443333",
                    "availableTimes": [
                        "09:00-10:00",
                        "10:00-11:00",
                        "13:00-14:00"
                    ]
                }
                """;

        mockMvc.perform(post(SAVE_DOCTORS_URI + "/" + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(doctorData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Doctor added successfully"));

        assertTrue(doctorRepository.existsByEmail("john.doe@email.com"));
    }

    @Test
    void shouldNotSaveDoctorOnInvalidToken() throws Exception {
        UserDetails doctorUser = User.builder()
                .username("doctor@email.com")
                .password("doctor@1234")
                .roles(Role.DOCTOR)
                .build();

        String doctorToken = tokenService.generateToken(doctorUser);

        String doctorData = """
                {
                    "name": "John Doe",
                    "specialty": "Pediatrician",
                    "email": "john.doe@email.com",
                    "password": "123456",
                    "phone": "5554443333",
                    "availableTimes": [
                        "09:00-10:00",
                        "10:00-11:00",
                        "13:00-14:00"
                    ]
                }
                """;

        mockMvc.perform(post(SAVE_DOCTORS_URI + "/" + doctorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(doctorData))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void shouldNotSaveDoctorOnBadRequest() throws Exception {

        String doctorData = """
                {
                    "specialty": "Pediatrician",
                    "email": "john.doe@email.com",
                    "password": "123456",
                    "phone": "5554443333",
                    "availableTimes": [
                        "09:00-10:00",
                        "10:00-11:00",
                        "13:00-14:00"
                    ]
                }
                """;

        mockMvc.perform(post(SAVE_DOCTORS_URI + "/xxx.yyy.zzz")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(doctorData))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.*").isNotEmpty());
    }

    @Test
    void shouldDeleteADoctor() throws Exception {
        Doctor doctor = new Doctor();
        doctor.setName("John Doe");
        doctor.setEmail("doctor@email.com");
        doctor.setPhone("5553334444");
        doctor.setSpecialty("Neurologist");
        doctor.setPassword(passwordEncoder.encode("doctor@1234"));

        Doctor savedDoctor = doctorRepository.save(doctor);
        boolean isDoctorSaved = doctorRepository.existsByEmail(savedDoctor.getEmail());


        assertTrue(isDoctorSaved);

        mockMvc.perform(delete(SAVE_DOCTORS_URI + "/" + savedDoctor.getId() + "/" + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertFalse(doctorRepository.existsByEmail(savedDoctor.getEmail()));
    }

    @Test
    void shouldResponseGracefullyWhenDoctorDoesNotExist() throws Exception {

        doctorRepository.deleteById(1L);

        mockMvc.perform(delete(SAVE_DOCTORS_URI + "/1/" + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertFalse(doctorRepository.existsById(1L));
    }

    @Test
    void shouldNotDeleteADoctorOnBadRequest() throws Exception {
        mockMvc.perform(delete(SAVE_DOCTORS_URI + "/one/" + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Bad Request"));
    }

    @Test
    void shouldNotDeleteADoctorOnInvalidToken() throws Exception {
        UserDetails doctorUser = User.builder()
                .username("doctor@email.com")
                .password("doctor@1234")
                .roles(Role.DOCTOR)
                .build();

        String doctorToken = tokenService.generateToken(doctorUser);


        mockMvc.perform(delete(SAVE_DOCTORS_URI + "/1/" + doctorToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
}
