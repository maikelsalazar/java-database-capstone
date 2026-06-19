package com.project.back_end.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public record AppointmentCreateDTO(
        @NotNull(message = "Doctor is required")
        @Valid
        DoctorIdDTO doctor,

        @NotNull(message = "Patient is required")
        @Valid
        PatientIdDTO patient,

        @NotNull(message = "Appointment time is required")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime appointmentTime,

        @NotNull(message = "Status is required")
        Integer status) {

    public AppointmentCreateDTO {
        if (appointmentTime != null) {
            appointmentTime = appointmentTime.truncatedTo(ChronoUnit.MINUTES);
        }
    }
}
