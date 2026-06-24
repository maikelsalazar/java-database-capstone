package com.project.back_end.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AppointmentUpdateDTO(
    @NotNull(message = "Appointment's id is required")
    Long id,

    @NotNull(message = "Doctor is required")
    @Valid
    DoctorIdDTO doctor,

    @NotNull(message = "Patient is required")
    @Valid
    PatientIdDTO patient,

    @NotNull(message = "Appointment time is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:00")
    LocalDateTime appointmentTime,

    @NotNull(message = "Status is required")
    Integer status
) {
}
