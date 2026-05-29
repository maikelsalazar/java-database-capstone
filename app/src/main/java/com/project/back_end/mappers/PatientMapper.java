package com.project.back_end.mappers;

import com.project.back_end.DTO.PatientDTO;
import com.project.back_end.models.Patient;

public class PatientMapper {
    public static PatientDTO toDTO(Patient patient) {
        return new PatientDTO(
                patient.getId(),
                patient.getName(),
                patient.getEmail(),
                patient.getPhone(),
                patient.getAddress()
        );
    }
}
