package com.project.back_end.integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.back_end.DTO.AppointmentCreateDTO;
import com.project.back_end.DTO.AppointmentUpdateDTO;
import com.project.back_end.DTO.DoctorIdDTO;
import com.project.back_end.DTO.PatientIdDTO;
import com.project.back_end.builders.AppointmentBuilder;
import com.project.back_end.builders.DoctorBuilder;
import com.project.back_end.builders.PatientBuilder;
import com.project.back_end.integration.shared.IntegrationTest;
import com.project.back_end.enums.AvailableTime;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.security.Role;
import com.project.back_end.services.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AppointmentControllerIT extends IntegrationTest {

    private static final String APPOINTMENTS_API = "/api/appointments";
    private static final List<AvailableTime> AVAILABLE_TIMES = List.of(
            AvailableTime.SLOT_09_10, AvailableTime.SLOT_10_11
    );

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Clock clock;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldBookAppointmentSuccessfully() throws Exception {
        Doctor doctor = doctorRepository.save(DoctorBuilder.aDoctor().build());
        Patient patient = patientRepository.save(PatientBuilder.aPatient().build());

        LocalDateTime appointmentTime = at(1);

        AppointmentCreateDTO dto = new AppointmentCreateDTO(
                new DoctorIdDTO(doctor.getId()),
                new PatientIdDTO(patient.getId()),
                appointmentTime,
                Appointment.STATUS_SCHEDULED
        );

        String createAppointmentJson = objectMapper.writeValueAsString(dto);

        String token = getToken(patient.getEmail(), Role.PATIENT);

        mockMvc.perform(post(APPOINTMENTS_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAppointmentJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.message").isNotEmpty());


        assertEquals(1, appointmentRepository.count());

        Appointment savedAppointment = appointmentRepository.findAll().get(0);

        assertEquals(doctor.getId(), savedAppointment.getDoctor().getId());
        assertEquals(patient.getId(), savedAppointment.getPatient().getId());
        assertEquals(appointmentTime, savedAppointment.getAppointmentTime());
        assertEquals(
                Appointment.STATUS_SCHEDULED,
                savedAppointment.getStatus()
        );
    }

    @Test
    void shouldRejectBookAppointmentWhenUserIsNotAPatient() throws Exception {
        Doctor anDoctor = DoctorBuilder.aDoctor().build();
        Patient anPatient = PatientBuilder.aPatient().build();

        Doctor doctor = doctorRepository.save(anDoctor);
        Patient patient = patientRepository.save(anPatient);

        LocalDateTime appointmentTime = at(1);

        AppointmentCreateDTO dto = new AppointmentCreateDTO(
                new DoctorIdDTO(doctor.getId()),
                new PatientIdDTO(patient.getId()),
                appointmentTime,
                Appointment.STATUS_SCHEDULED
        );

        String createAppointmentJson = objectMapper.writeValueAsString(dto);

        String doctorToken = getToken(doctor.getEmail(), Role.DOCTOR);

        mockMvc.perform(post(APPOINTMENTS_API + "/" + doctorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAppointmentJson))
                .andExpect(status().isForbidden());

        assertEquals(0, appointmentRepository.count());
    }

    @Test
    void shouldRejectBookAppointmentWhenPatientDoesNotMatchToken() throws Exception {
        Patient patient = patientRepository.save(PatientBuilder.aPatient()
                .withEmail("an.patient@email.com")
                .build());

        Patient anotherPatient = patientRepository.save(PatientBuilder.aPatient()
                .withEmail("another.patient@email.com")
                .build());

        Doctor doctor = doctorRepository.save(DoctorBuilder.aDoctor().build());

        LocalDateTime appointmentTime = at(1);

        String patientToken = getToken(patient.getEmail(), Role.PATIENT);

        AppointmentCreateDTO anotherPatientDto = new AppointmentCreateDTO(
                new DoctorIdDTO(doctor.getId()),
                new PatientIdDTO(anotherPatient.getId()),
                appointmentTime,
                Appointment.STATUS_SCHEDULED
        );

        mockMvc.perform(post(APPOINTMENTS_API + "/" + patientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(anotherPatientDto)))
                .andExpect(status().isForbidden());

        assertEquals(0, appointmentRepository.count());
    }

    @Test
    void shouldReturnNotFoundAndNotBookAppointmentWhenReferencedDoctorDoesNotExist() throws Exception {
        Doctor anDoctor = DoctorBuilder.aDoctor().build();
        Patient anPatient = PatientBuilder.aPatient().build();

        Doctor doctor = doctorRepository.save(anDoctor);
        Patient patient = patientRepository.save(anPatient);

        long deletedDoctorId = doctor.getId();

        doctorRepository.deleteById(deletedDoctorId);

        LocalDateTime appointmentTime = at(1);

        AppointmentCreateDTO dto = new AppointmentCreateDTO(
                new DoctorIdDTO(deletedDoctorId),
                new PatientIdDTO(patient.getId()),
                appointmentTime,
                Appointment.STATUS_SCHEDULED
        );

        String createAppointmentJson = objectMapper.writeValueAsString(dto);

        String token = getToken(patient.getEmail(), Role.PATIENT);

        mockMvc.perform(post(APPOINTMENTS_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAppointmentJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.message").isNotEmpty());


        assertEquals(0, appointmentRepository.count());
    }

    @Test
    void shouldReturnNotFoundAndNotBookAppointmentWhenReferencedPatientDoesNotExist() throws Exception {
        Doctor doctor = doctorRepository.save(DoctorBuilder.aDoctor().build());
        Patient patient = patientRepository.save(PatientBuilder.aPatient()
                .withEmail("patient@email.com")
                .build());
        Patient anotherPatient = patientRepository.save(PatientBuilder.aPatient()
                .withEmail("another.patient@email.com")
                .build());

        long deletedAnotherPatientId = anotherPatient.getId();

        patientRepository.deleteById(deletedAnotherPatientId);

        LocalDateTime appointmentTime = at(1);

        AppointmentCreateDTO dto = new AppointmentCreateDTO(
                new DoctorIdDTO(doctor.getId()),
                new PatientIdDTO(deletedAnotherPatientId),
                appointmentTime,
                Appointment.STATUS_SCHEDULED
        );

        String createAppointmentJson = objectMapper.writeValueAsString(dto);

        String token = getToken(patient.getEmail(), Role.PATIENT);

        mockMvc.perform(post(APPOINTMENTS_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAppointmentJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.message").isNotEmpty());


        assertEquals(0, appointmentRepository.count());
    }

    @Test
    void shouldNotBookAppointmentWhenDoctorIsUnavailable() throws Exception {
        Doctor doctor = doctorRepository.save(DoctorBuilder.aDoctor()
                .withAvailableTimes(List.of(AvailableTime.SLOT_09_10, AvailableTime.SLOT_10_11))
                .build());
        Patient patient = patientRepository.save(PatientBuilder.aPatient().build());

        LocalDateTime appointmentTime = at(1, 11);

        AppointmentCreateDTO dto = new AppointmentCreateDTO(
                new DoctorIdDTO(doctor.getId()),
                new PatientIdDTO(patient.getId()),
                appointmentTime,
                Appointment.STATUS_SCHEDULED
        );

        String createAppointmentJson = objectMapper.writeValueAsString(dto);

        String token = getToken(patient.getEmail(), Role.PATIENT);

        mockMvc.perform(post(APPOINTMENTS_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAppointmentJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.message").isNotEmpty());


        assertEquals(0, appointmentRepository.count());
    }

    @Test
    void shouldNotBookAppointmentWhenDoctorIsAlreadyBooked() throws Exception {
        Doctor doctor = doctorRepository.save(DoctorBuilder.aDoctor()
                .withAvailableTimes(List.of(AvailableTime.SLOT_09_10, AvailableTime.SLOT_10_11))
                .build());
        Patient patientA = patientRepository.save(PatientBuilder.aPatient()
                .withEmail("patient.a@email.com")
                .build());
        Patient patientB = patientRepository.save(PatientBuilder.aPatient()
                .withEmail("patient.b@email.com")
                .build());

        LocalDateTime appointmentTime = at(1);

        Appointment existingAppointment = appointmentRepository.save(AppointmentBuilder.anAppointment()
                .withDoctor(doctor)
                .withPatient(patientB)
                .withAppointmentTime(appointmentTime)
                .build()
        );

        AppointmentCreateDTO dto = new AppointmentCreateDTO(
                new DoctorIdDTO(doctor.getId()),
                new PatientIdDTO(patientA.getId()),
                appointmentTime,
                Appointment.STATUS_SCHEDULED
        );

        String createAppointmentJson = objectMapper.writeValueAsString(dto);

        String token = getToken(patientA.getEmail(), Role.PATIENT);

        mockMvc.perform(post(APPOINTMENTS_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAppointmentJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.message").isNotEmpty());

        assertEquals(1, appointmentRepository.count());

        checkNoChangesMade(existingAppointment);
    }

    @Test
    void shouldNotBookAppointmentWhenPatientHasAnotherAppointmentAtRequestedTime() throws Exception {
        Doctor doctorA = doctorRepository.save(DoctorBuilder.aDoctor()
                .withEmail("doctor.a@email.com")
                .build());
        Doctor doctorB = doctorRepository.save(DoctorBuilder.aDoctor()
                .withEmail("doctor.b@email.com")
                .build());
        Patient patient = patientRepository.save(PatientBuilder.aPatient().build());

        LocalDateTime appointmentTime = at(1);

        Appointment existingAppointment = appointmentRepository.save(AppointmentBuilder.anAppointment()
                .withDoctor(doctorB)
                .withPatient(patient)
                .withAppointmentTime(appointmentTime)
                .build()
        );

        AppointmentCreateDTO dto = new AppointmentCreateDTO(
                new DoctorIdDTO(doctorA.getId()),
                new PatientIdDTO(patient.getId()),
                appointmentTime,
                Appointment.STATUS_SCHEDULED
        );

        String createAppointmentJson = objectMapper.writeValueAsString(dto);

        String token = getToken(patient.getEmail(), Role.PATIENT);

        mockMvc.perform(post(APPOINTMENTS_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAppointmentJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.message").isNotEmpty());

        assertEquals(1, appointmentRepository.count());

        checkNoChangesMade(existingAppointment);
    }

    @Test
    void shouldNotBookAppointmentWhenRequestedTimeIsInThePast() throws Exception {
        Doctor doctor = doctorRepository.save(DoctorBuilder.aDoctor().build());
        Patient patient = patientRepository.save(PatientBuilder.aPatient().build());

        LocalDateTime appointmentTime = before(1);

        AppointmentCreateDTO dto = new AppointmentCreateDTO(
                new DoctorIdDTO(doctor.getId()),
                new PatientIdDTO(patient.getId()),
                appointmentTime,
                Appointment.STATUS_SCHEDULED
        );

        String createAppointmentJson = objectMapper.writeValueAsString(dto);

        String token = getToken(patient.getEmail(), Role.PATIENT);

        mockMvc.perform(post(APPOINTMENTS_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAppointmentJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.message").isNotEmpty());

        assertEquals(0, appointmentRepository.count());
    }

    @Test
    void shouldNotBookAppointmentWhenAppointmentTimeHasWrongFormat() throws Exception {
        Doctor doctor = doctorRepository.save(DoctorBuilder.aDoctor().build());
        Patient patient = patientRepository.save(PatientBuilder.aPatient().build());

        String createAppointmentJson = """
                {
                    "doctor":{
                        "id":%d
                    },
                    "patient":
                        {
                        "id":%d
                    },
                    "appointmentTime":"T:00",
                    "status":0
                }
                """.formatted(doctor.getId(), patient.getId());

        String token = getToken(patient.getEmail(), Role.PATIENT);

        mockMvc.perform(post(APPOINTMENTS_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAppointmentJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.message").isNotEmpty());

        assertEquals(0, appointmentRepository.count());
    }

    @Test
    void shouldNotBookAppointmentOnMissingRequiredFiled() throws Exception {
        Patient patient = patientRepository.save(PatientBuilder.aPatient().build());

        String createAppointmentJson = """
                {
                }
                """;

        String token = getToken(patient.getEmail(), Role.PATIENT);

        mockMvc.perform(post(APPOINTMENTS_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAppointmentJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.errors").isMap())
                .andExpect(jsonPath("$.errors").isNotEmpty());

        assertEquals(0, appointmentRepository.count());
    }

    @Test
    void shouldUpdateAppointmentSuccessfully() throws Exception {
        Patient anPatient = PatientBuilder.aPatient().build();
        Doctor anDoctor = DoctorBuilder.aDoctor()
                .withAvailableTimes(AVAILABLE_TIMES)
                .build();

        Patient patient = patientRepository.save(anPatient);
        Doctor doctor = doctorRepository.save(anDoctor);

        LocalDateTime appointmentTime = at(1);
        LocalDateTime newAppointmentTime = at(2);

        Appointment anAppointment = AppointmentBuilder.anAppointment()
                .withDoctor(doctor)
                .withPatient(patient)
                .withAppointmentTime(appointmentTime)
                .build();

        Appointment appointment = appointmentRepository.save(anAppointment);

        AppointmentUpdateDTO dto = new AppointmentUpdateDTO(
                appointment.getId(),
                new DoctorIdDTO(doctor.getId()),
                new PatientIdDTO(patient.getId()),
                newAppointmentTime,
                Appointment.STATUS_SCHEDULED
        );

        String token = getToken(patient.getEmail(), Role.PATIENT);

        mockMvc.perform(put(APPOINTMENTS_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        Optional<Appointment> appointmentUpdated = appointmentRepository.findById(appointment.getId());

        assertTrue(appointmentUpdated.isPresent());
        assertEquals(newAppointmentTime, appointmentUpdated.get().getAppointmentTime());
    }

    @Test
    void shouldRejectAppointmentUpdateWhenUserHasInvalidRole() throws Exception {
        Patient patient = patientRepository.save(PatientBuilder.aPatient().build());
        Doctor doctor = doctorRepository.save(DoctorBuilder.aDoctor()
                .withAvailableTimes(AVAILABLE_TIMES)
                .build());

        LocalDateTime appointmentTime = at(1);
        LocalDateTime newAppointmentTime = at(2);
        Appointment appointment = appointmentRepository.save(
                AppointmentBuilder.anAppointment()
                        .withDoctor(doctor)
                        .withPatient(patient)
                        .withAppointmentTime(appointmentTime)
                        .build());

        AppointmentUpdateDTO dto = new AppointmentUpdateDTO(
                appointment.getId(),
                new DoctorIdDTO(doctor.getId()),
                new PatientIdDTO(patient.getId()),
                newAppointmentTime,
                Appointment.STATUS_SCHEDULED
        );

        String token = getToken(doctor.getEmail(), Role.DOCTOR);

        mockMvc.perform(put(APPOINTMENTS_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());

        checkNoChangesMade(appointment);
    }

    @Test
    void shouldRejectAppointmentUpdateWhenUserIsNotOwner() throws Exception {
        Patient patientA = patientRepository.save(PatientBuilder.aPatient().build());
        Patient patientB = patientRepository.save(PatientBuilder.aPatient()
                .withEmail("lisa.doe@email.com")
                .build());
        Doctor doctor = doctorRepository.save(DoctorBuilder.aDoctor().build());
        LocalDateTime appointmentTime = at(1);
        LocalDateTime newAppointmentTime = at(2);
        Appointment appointment = appointmentRepository.save(
                AppointmentBuilder.anAppointment()
                        .withDoctor(doctor)
                        .withAppointmentTime(appointmentTime)
                        .withPatient(patientA)
                        .build());

        AppointmentUpdateDTO dto = new AppointmentUpdateDTO(
                appointment.getId(),
                new DoctorIdDTO(doctor.getId()),
                new PatientIdDTO(patientA.getId()),
                newAppointmentTime,
                Appointment.STATUS_SCHEDULED
        );

        String token = getToken(patientB.getEmail(), Role.PATIENT);

        mockMvc.perform(put(APPOINTMENTS_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());

        checkNoChangesMade(appointment);
    }

    @Test
    void shouldRejectAppointmentUpdateWhenAppointmentTimeIsInThePast() throws Exception {
        Patient patient = patientRepository.save(PatientBuilder.aPatient().build());
        Doctor doctor = doctorRepository.save(DoctorBuilder.aDoctor()
                .withAvailableTimes(AVAILABLE_TIMES)
                .build());
        LocalDateTime appointmentTime = at(1);
        LocalDateTime newAppointmentTime = before(2);
        Appointment appointment = appointmentRepository.save(
                AppointmentBuilder.anAppointment()
                        .withPatient(patient)
                        .withDoctor(doctor)
                        .withAppointmentTime(appointmentTime)
                        .build());

        AppointmentUpdateDTO dto = new AppointmentUpdateDTO(
                appointment.getId(),
                new DoctorIdDTO(doctor.getId()),
                new PatientIdDTO(patient.getId()),
                newAppointmentTime,
                Appointment.STATUS_SCHEDULED
        );

        String token = getToken(patient.getEmail(), Role.PATIENT);

        mockMvc.perform(put(APPOINTMENTS_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.message").isNotEmpty());

        checkNoChangesMade(appointment);
    }

    @Test
    void shouldRejectAppointmentUpdateWhenAppointmentHasIllegalStatus() throws Exception {
        Patient patient = patientRepository.save(PatientBuilder.aPatient().build());
        Doctor doctor = doctorRepository.save(DoctorBuilder.aDoctor().build());
        LocalDateTime appointmentTime = at(1);
        LocalDateTime newAppointmentTime = at(2);
        Appointment appointment = appointmentRepository.save(
                AppointmentBuilder.anAppointment()
                        .withDoctor(doctor)
                        .withPatient(patient)
                        .withAppointmentTime(appointmentTime)
                        .withStatus(Appointment.STATUS_CANCELED)
                        .build());

        AppointmentUpdateDTO dto = new AppointmentUpdateDTO(
                appointment.getId(),
                new DoctorIdDTO(doctor.getId()),
                new PatientIdDTO(patient.getId()),
                newAppointmentTime,
                Appointment.STATUS_SCHEDULED
        );

        String token = getToken(patient.getEmail(), Role.PATIENT);

        mockMvc.perform(put(APPOINTMENTS_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isNotEmpty());

        checkNoChangesMade(appointment);
    }

    @Test
    void shouldRejectAppointmentUpdateWhenDoctorIsAlreadyBooked() throws Exception {
        Patient patientA = patientRepository.save(PatientBuilder.aPatient().build());
        Patient patientB = patientRepository.save(PatientBuilder.aPatient()
                .withEmail("lisa.doe@email.com")
                .build());

        Doctor doctor = doctorRepository.save(DoctorBuilder.aDoctor()
                .withAvailableTimes(AVAILABLE_TIMES)
                .build());

        LocalDateTime appointmentTimeOfPatientA = at(1);
        LocalDateTime appointmentTimeOfPatientB = at(2);
        LocalDateTime newAppointmentTimeOfPatientA = at(2);

        Appointment appointmentOfPatientA = appointmentRepository.save(
                AppointmentBuilder.anAppointment()
                        .withPatient(patientA)
                        .withAppointmentTime(appointmentTimeOfPatientA)
                        .withDoctor(doctor)
                        .build());

        Appointment appointmentOfPatientB = appointmentRepository.save(
                AppointmentBuilder.anAppointment()
                        .withPatient(patientB)
                        .withAppointmentTime(appointmentTimeOfPatientB)
                        .withDoctor(doctor)
                        .build());

        AppointmentUpdateDTO dto = new AppointmentUpdateDTO(
                appointmentOfPatientA.getId(),
                new DoctorIdDTO(doctor.getId()),
                new PatientIdDTO(patientA.getId()),
                newAppointmentTimeOfPatientA,
                Appointment.STATUS_SCHEDULED
        );

        String token = getToken(patientA.getEmail(), Role.PATIENT);

        mockMvc.perform(put(APPOINTMENTS_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isNotEmpty());

        checkNoChangesMade(appointmentOfPatientA);
        checkNoChangesMade(appointmentOfPatientB);
    }

    @Test
    void shouldRejectAppointmentUpdateWhenPatientHasAppointmentAtTheSameTime() throws Exception {
        Patient patient = patientRepository.save(PatientBuilder.aPatient().build());
        Doctor doctorA = doctorRepository.save(DoctorBuilder.aDoctor()
                .withAvailableTimes(AVAILABLE_TIMES)
                .withEmail("john.doe@email.com")
                .build());
        Doctor doctorB = doctorRepository.save(DoctorBuilder.aDoctor()
                .withAvailableTimes(AVAILABLE_TIMES)
                .withEmail("jane.doe@email.com")
                .build());

        LocalDateTime appointmentTimeA = at(1);
        LocalDateTime appointmentTimeB = at(2);
        LocalDateTime newAppointmentTimeForA = at(2);

        Appointment appointmentA = appointmentRepository.save(AppointmentBuilder.anAppointment()
                .withPatient(patient)
                .withDoctor(doctorA)
                .withAppointmentTime(appointmentTimeA)
                .build());

        Appointment appointmentB = appointmentRepository.save(AppointmentBuilder.anAppointment()
                .withPatient(patient)
                .withDoctor(doctorB)
                .withAppointmentTime(appointmentTimeB)
                .build());

        AppointmentUpdateDTO dto = new AppointmentUpdateDTO(
                appointmentA.getId(),
                new DoctorIdDTO(doctorA.getId()),
                new PatientIdDTO(patient.getId()),
                newAppointmentTimeForA,
                Appointment.STATUS_SCHEDULED
        );

        String token = getToken(patient.getEmail(), Role.PATIENT);

        mockMvc.perform(put(APPOINTMENTS_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isNotEmpty());

        checkNoChangesMade(appointmentA);
        checkNoChangesMade(appointmentB);
    }

    @Test
    void shouldReturnNotFoundWhenAppointmentDoesNotExist() throws Exception {
        Patient patient = patientRepository.save(PatientBuilder.aPatient().build());
        Doctor doctor = doctorRepository.save(DoctorBuilder.aDoctor().build());
        LocalDateTime appointmentTime = at(1);
        LocalDateTime newAppointmentTime = at(2);

        Appointment appointment = appointmentRepository.save(AppointmentBuilder.anAppointment()
                .withDoctor(doctor)
                .withPatient(patient)
                .withAppointmentTime(appointmentTime)
                .build()
        );

        long deletedAppointmentId = appointment.getId();
        appointmentRepository.delete(appointment);

        AppointmentUpdateDTO dto = new AppointmentUpdateDTO(
                deletedAppointmentId,
                new DoctorIdDTO(doctor.getId()),
                new PatientIdDTO(patient.getId()),
                newAppointmentTime,
                Appointment.STATUS_SCHEDULED
        );

        String token = getToken(patient.getEmail(), Role.PATIENT);

        mockMvc.perform(put(APPOINTMENTS_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());

        Optional<Appointment> appointmentUpdated = appointmentRepository.findById(deletedAppointmentId);

        assertTrue(appointmentUpdated.isEmpty());
    }

    @Test
    void shouldRejectAppointmentUpdateWhenDoctorIsUnavailable() throws Exception {
        Patient patient = patientRepository.save(PatientBuilder.aPatient().build());
        Doctor doctor = doctorRepository.save(DoctorBuilder.aDoctor().build());
        LocalDateTime appointmentTime = at(1, 10);
        LocalDateTime newAppointmentTime = at(1, 15);
        Appointment appointment = appointmentRepository.save(AppointmentBuilder.anAppointment()
                .withDoctor(doctor)
                .withPatient(patient)
                .withAppointmentTime(appointmentTime)
                .build());

        AppointmentUpdateDTO dto = new AppointmentUpdateDTO(
                appointment.getId(),
                new DoctorIdDTO(doctor.getId()),
                new PatientIdDTO(patient.getId()),
                newAppointmentTime,
                Appointment.STATUS_SCHEDULED
        );

        String token = getToken(patient.getEmail(), Role.PATIENT);

        mockMvc.perform(put(APPOINTMENTS_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isNotEmpty());


        checkNoChangesMade(appointment);
    }

    @Test
    void shouldRejectAppointmentUpdateWhenMissingRequiredFields() throws Exception {
        Patient anPatient = PatientBuilder.aPatient().build();
        Doctor anDoctor = DoctorBuilder.aDoctor()
                .withAvailableTimes(AVAILABLE_TIMES)
                .build();

        Patient patient = patientRepository.save(anPatient);
        Doctor doctor = doctorRepository.save(anDoctor);

        LocalDateTime appointmentTime = at(1);
        LocalDateTime newAppointmentTime = at(2);

        Appointment anAppointment = AppointmentBuilder.anAppointment()
                .withDoctor(doctor)
                .withPatient(patient)
                .withAppointmentTime(appointmentTime)
                .build();

        Appointment appointment = appointmentRepository.save(anAppointment);

        String updateAppointmentJson = """
                {
                    "id": %d
                }
                """.formatted(appointment.getId(), newAppointmentTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        String token = getToken(patient.getEmail(), Role.PATIENT);

        mockMvc.perform(put(APPOINTMENTS_API + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateAppointmentJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.errors").isMap())
                .andExpect(jsonPath("$.errors").isNotEmpty());

        checkNoChangesMade(appointment);
    }

    @Test
    void shouldGetAllDoctorAppointmentsOfTheSpecifiedDate() throws Exception {
        Doctor doctor = doctorRepository.save(DoctorBuilder.aDoctor().build());
        Patient patient = patientRepository.save(PatientBuilder.aPatient().build());
        LocalDateTime appointmentTime = at(1);

        Appointment appointment = appointmentRepository.save(
                AppointmentBuilder.anAppointment()
                        .withDoctor(doctor)
                        .withPatient(patient)
                        .withAppointmentTime(appointmentTime)
                        .build()
        );

        appointmentRepository.save(
                AppointmentBuilder.anAppointment()
                        .withDoctor(doctor)
                        .withPatient(patient)
                        .withAppointmentTime(appointmentTime.plusDays(2))
                        .build()
        );


        String specifiedDate = appointmentTime.toLocalDate().format(DATE_FORMATTER);
        String token = getToken(doctor.getEmail(), Role.DOCTOR);

        mockMvc.perform(get(APPOINTMENTS_API + "/" + specifiedDate + "/" + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointments").exists())
                .andExpect(jsonPath("$.appointments").isArray())
                .andExpect(jsonPath("$.appointments.length()").value(1))
                .andExpect(jsonPath("$.appointments[0].appointmentId").value(appointment.getId()))
                .andExpect(jsonPath("$.appointments[0].doctorId").value(doctor.getId()))
                .andExpect(jsonPath("$.appointments[0].patientId").value(patient.getId()))
                .andExpect(jsonPath("$.appointments[0].patientName").value(patient.getName()))
                .andExpect(jsonPath("$.appointments[0].patientPhone").value(patient.getPhone()))
                .andExpect(jsonPath("$.appointments[0].patientEmail").value(patient.getEmail()));
    }

    @Test
    void shouldReturnEmptyAppointmentListWhenDoctorHasNoAppointmentsAtTheSpecifiedDate() throws Exception {
        Doctor doctor = doctorRepository.save(DoctorBuilder.aDoctor().build());

        String specifiedDate = at(1).toLocalDate().format(DATE_FORMATTER);
        String token = getToken(doctor.getEmail(), Role.DOCTOR);

        mockMvc.perform(get(APPOINTMENTS_API + "/" + specifiedDate + "/" + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointments").exists())
                .andExpect(jsonPath("$.appointments").isArray())
                .andExpect(jsonPath("$.appointments").isEmpty());
    }

    @Test
    void shouldReturnBadRequestOnInvalidDateFormat() throws Exception {
        Doctor doctor = doctorRepository.save(DoctorBuilder.aDoctor().build());

        String specifiedDate = at(1).toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String token = getToken(doctor.getEmail(), Role.DOCTOR);

        mockMvc.perform(get(APPOINTMENTS_API + "/" + specifiedDate + "/" + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNotADoctor() throws Exception {
        String specifiedDate = at(1).toLocalDate().format(DATE_FORMATTER);
        String token = getToken(PatientBuilder.aPatient().build().getEmail(), Role.PATIENT);

        mockMvc.perform(get(APPOINTMENTS_API + "/" + specifiedDate + "/" + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenAuthenticatedDoctorDoesNotExist() throws Exception {
        String specifiedDate = at(1).toLocalDate().format(DATE_FORMATTER);
        String token = getToken("missing.doctor@email.com", Role.DOCTOR);

        mockMvc.perform(get(APPOINTMENTS_API + "/" + specifiedDate + "/" + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldGetAllDoctorAppointmentsOfTheSpecifiedDateAndContainsName() throws Exception {
        Doctor doctor = doctorRepository.save(DoctorBuilder.aDoctor().build());
        Patient patient = patientRepository.save(PatientBuilder.aPatient()
                .withName("Jane Doe")
                .build());
        LocalDateTime appointmentTime = at(1);

        Appointment appointment = appointmentRepository.save(
                AppointmentBuilder.anAppointment()
                        .withDoctor(doctor)
                        .withPatient(patient)
                        .withAppointmentTime(appointmentTime)
                        .build()
        );

        appointmentRepository.save(
                AppointmentBuilder.anAppointment()
                        .withDoctor(doctor)
                        .withPatient(patient)
                        .withAppointmentTime(appointmentTime.plusDays(2))
                        .build()
        );

        String specifiedDate = appointmentTime.toLocalDate().format(DATE_FORMATTER);
        String token = getToken(doctor.getEmail(), Role.DOCTOR);

        mockMvc.perform(get(APPOINTMENTS_API + "/" + specifiedDate + "/search/jane/" + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointments").exists())
                .andExpect(jsonPath("$.appointments").isArray())
                .andExpect(jsonPath("$.appointments.length()").value(1))
                .andExpect(jsonPath("$.appointments[0].appointmentId").value(appointment.getId()))
                .andExpect(jsonPath("$.appointments[0].doctorId").value(doctor.getId()))
                .andExpect(jsonPath("$.appointments[0].patientId").value(patient.getId()))
                .andExpect(jsonPath("$.appointments[0].patientName").value(patient.getName()))
                .andExpect(jsonPath("$.appointments[0].patientPhone").value(patient.getPhone()))
                .andExpect(jsonPath("$.appointments[0].patientEmail").value(patient.getEmail()));
    }

    @Test
    void shouldReturnEmptyListWhenNoAppointmentsMatchPatientNameFilter() throws Exception {
        Doctor doctor = doctorRepository.save(DoctorBuilder.aDoctor().build());
        Patient patient = patientRepository.save(PatientBuilder.aPatient()
                .withName("Jane Doe")
                .build());
        LocalDateTime appointmentTime = at(1);

        appointmentRepository.save(
                AppointmentBuilder.anAppointment()
                        .withDoctor(doctor)
                        .withPatient(patient)
                        .withAppointmentTime(appointmentTime)
                        .build());

        String specifiedDate = appointmentTime.toLocalDate().format(DATE_FORMATTER);
        String token = getToken(doctor.getEmail(), Role.DOCTOR);

        mockMvc.perform(get(APPOINTMENTS_API + "/" + specifiedDate + "/search/john/" + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointments").exists())
                .andExpect(jsonPath("$.appointments").isArray())
                .andExpect(jsonPath("$.appointments").isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenNoAppointmentsMatchDateAndPatientName() throws Exception {
        Doctor doctor = doctorRepository.save(DoctorBuilder.aDoctor().build());
        Patient patientA = patientRepository.save(PatientBuilder.aPatient()
                .withName("Jane Doe")
                .withEmail("jane.doe@email.com")
                .build());

        Patient patientB = patientRepository.save(PatientBuilder.aPatient()
                .withName("John Doe")
                .withEmail("john.doe@email.com")
                .build());

        LocalDateTime appointmentTimeA = at(1);
        LocalDateTime appointmentTimeB = at(2);

        appointmentRepository.save(
                AppointmentBuilder.anAppointment()
                        .withDoctor(doctor)
                        .withPatient(patientA)
                        .withAppointmentTime(appointmentTimeA)
                        .build());

        appointmentRepository.save(
                AppointmentBuilder.anAppointment()
                        .withDoctor(doctor)
                        .withPatient(patientB)
                        .withAppointmentTime(appointmentTimeB)
                        .build());

        String specifiedDate = appointmentTimeA.toLocalDate().format(DATE_FORMATTER);
        String token = getToken(doctor.getEmail(), Role.DOCTOR);

        mockMvc.perform(get(APPOINTMENTS_API + "/" + specifiedDate + "/search/john/" + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointments").exists())
                .andExpect(jsonPath("$.appointments").isArray())
                .andExpect(jsonPath("$.appointments").isEmpty());
    }

    private void checkNoChangesMade(Appointment original) {
        Appointment persisted = appointmentRepository.findById(original.getId()).orElseThrow();

        assertEquals(original.getId(), persisted.getId());
        assertEquals(original.getAppointmentTime(), persisted.getAppointmentTime());
        assertEquals(original.getDoctor().getId(), persisted.getDoctor().getId());
        assertEquals(original.getPatient().getId(), persisted.getPatient().getId());
        assertEquals(original.getStatus(), persisted.getStatus());
    }

    private String getToken(String email, String role) {
        UserDetails patientUser = User.builder()
                .username(email)
                .password("test")
                .roles(role.toUpperCase())
                .build();

        return tokenService.generateToken(patientUser);
    }

    private LocalDateTime at(int days) {
        return LocalDateTime.now(clock)
                .plusDays(days)
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }

    private LocalDateTime at(int days, int hour) {
        return LocalDateTime.now(clock)
                .plusDays(days)
                .withHour(hour)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }

    private LocalDateTime before(int days) {
        return LocalDateTime.now(clock)
                .minusDays(days)
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }
}
