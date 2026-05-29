package com.project.back_end.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record AppointmentDTO(Long id,
                             Long doctorId,
                             String doctorName,
                             Long patientId,
                             String patientName,
                             String patientEmail,
                             String patientPhone,
                             String patientAddress,
                             @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                             LocalDateTime appointmentTime,
                             int status) {

    public LocalDate getAppointmentDate() {
        return appointmentTime.toLocalDate();
    }

    @JsonFormat(pattern = "HH:mm")
    public LocalTime getAppointmentTimeOnly() {
        return appointmentTime.toLocalTime();
    }

    public LocalTime endTime() {
        return getAppointmentTimeOnly().plusHours(1);
    }
}
