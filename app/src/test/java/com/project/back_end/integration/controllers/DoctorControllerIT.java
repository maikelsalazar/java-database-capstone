package com.project.back_end.integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.back_end.DTO.DoctorProfileUpdateDTO;
import com.project.back_end.integration.shared.IntegrationTest;
import com.project.back_end.enums.AvailableTime;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.security.Role;
import com.project.back_end.services.Service;
import com.project.back_end.services.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DoctorControllerIT extends IntegrationTest {
    private static final String LOGIN_URI = "/api/doctor/login";
    private static final String DOCTOR_API = "/api/doctor/";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private Service service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;

    @BeforeEach
    void setAdminToken() {
        adminToken = getToken("admin", Role.ADMIN);
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
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword(
                passwordEncoder.encode("admin@1234")
        );

        adminRepository.save(admin);

        String doctorData = """
                {
                    "name": "John Doe",
                    "specialty": "Pediatrician",
                    "email": "john.doe@email.com",
                    "password": "12345678",
                    "phone": "5554443333",
                    "availableTimes": [
                        "09:00-10:00",
                        "10:00-11:00",
                        "13:00-14:00"
                    ]
                }
                """;

        mockMvc.perform(post(DOCTOR_API + "/" + adminToken)
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
                    "password": "12345678",
                    "phone": "5554443333",
                    "availableTimes": [
                        "09:00-10:00",
                        "10:00-11:00",
                        "13:00-14:00"
                    ]
                }
                """;

        mockMvc.perform(post(DOCTOR_API + "/" + doctorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(doctorData))
                .andExpect(status().isForbidden());
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

        mockMvc.perform(post(DOCTOR_API + "/xxx.yyy.zzz")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(doctorData))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.*").isNotEmpty());
    }

    @Test
    void shouldUpdateDoctor() throws Exception {
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword(
                passwordEncoder.encode("admin@1234")
        );

        adminRepository.save(admin);

        Doctor doctor = doctorRepository.save(aDoctor());
        DoctorProfileUpdateDTO doctorToUpdate = new DoctorProfileUpdateDTO(
                doctor.getId(),
                "Dr. Updated",
                "Cardiology",
                "5551234567",
                List.of(AvailableTime.SLOT_11_12, AvailableTime.SLOT_13_14)
        );

        mockMvc.perform(put(DOCTOR_API + "/" + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctorToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Doctor updated successfully"));

        Doctor updatedDoctor = doctorRepository.findById(doctor.getId()).orElseThrow();

        assertEquals(doctorToUpdate.name(), updatedDoctor.getName());
        assertEquals(doctorToUpdate.specialty(), updatedDoctor.getSpecialty());
        assertEquals(doctorToUpdate.phone(), updatedDoctor.getPhone());
        assertEquals(doctorToUpdate.availableTimes(), updatedDoctor.getAvailableTimes());
    }

    @Test
    void shouldRejectUpdateDoctorOnDoctorNotFound() throws Exception {
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword(
                passwordEncoder.encode("admin@1234")
        );

        adminRepository.save(admin);

        DoctorProfileUpdateDTO doctorToUpdate = new DoctorProfileUpdateDTO(
                1000L,
                "Dr. Updated",
                "Cardiology",
                "5551234567",
                List.of(AvailableTime.SLOT_11_12, AvailableTime.SLOT_13_14)
        );

        mockMvc.perform(put(DOCTOR_API + "/" + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctorToUpdate)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Doctor Not Found"));
    }

    @Test
    void shouldRejectUpdateDoctorWhenUserIsNotAdmin() throws Exception {
        Doctor doctor = doctorRepository.save(aDoctor());
        DoctorProfileUpdateDTO doctorToUpdate = new DoctorProfileUpdateDTO(
                doctor.getId(),
                "Dr. Updated",
                "Cardiology",
                "5551234567",
                List.of(AvailableTime.SLOT_11_12, AvailableTime.SLOT_13_14)
        );

        String patientToken = getToken("patient@email.com", Role.PATIENT);
        mockMvc.perform(put(DOCTOR_API + "/" + patientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctorToUpdate)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Not allowed"));
    }

    @Test
    void shouldRejectUpdateDoctorOnBadRequest() throws Exception {
        Doctor existingDoctor = doctorRepository.save(aDoctor());

        DoctorProfileUpdateDTO doctorToUpdate = new DoctorProfileUpdateDTO(
                existingDoctor.getId(),
                "",
                "",
                "55",
                List.of(AvailableTime.SLOT_11_12, AvailableTime.SLOT_13_14)
        );

        mockMvc.perform(put(DOCTOR_API + "/" + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctorToUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.specialty").exists())
                .andExpect(jsonPath("$.errors.phone").exists());

        Doctor notUpdatedDoctor = doctorRepository.findById(existingDoctor.getId()).orElseThrow();

        assertEquals(existingDoctor.getName(), notUpdatedDoctor.getName());
        assertEquals(existingDoctor.getSpecialty(), notUpdatedDoctor.getSpecialty());
        assertEquals(existingDoctor.getPhone(), notUpdatedDoctor.getPhone());
        assertIterableEquals(existingDoctor.getAvailableTimes(), notUpdatedDoctor.getAvailableTimes());
    }

    @Test
    void shouldDeleteADoctor() throws Exception {
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword(
                passwordEncoder.encode("admin@1234")
        );

        adminRepository.save(admin);

        Doctor doctor = new Doctor();
        doctor.setName("John Doe");
        doctor.setEmail("doctor@email.com");
        doctor.setPhone("5553334444");
        doctor.setSpecialty("Neurologist");
        doctor.setPassword(passwordEncoder.encode("doctor@1234"));

        Doctor savedDoctor = doctorRepository.save(doctor);
        boolean isDoctorSaved = doctorRepository.existsByEmail(savedDoctor.getEmail());


        assertTrue(isDoctorSaved);

        mockMvc.perform(delete(DOCTOR_API + "/" + savedDoctor.getId() + "/" + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertFalse(doctorRepository.existsByEmail(savedDoctor.getEmail()));
    }

    @Test
    void shouldResponseGracefullyWhenDoctorDoesNotExist() throws Exception {
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword(
                passwordEncoder.encode("admin@1234")
        );

        adminRepository.save(admin);


        doctorRepository.deleteById(1L);

        mockMvc.perform(delete(DOCTOR_API + "/1/" + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertFalse(doctorRepository.existsById(1L));
    }

    @Test
    void shouldNotDeleteADoctorOnBadRequest() throws Exception {
        mockMvc.perform(delete(DOCTOR_API + "/one/" + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void shouldNotDeleteADoctorOnInvalidToken() throws Exception {
        UserDetails doctorUser = User.builder()
                .username("doctor@email.com")
                .password("doctor@1234")
                .roles(Role.DOCTOR)
                .build();

        String doctorToken = tokenService.generateToken(doctorUser);

        mockMvc.perform(delete(DOCTOR_API + "/1/" + doctorToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    /* ---------------------------------------------- Helpers ---------------------------------------------- */

    private Doctor aDoctor() {
        Doctor doctor = new Doctor();
        doctor.setName("John Doe");
        doctor.setEmail("john.doe@email.com");
        doctor.setPhone("5553334444");
        doctor.setPassword(passwordEncoder.encode("john.doe@1234"));
        doctor.setSpecialty("Neurologist");
        doctor.setAvailableTimes(List.of(AvailableTime.SLOT_08_09, AvailableTime.SLOT_10_11));

        return doctor;
    }

    private Doctor anotherDoctor() {
        Doctor doctor = new Doctor();
        doctor.setName("Jane Doe");
        doctor.setEmail("jane.doe@email.com");
        doctor.setPhone("1153334444");
        doctor.setPassword(passwordEncoder.encode("jane.doe@1234"));
        doctor.setSpecialty("Cardiologist");
        doctor.setAvailableTimes(List.of(AvailableTime.SLOT_13_14, AvailableTime.SLOT_14_15));

        return doctor;
    }

    private String getToken(String email, String role) {
        UserDetails user = User.builder()
                .username(email)
                .password("test")
                .roles(role.toUpperCase())
                .build();

        return tokenService.generateToken(user);
    }
}
