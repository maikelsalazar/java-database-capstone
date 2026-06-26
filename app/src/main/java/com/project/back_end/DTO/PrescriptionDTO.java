package com.project.back_end.DTO;

public record PrescriptionDTO(
        Long appointmentId,
        String patientName,
        String medication,
        String dosage,
        String doctorNotes) {
}
