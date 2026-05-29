package com.project.back_end.mappers;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppointmentMapperTest {

    @Test
    void shouldMapAppointmentListToDTOList() {
        assertEquals(
                List.of(anAppointmentDTO()),
                AppointmentMapper.toDTOList(List.of(anAppointmentModel()))
        );
    }

    @Test
    void shouldReturnEmptyListWhenInputListIsEmpty() {
        assertEquals(List.of(), AppointmentMapper.toDTOList(List.of()));
    }

    @Test
    void shouldReturnEmptyListWhenInputListIsNull() {
        assertEquals(List.of(), AppointmentMapper.toDTOList(null));
    }

    private static Appointment anAppointmentModel() {
        Doctor doctor = new Doctor();
        doctor.setId(2L);
        doctor.setName("John Doe");

        Patient patient = new Patient();
        patient.setId(3L);
        patient.setName("Jane Doe");
        patient.setEmail("jane.doe@email.com");
        patient.setPhone("1234567890");
        patient.setAddress("Av. 12, NY");

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentTime(LocalDateTime.of(2025, 5, 10, 10, 15));
        appointment.setStatus(0);

        return appointment;
    }

    private static AppointmentDTO anAppointmentDTO() {
        return new AppointmentDTO(
                1L,
                2L,
                "John Doe",
                3L,
                "Jane Doe",
                "jane.doe@email.com",
                "1234567890",
                "Av. 12, NY",
                LocalDateTime.of(2025, 5, 10, 10, 15),
                0
        );
    }
}
