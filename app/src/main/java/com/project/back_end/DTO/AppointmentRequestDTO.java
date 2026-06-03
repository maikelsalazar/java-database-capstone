package com.project.back_end.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record AppointmentRequestDTO(Long id,
                                    DoctorIdDTO doctor,
                                    PatientIdDTO patient,
                                    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:00")
                                    LocalDateTime appointmentTime,
                                    Integer status
) {
}
