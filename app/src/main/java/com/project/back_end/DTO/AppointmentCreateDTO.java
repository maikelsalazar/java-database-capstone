package com.project.back_end.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public record AppointmentCreateDTO(DoctorIdDTO doctor,
                                   PatientIdDTO patient,
                                   @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
                                   LocalDateTime appointmentTime,
                                   Integer status) {

    public AppointmentCreateDTO {
        if (appointmentTime != null) {
            appointmentTime = appointmentTime.truncatedTo(ChronoUnit.MINUTES);
        }
    }
}
