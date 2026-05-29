package com.project.back_end.mappers;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;

import java.util.List;

public class AppointmentMapper {
    public static AppointmentDTO toDTO(Appointment appointment) {
        Doctor doctor = appointment.getDoctor();
        Patient patient = appointment.getPatient();

        return new AppointmentDTO(
                appointment.getId(),
                doctor.getId(),
                doctor.getName(),
                patient.getId(),
                patient.getName(),
                patient.getEmail(),
                patient.getPhone(),
                patient.getAddress(),
                appointment.getAppointmentTime(),
                appointment.getStatus()
        );
    }

    public static List<AppointmentDTO> toDTOList(List<Appointment> appointments) {
        if (appointments == null) return List.of();

        return appointments.stream()
                .map(AppointmentMapper::toDTO)
                .toList();
    }
}
