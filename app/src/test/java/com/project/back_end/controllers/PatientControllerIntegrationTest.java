package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.security.Role;
import com.project.back_end.services.Service;
import com.project.back_end.services.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PatientControllerIntegrationTest extends IntegrationTest {

    private static final String PATIENT_API_URL = "/api/patient";

    private static final String PATIENT_EMAIL = "jane.doe@email.com";
    private static final String PATIENT_PASSWORD = "patient@1234";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Service service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

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

    /* ----------------------------------------------- Appointments ------------------------------------------------ */

    @Test
    void shouldGetPatientAppointments() throws Exception {
        Patient savedPatient = patientRepository.save(aPatient());
        Doctor savedDoctor = doctorRepository.save(aDoctor());

        Appointment appointment = createAnAppointmentAt(savedPatient, savedDoctor, LocalDateTime.now());
        appointmentRepository.save(appointment);

        String token = getToken(savedPatient.getEmail(), Role.PATIENT);

        mockMvc.perform(get(PATIENT_API_URL + "/" + savedPatient.getId() + "/patient/" + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointments").isArray())
                .andExpect(jsonPath("$.appointments[0].doctorId").value(savedDoctor.getId()))
                .andExpect(jsonPath("$.appointments[0].patientId").value(savedPatient.getId()));
    }

    @Test
    void shouldNotGetAppointmentsOfAnotherPatient() throws Exception {
        Patient patientA = patientRepository.save(aPatient());
        Patient patientB = patientRepository.save(aDifferentPatient());

        Doctor doctor = doctorRepository.save(aDoctor());

        Appointment appointment = createAnAppointmentAt(patientB, doctor, LocalDateTime.now());

        appointmentRepository.save(appointment);

        String token = getToken(patientA.getEmail(), Role.PATIENT);

        mockMvc.perform(get(PATIENT_API_URL + "/" + patientB.getId() + "/patient/" + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenPatientDoesNotExist() throws Exception {
        Patient patientA = patientRepository.save(aPatient());
        Patient patientB = patientRepository.save(aDifferentPatient());

        Long patientBId = patientB.getId();
        patientRepository.deleteById(patientBId);

        String token = getToken(patientA.getEmail(), Role.PATIENT);

        mockMvc.perform(get(PATIENT_API_URL + "/" + patientBId + "/patient/" + token))
                .andExpect(status().isForbidden());
    }

    /* ---------------------------------------------- Patient's data ----------------------------------------------- */

    @Test
    void shouldGetPatientData() throws Exception {
        Patient patient = patientRepository.save(aPatient());
        String token = getToken(patient.getEmail(), Role.PATIENT);

        mockMvc.perform(get(PATIENT_API_URL + "/" + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.patient.id").value(patient.getId()))
                .andExpect(jsonPath("$.patient.name").value(patient.getName()))
                .andExpect(jsonPath("$.patient.email").value(patient.getEmail()))
                .andExpect(jsonPath("$.patient.phone").value(patient.getPhone()))
                .andExpect(jsonPath("$.patient.address").value(patient.getAddress()))
                .andExpect(jsonPath("$.patient.password").doesNotExist());
    }

    @Test
    void shouldNotGetPatientDataOnWrongRole() throws Exception {
        Doctor doctor = doctorRepository.save(aDoctor());
        String token = getToken(doctor.getEmail(), Role.DOCTOR);

        mockMvc.perform(get(PATIENT_API_URL + "/" + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
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

    private Patient aDifferentPatient() {
        Patient patient = new Patient();
        patient.setName("Other Patient");
        patient.setEmail("other@email.com");
        patient.setPassword(passwordEncoder.encode("patient@1234"));
        patient.setPhone("999999999");
        patient.setAddress("Other address");

        return patient;
    }

    private Doctor aDoctor() {
        Doctor doctor = new Doctor();
        doctor.setName("John Doe");
        doctor.setEmail("doctor@email.com");
        doctor.setPhone("5553334444");
        doctor.setSpecialty("Neurologist");
        doctor.setPassword(passwordEncoder.encode("doctor@1234"));

        return doctor;
    }

    private Appointment createAnAppointmentAt(Patient patient, Doctor doctor, LocalDateTime at) {
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentTime(at);
        appointment.setStatus(0);

        return (appointment);
    }

    private String getToken(String email, String role) {
        UserDetails patientUser = User.builder()
                .username(email)
                .password("test")
                .roles(role.toUpperCase())
                .build();

        return tokenService.generateToken(patientUser);
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
