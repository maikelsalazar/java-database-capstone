package com.project.back_end.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PrescriptionCreateDTO(
        @NotNull(message = "Appointment Id is required")
        Long appointmentId,

        @NotBlank(message = "Patient's Name is required")
        @Size(min = 3, max = 100)
        String patientName,

        @NotBlank(message = "Medication is required")
        @Size(min = 3, max = 100)
        String medication,

        @NotBlank(message = "Dosage is required")
        @Size(min = 3, max = 100)
        String dosage,

        @Size(max = 200)
        String doctorNotes
) {
}
