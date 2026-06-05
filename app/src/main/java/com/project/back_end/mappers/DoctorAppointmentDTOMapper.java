package com.project.back_end.mappers;

import com.project.back_end.DTO.DoctorAppointmentDTO;
import com.project.back_end.models.Appointment;

import java.util.List;

public class DoctorAppointmentDTOMapper {

    public static List<DoctorAppointmentDTO> fromList(List<Appointment> appointments) {
        return appointments.stream()
                .map(DoctorAppointmentDTOMapper::fromEntity)
                .toList();
    }

    public static DoctorAppointmentDTO fromEntity(Appointment appointment) {
        return new DoctorAppointmentDTO(
                appointment.getId(),
                appointment.getDoctor().getId(),
                appointment.getPatient().getId(),
                appointment.getPatient().getName(),
                appointment.getPatient().getPhone(),
                appointment.getPatient().getEmail()
        );
    }
}
