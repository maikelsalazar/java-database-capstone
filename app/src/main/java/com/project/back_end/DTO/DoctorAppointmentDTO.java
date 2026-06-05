package com.project.back_end.DTO;

public record DoctorAppointmentDTO(
        Long appointmentId,
        Long doctorId,
        Long patientId,
        String patientName,
        String patientPhone,
        String patientEmail
) {
}
