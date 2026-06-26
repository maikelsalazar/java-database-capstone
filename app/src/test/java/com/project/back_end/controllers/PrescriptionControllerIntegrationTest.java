package com.project.back_end.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.back_end.DTO.PrescriptionCreateDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.models.Prescription;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.repo.PrescriptionRepository;
import com.project.back_end.security.Role;
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
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class PrescriptionControllerIntegrationTest {
    private static final String PRESCRIPTION_API = "/api/prescription";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.0.6");

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        prescriptionRepository.deleteAll();
        appointmentRepository.deleteAll();
        doctorRepository.deleteAll();
        patientRepository.deleteAll();
    }

    @Test
    void shouldCreatePrescription() throws Exception {
        Doctor doctor = doctorRepository.save(aDoctor());
        Patient patient = patientRepository.save(aPatient());

        Appointment appointment = appointmentRepository.save(anAppointment(patient, doctor, LocalDateTime.now()));

        PrescriptionCreateDTO prescriptionCreateDTO =
                new PrescriptionCreateDTO(
                        appointment.getId(),
                        patient.getName(),
                        "Paracetamol",
                        "every 8 hours",
                        "Only if needed");

        String prescriptionJson = objectMapper.writeValueAsString(prescriptionCreateDTO);
        String token = getToken(doctor.getEmail(), Role.DOCTOR);
        mockMvc.perform(post(PRESCRIPTION_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(prescriptionJson))
                .andExpect(status().isCreated());

        assertEquals(1, prescriptionRepository.findAll().size());

        Prescription actual = prescriptionRepository.findAll().get(0);

        assertEquals(appointment.getId(), actual.getAppointmentId());
        assertEquals(patient.getName(), actual.getPatientName());
        assertEquals(prescriptionCreateDTO.medication(), actual.getMedication());
        assertEquals(prescriptionCreateDTO.dosage(), actual.getDosage());
        assertEquals(prescriptionCreateDTO.doctorNotes(), actual.getDoctorNotes());

        Appointment updatedAppointment = appointmentRepository.findById(appointment.getId()).orElseThrow();
        assertEquals(Appointment.STATUS_COMPLETED, updatedAppointment.getStatus());
    }

    @Test
    void shouldRejectCreatePrescriptionWhenAppointmentAlreadyHasAPrescription() throws Exception {
        Doctor doctor = doctorRepository.save(aDoctor());
        Patient patient = patientRepository.save(aPatient());
        Appointment appointment = appointmentRepository.save(anAppointment(patient, doctor, LocalDateTime.now()));
        Prescription prescription = aPrescription(appointment, patient);
        prescriptionRepository.save(prescription);

        String token = getToken(doctor.getEmail(), Role.DOCTOR);
        PrescriptionCreateDTO anotherPrescription = new PrescriptionCreateDTO(
                appointment.getId(),
                patient.getName(),
                "Paracetamol",
                "every 8 hours",
                "Only if needed");

        String anotherPrescriptionJson = objectMapper.writeValueAsString(anotherPrescription);
        mockMvc.perform(post(PRESCRIPTION_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(anotherPrescriptionJson))
                .andExpect(status().isConflict());

        assertEquals(1, prescriptionRepository.findByAppointmentId(appointment.getId()).size());
    }

    @Test
    void shouldRejectCreatePrescriptionWhenUserIsNotADoctor() throws Exception {
        Doctor doctor = doctorRepository.save(aDoctor());
        Patient patient = patientRepository.save(aPatient());

        Appointment appointment = appointmentRepository.save(anAppointment(patient, doctor, LocalDateTime.now()));

        PrescriptionCreateDTO prescriptionCreateDTO =
                new PrescriptionCreateDTO(
                        appointment.getId(),
                        patient.getName(),
                        "Paracetamol",
                        "every 8 hours",
                        "Only if needed");

        String prescriptionJson = objectMapper.writeValueAsString(prescriptionCreateDTO);
        String token = getToken(patient.getEmail(), Role.PATIENT);

        mockMvc.perform(post(PRESCRIPTION_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(prescriptionJson))
                .andExpect(status().isForbidden());

        assertEquals(0, prescriptionRepository.findAll().size());
    }

    @Test
    void shouldRejectCreatePrescriptionWhenDoctorDoesNotOwnAppointment() throws Exception {
        Doctor doctor = doctorRepository.save(aDoctor());
        Doctor anotherDoctor = doctorRepository.save(anotherDoctor());
        Patient patient = patientRepository.save(aPatient());

        Appointment appointment = appointmentRepository.save(anAppointment(patient, doctor, LocalDateTime.now()));

        PrescriptionCreateDTO prescriptionCreateDTO =
                new PrescriptionCreateDTO(
                        appointment.getId(),
                        patient.getName(),
                        "Paracetamol",
                        "every 8 hours",
                        "Only if needed");

        String prescriptionJson = objectMapper.writeValueAsString(prescriptionCreateDTO);
        String token = getToken(anotherDoctor.getEmail(), Role.DOCTOR);
        mockMvc.perform(post(PRESCRIPTION_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(prescriptionJson))
                .andExpect(status().isForbidden());

        assertEquals(0, prescriptionRepository.findAll().size());
    }

    @Test
    void shouldNotCreatePrescriptionWhenAppointmentDoesNotExist() throws Exception {
        Doctor doctor = doctorRepository.save(aDoctor());
        Patient patient = patientRepository.save(aPatient());

        PrescriptionCreateDTO prescriptionCreateDTO =
                new PrescriptionCreateDTO(
                        1000L,
                        patient.getName(),
                        "Paracetamol",
                        "every 8 hours",
                        "Only if needed");

        String prescriptionJson = objectMapper.writeValueAsString(prescriptionCreateDTO);
        String token = getToken(doctor.getEmail(), Role.DOCTOR);

        mockMvc.perform(post(PRESCRIPTION_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(prescriptionJson))
                .andExpect(status().isNotFound());

        assertEquals(0, prescriptionRepository.findAll().size());
    }

    @Test
    void shouldRejectCreatePrescriptionOnBadRequest() throws Exception {
        Doctor doctor = doctorRepository.save(aDoctor());
        Patient patient = patientRepository.save(aPatient());

        PrescriptionCreateDTO prescriptionCreateDTO =
                new PrescriptionCreateDTO(
                        null,
                        "",
                        "",
                        "",
                        "");

        String prescriptionJson = objectMapper.writeValueAsString(prescriptionCreateDTO);
        String token = getToken(doctor.getEmail(), Role.DOCTOR);

        mockMvc.perform(post(PRESCRIPTION_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(prescriptionJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.appointmentId").exists())
                .andExpect(jsonPath("$.errors.patientName").exists())
                .andExpect(jsonPath("$.errors.medication").exists())
                .andExpect(jsonPath("$.errors.dosage").exists())
                .andExpect(jsonPath("$.errors.doctorNotes").doesNotExist());

        assertEquals(0, prescriptionRepository.findAll().size());
    }

    @Test
    void shouldRejectCreatePrescriptionOnInvalidToken() throws Exception {
        Doctor doctor = doctorRepository.save(aDoctor());
        Patient patient = patientRepository.save(aPatient());

        Appointment appointment = appointmentRepository.save(anAppointment(patient, doctor, LocalDateTime.now()));

        PrescriptionCreateDTO prescriptionCreateDTO =
                new PrescriptionCreateDTO(
                        appointment.getId(),
                        patient.getName(),
                        "Paracetamol",
                        "every 8 hours",
                        "Only if needed");

        String prescriptionJson = objectMapper.writeValueAsString(prescriptionCreateDTO);
        String invalidToken = "xxx.yyy.zzz";
        mockMvc.perform(post(PRESCRIPTION_API + "/" + invalidToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(prescriptionJson))
                .andExpect(status().isUnauthorized());
    }

    /* -------------------------------------------- Get Prescription  -------------------------------------------- */

    @Test
    void shouldGetPrescriptionOfAppointmentById() throws Exception {
        Doctor doctor = doctorRepository.save(aDoctor());
        Patient patient = patientRepository.save(aPatient());

        Appointment appointment = appointmentRepository.save(anAppointment(patient, doctor, LocalDateTime.now()));
        Prescription prescription = prescriptionRepository.save(aPrescription(appointment, patient));
        String token = getToken(doctor.getEmail(), Role.DOCTOR);

        mockMvc.perform(get(PRESCRIPTION_API + "/" + appointment.getId() + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prescription").isArray())
                .andExpect(jsonPath("$.prescription.length()").value(1))
                .andExpect(jsonPath("$.prescription[0].id").doesNotExist())
                .andExpect(jsonPath("$.prescription[0].appointmentId").value(appointment.getId()))
                .andExpect(jsonPath("$.prescription[0].patientName").value(patient.getName()))
                .andExpect(jsonPath("$.prescription[0].medication").value(prescription.getMedication()))
                .andExpect(jsonPath("$.prescription[0].dosage").value(prescription.getDosage()))
                .andExpect(jsonPath("$.prescription[0].doctorNotes").value(prescription.getDoctorNotes()));
    }

    @Test
    void shouldReturnEmptyListWhenAppointmentHasNoPrescription() throws Exception {
        Doctor doctor = doctorRepository.save(aDoctor());
        Patient patient = patientRepository.save(aPatient());

        Appointment appointment = appointmentRepository.save(anAppointment(patient, doctor, LocalDateTime.now()));
        String token = getToken(doctor.getEmail(), Role.DOCTOR);

        mockMvc.perform(get(PRESCRIPTION_API + "/" + appointment.getId() + "/" + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prescription").isArray())
                .andExpect(jsonPath("$.prescription.length()").value(0));
    }

    @Test
    void shouldRejectGetPrescriptionWhenUserIsNotADoctor() throws Exception {
        Doctor doctor = doctorRepository.save(aDoctor());
        Patient patient = patientRepository.save(aPatient());

        Appointment appointment = appointmentRepository.save(anAppointment(patient, doctor, LocalDateTime.now()));
        String token = getToken(patient.getEmail(), Role.PATIENT);

        mockMvc.perform(get(PRESCRIPTION_API + "/" + appointment.getId() + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectGetPrescriptionOnInvalidToken() throws Exception {
        Doctor doctor = doctorRepository.save(aDoctor());
        Patient patient = patientRepository.save(aPatient());

        Appointment appointment = appointmentRepository.save(anAppointment(patient, doctor, LocalDateTime.now()));
        String invalidToken = "xxx.yyy.zzz";

        mockMvc.perform(get(PRESCRIPTION_API + "/" + appointment.getId() + "/" + invalidToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectGetPrescriptionWhenDoctorDoesNotOwnAppointment() throws Exception {
        Doctor doctor = doctorRepository.save(aDoctor());
        Doctor anotherDoctor = doctorRepository.save(anotherDoctor());
        Patient patient = patientRepository.save(aPatient());

        Appointment appointment = appointmentRepository.save(anAppointment(patient, doctor, LocalDateTime.now()));
        String token = getToken(anotherDoctor.getEmail(), Role.DOCTOR);

        mockMvc.perform(get(PRESCRIPTION_API + "/" + appointment.getId() + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    /* -------------------------------------------- Helpers  -------------------------------------------- */

    private String getToken(String email, String role) {
        UserDetails user = User.builder()
                .username(email)
                .password("test")
                .roles(role.toUpperCase())
                .build();

        return tokenService.generateToken(user);
    }

    private Patient aPatient() {
        Patient patient = new Patient();
        patient.setName("Jane Doe");
        patient.setEmail("jane.doe@email.com");
        patient.setPassword(passwordEncoder.encode("patient@1234"));
        patient.setPhone("1234567890");
        patient.setAddress("Av. 145, NY");

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

    private Doctor anotherDoctor() {
        Doctor doctor = new Doctor();
        doctor.setName("John Smith");
        doctor.setEmail("john.smith@email.com");
        doctor.setPhone("5553334444");
        doctor.setSpecialty("Neurologist");
        doctor.setPassword(passwordEncoder.encode("doctor@1234"));

        return doctor;
    }

    private Prescription aPrescription(Appointment appointment, Patient patient) {
        Prescription prescription = new Prescription();
        prescription.setAppointmentId(appointment.getId());
        prescription.setPatientName(patient.getName());
        prescription.setMedication("Paracetamol");
        prescription.setDosage("every 8 hours");
        prescription.setDoctorNotes("Only if needed");

        return prescription;
    }

    private Appointment anAppointment(Patient patient, Doctor doctor, LocalDateTime at) {
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentTime(at);
        appointment.setStatus(Appointment.STATUS_SCHEDULED);

        return appointment;
    }
}
