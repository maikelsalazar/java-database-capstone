package com.project.back_end.controllers;

import com.project.back_end.builders.AppointmentBuilder;
import com.project.back_end.builders.DoctorBuilder;
import com.project.back_end.builders.PatientBuilder;
import com.project.back_end.enums.AvailableTime;
import com.project.back_end.exceptions.AppointmentAlreadyExistsException;
import com.project.back_end.exceptions.IllegalAppointmentUpdateException;
import com.project.back_end.exceptions.UnavailableDoctorException;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
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
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class AppointmentControllerIntegrationTests {

    private static final String APPOINTMENTS_API = "/api/appointments";
    private static final List<AvailableTime> AVAILABLE_TIMES = List.of(
            AvailableTime.SLOT_09_10, AvailableTime.SLOT_10_11
    );

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private TokenService tokenService;

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
        appointmentRepository.deleteAll();
        doctorRepository.deleteAll();
        patientRepository.deleteAll();
    }

    @Test
    void shouldUpdateAppointmentSuccessfully() throws Exception {
        Patient anPatient = PatientBuilder.aPatient().build();
        Doctor anDoctor = DoctorBuilder.aDoctor()
                .withAvailableTimes(AVAILABLE_TIMES)
                .build();

        Patient patient = patientRepository.save(anPatient);
        Doctor doctor = doctorRepository.save(anDoctor);

        LocalDateTime appointmentDate = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime newAppointmentDate = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0);

        Appointment anAppointment = AppointmentBuilder.anAppointment()
                .withDoctor(doctor)
                .withPatient(patient)
                .withAppointmentTime(appointmentDate)
                .build();

        Appointment appointment = appointmentRepository.save(anAppointment);

        String appointmentUpdate = """
                {
                    "id": %d,
                    "doctor": {
                        "id": %d
                    },
                    "patient": {
                        "id": %d
                    },
                    "appointmentTime": "%s",
                    "status": 0
                }
                """.formatted(appointment.getId(), doctor.getId(), patient.getId(), newAppointmentDate.format(DATE_TIME_FORMATTER));

        String token = getToken(patient.getEmail(), Role.PATIENT);

        mockMvc.perform(put(APPOINTMENTS_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appointmentUpdate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        Optional<Appointment> appointmentUpdated = appointmentRepository.findById(appointment.getId());

        assertTrue(appointmentUpdated.isPresent());
        assertEquals(newAppointmentDate, appointmentUpdated.get().getAppointmentTime());
    }

    @Test
    void shouldRejectUserWithInvalidRole() throws Exception {
        Patient patient = patientRepository.save(PatientBuilder.aPatient().build());
        Doctor doctor = doctorRepository.save(DoctorBuilder.aDoctor()
                .withAvailableTimes(AVAILABLE_TIMES)
                .build());

        LocalDateTime appointmentTime = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime newAppointmentTime = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0);
        Appointment appointment = appointmentRepository.save(
                AppointmentBuilder.anAppointment()
                        .withDoctor(doctor)
                        .withPatient(patient)
                        .withAppointmentTime(appointmentTime)
                        .build());

        String appointmentUpdate = """
                {
                    "id": %d,
                    "doctor": {
                        "id": %d
                    },
                    "patient": {
                        "id": %d
                    },
                    "appointmentTime": "%s",
                    "status": 0
                }
                """.formatted(appointment.getId(), doctor.getId(), patient.getId(), newAppointmentTime.format(DATE_TIME_FORMATTER));

        String token = getToken(doctor.getEmail(), Role.DOCTOR);

        mockMvc.perform(put(APPOINTMENTS_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appointmentUpdate))
                .andExpect(status().isForbidden());

        checkNoChangesMade(appointment);
    }

    @Test
    void shouldRejectNonOwnerPatient() throws Exception {
        Patient patientA = patientRepository.save(PatientBuilder.aPatient().build());
        Patient patientB = patientRepository.save(PatientBuilder.aPatient()
                .withEmail("lisa.doe@email.com")
                .build());
        Doctor doctor = doctorRepository.save(DoctorBuilder.aDoctor().build());
        LocalDateTime appointmentTime = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime newAppointmentTime = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0);
        Appointment appointment = appointmentRepository.save(
                AppointmentBuilder.anAppointment()
                        .withDoctor(doctor)
                        .withAppointmentTime(appointmentTime)
                        .withPatient(patientA)
                        .build());

        String appointmentUpdate = """
                {
                    "id": %d,
                    "doctor": {
                        "id": %d
                    },
                    "patient": {
                        "id": %d
                    },
                    "appointmentTime": "%s",
                    "status": 0
                }
                """.formatted(appointment.getId(), doctor.getId(), patientA.getId(), newAppointmentTime.format(DATE_TIME_FORMATTER));

        String token = getToken(patientB.getEmail(), Role.PATIENT);

        mockMvc.perform(put(APPOINTMENTS_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appointmentUpdate))
                .andExpect(status().isForbidden());

        checkNoChangesMade(appointment);
    }

    @Test
    void shouldRejectWhenAppointmentTimeIsInThePast() throws Exception {
        Patient patient = patientRepository.save(PatientBuilder.aPatient().build());
        Doctor doctor = doctorRepository.save(DoctorBuilder.aDoctor()
                .withAvailableTimes(AVAILABLE_TIMES)
                .build());
        LocalDateTime appointmentDate = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime newAppointmentDate = LocalDateTime.now().minusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0);
        Appointment appointment = appointmentRepository.save(
                AppointmentBuilder.anAppointment()
                        .withPatient(patient)
                        .withDoctor(doctor)
                        .withAppointmentTime(appointmentDate)
                        .build());

        String appointmentUpdate = """
                {
                    "id": %d,
                    "doctor": {
                        "id": %d
                    },
                    "patient": {
                        "id": %d
                    },
                    "appointmentTime": "%s",
                    "status": 0
                }
                """.formatted(appointment.getId(), doctor.getId(), patient.getId(), newAppointmentDate.format(DATE_TIME_FORMATTER));

        String token = getToken(patient.getEmail(), Role.PATIENT);

        mockMvc.perform(put(APPOINTMENTS_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appointmentUpdate))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(IllegalAppointmentUpdateException.APPOINTMENT_TIME_ALREADY_PAST));

        checkNoChangesMade(appointment);
    }

    @Test
    void shouldRejectWhenAppointmentHasIllegalStatus() throws Exception {
        Patient patient = patientRepository.save(PatientBuilder.aPatient().build());
        Doctor doctor = doctorRepository.save(DoctorBuilder.aDoctor().build());
        LocalDateTime appointmentDate = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime newAppointmentDate = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0);
        Appointment appointment = appointmentRepository.save(
                AppointmentBuilder.anAppointment()
                        .withDoctor(doctor)
                        .withPatient(patient)
                        .withAppointmentTime(appointmentDate)
                        .withStatus(Appointment.STATUS_CANCELED)
                        .build());

        String appointmentUpdate = """
                {
                    "id": %d,
                    "doctor": {
                        "id": %d
                    },
                    "patient": {
                        "id": %d
                    },
                    "appointmentTime": "%s",
                    "status": 0
                }
                """.formatted(appointment.getId(), doctor.getId(), patient.getId(), newAppointmentDate.format(DATE_TIME_FORMATTER));

        String token = getToken(patient.getEmail(), Role.PATIENT);

        mockMvc.perform(put(APPOINTMENTS_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appointmentUpdate))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(IllegalAppointmentUpdateException.ILLEGAL_APPOINTMENT_STATUS));

        checkNoChangesMade(appointment);
    }

    @Test
    void shouldNotUpdateAppointmentWhenDoctorIsAlreadyBooked() throws Exception {
        Patient patientA = patientRepository.save(PatientBuilder.aPatient().build());
        Patient patientB = patientRepository.save(PatientBuilder.aPatient()
                .withEmail("lisa.doe@email.com")
                .build());

        Doctor doctor = doctorRepository.save(DoctorBuilder.aDoctor()
                .withAvailableTimes(AVAILABLE_TIMES)
                .build());

        LocalDateTime appointmentDateOfPatientA = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime appointmentDateOfPatientB = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime newAppointmentDateOfPatientA = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0);

        Appointment appointmentOfPatientA = appointmentRepository.save(
                AppointmentBuilder.anAppointment()
                        .withPatient(patientA)
                        .withAppointmentTime(appointmentDateOfPatientA)
                        .withDoctor(doctor)
                        .build());

        Appointment appointmentOfPatientB = appointmentRepository.save(
                AppointmentBuilder.anAppointment()
                        .withPatient(patientB)
                        .withAppointmentTime(appointmentDateOfPatientB)
                        .withDoctor(doctor)
                        .build());

        String appointmentUpdate = """
                {
                    "id": %d,
                    "doctor": {
                        "id": %d
                    },
                    "patient": {
                        "id": %d
                    },
                    "appointmentTime": "%s",
                    "status": 0
                }
                """.formatted(
                appointmentOfPatientA.getId(),
                doctor.getId(),
                patientA.getId(),
                newAppointmentDateOfPatientA.format(DATE_TIME_FORMATTER)
        );

        String token = getToken(patientA.getEmail(), Role.PATIENT);

        mockMvc.perform(put(APPOINTMENTS_API + "/" + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(appointmentUpdate))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(AppointmentAlreadyExistsException.DOCTOR_ALREADY_BOOKED));

        checkNoChangesMade(appointmentOfPatientA);
        checkNoChangesMade(appointmentOfPatientB);
    }

    @Test
    void shouldNotUpdateAppointmentWhenPatientHasAppointmentAtTheSameTime() throws Exception {
        Patient patient = patientRepository.save(PatientBuilder.aPatient().build());
        Doctor doctorA = doctorRepository.save(DoctorBuilder.aDoctor()
                .withAvailableTimes(AVAILABLE_TIMES)
                .withEmail("john.doe@email.com")
                .build());
        Doctor doctorB = doctorRepository.save(DoctorBuilder.aDoctor()
                .withAvailableTimes(AVAILABLE_TIMES)
                .withEmail("jane.doe@email.com")
                .build());

        LocalDateTime appointmentDateA = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime appointmentDateB = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime newAppointmentDateForA = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0);

        Appointment appointmentA = appointmentRepository.save(AppointmentBuilder.anAppointment()
                .withPatient(patient)
                .withDoctor(doctorA)
                .withAppointmentTime(appointmentDateA)
                .build());

        Appointment appointmentB = appointmentRepository.save(AppointmentBuilder.anAppointment()
                .withPatient(patient)
                .withDoctor(doctorB)
                .withAppointmentTime(appointmentDateB)
                .build());

        String appointmentUpdate = """
                {
                    "id": %d,
                    "doctor": {
                        "id": %d
                    },
                    "patient": {
                        "id": %d
                    },
                    "appointmentTime": "%s",
                    "status": 0
                }
                """.formatted(
                appointmentA.getId(),
                doctorA.getId(),
                patient.getId(),
                newAppointmentDateForA.format(DATE_TIME_FORMATTER)
        );

        String token = getToken(patient.getEmail(), Role.PATIENT);

        mockMvc.perform(put(APPOINTMENTS_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appointmentUpdate))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(AppointmentAlreadyExistsException.PATIENT_HAS_ANOTHER_APPOINTMENT));

        checkNoChangesMade(appointmentA);
        checkNoChangesMade(appointmentB);
    }

    @Test
    void shouldReturnNotFoundWhenAppointmentDoesNotExist() throws Exception {
        Patient patient = patientRepository.save(PatientBuilder.aPatient().build());
        Doctor doctor = doctorRepository.save(DoctorBuilder.aDoctor().build());
        LocalDateTime appointmentDate = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);

        LocalDateTime newAppointmentDate = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0);
        Appointment appointment = appointmentRepository.save(AppointmentBuilder.anAppointment()
                .withDoctor(doctor)
                .withPatient(patient)
                .withAppointmentTime(appointmentDate)
                .build()
        );

        long deletedAppointmentId = appointment.getId();
        appointmentRepository.delete(appointment);
        ;

        String appointmentUpdate = """
                {
                    "id": %d,
                    "doctor": {
                        "id": %d
                    },
                    "patient": {
                        "id": %d
                    },
                    "appointmentTime": "%s",
                    "status": 0
                }
                """.formatted(
                deletedAppointmentId,
                doctor.getId(),
                patient.getId(),
                newAppointmentDate.format(DATE_TIME_FORMATTER));

        String token = getToken(patient.getEmail(), Role.PATIENT);

        mockMvc.perform(put(APPOINTMENTS_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appointmentUpdate))
                .andExpect(status().isNotFound());

        Optional<Appointment> appointmentUpdated = appointmentRepository.findById(deletedAppointmentId);

        assertTrue(appointmentUpdated.isEmpty());
    }

    @Test
    void shouldNotUpdateAppointmentWhenDoctorIsUnavailable() throws Exception {
        Patient patient = patientRepository.save(PatientBuilder.aPatient().build());
        Doctor doctor = doctorRepository.save(DoctorBuilder.aDoctor().build());
        LocalDateTime appointmentDate = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime newAppointmentDate = LocalDateTime.now().plusDays(1).withHour(15).withMinute(0).withSecond(0).withNano(0);
        Appointment appointment = appointmentRepository.save(AppointmentBuilder.anAppointment()
                .withDoctor(doctor)
                .withPatient(patient)
                .withAppointmentTime(appointmentDate)
                .build());

        String appointmentUpdate = """
                {
                    "id": %d,
                    "doctor": {
                        "id": %d
                    },
                    "patient": {
                        "id": %d
                    },
                    "appointmentTime": "%s",
                    "status": 0
                }
                """.formatted(appointment.getId(), doctor.getId(), patient.getId(), newAppointmentDate.format(DATE_TIME_FORMATTER));

        String token = getToken(patient.getEmail(), Role.PATIENT);

        mockMvc.perform(put(APPOINTMENTS_API + "/" + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(appointmentUpdate))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(UnavailableDoctorException.UNAVAILABLE_DOCTOR));


        checkNoChangesMade(appointment);
    }

    private void checkNoChangesMade(Appointment appointment) {
        Optional<Appointment> appointmentNotUpdatedFromDb = appointmentRepository.findById(appointment.getId());

        assertTrue(appointmentNotUpdatedFromDb.isPresent());

        Appointment appointmentNotUpdated = appointmentNotUpdatedFromDb.get();

        assertEquals(appointment.getId(), appointmentNotUpdated.getId());
        assertEquals(appointment.getAppointmentTime(), appointmentNotUpdated.getAppointmentTime());
        assertEquals(appointment.getDoctor().getId(), appointmentNotUpdated.getDoctor().getId());
        assertEquals(appointment.getPatient().getId(), appointmentNotUpdated.getPatient().getId());
        assertEquals(appointment.getStatus(), appointmentNotUpdated.getStatus());
    }

    private String getToken(String email, String role) {
        UserDetails patientUser = User.builder()
                .username(email)
                .password("test")
                .roles(role.toUpperCase())
                .build();

        return tokenService.generateToken(patientUser);
    }
}
