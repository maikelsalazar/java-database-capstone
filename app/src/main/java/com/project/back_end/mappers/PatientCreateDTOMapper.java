package com.project.back_end.mappers;

import com.project.back_end.DTO.PatientCreateDTO;
import com.project.back_end.models.Patient;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PatientCreateDTOMapper {

    public static Patient toEntity(PatientCreateDTO dto, PasswordEncoder passwordEncoder) {
        Patient patient = new Patient();
        patient.setName(dto.name());
        patient.setEmail(dto.email());
        patient.setPassword(passwordEncoder.encode(dto.password()));
        patient.setPhone(dto.phone());
        patient.setAddress(dto.address());

        return patient;
    }
}
