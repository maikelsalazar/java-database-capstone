package com.project.back_end.mappers;

import com.project.back_end.DTO.PatientCreateDTO;
import com.project.back_end.models.Patient;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PatientCreateDTOMapper {

    public static Patient toEntity(PatientCreateDTO dto, PasswordEncoder passwordEncoder) {
        Patient patient = new Patient();
        patient.setName(dto.getName());
        patient.setEmail(dto.getEmail());
        patient.setPassword(passwordEncoder.encode(dto.getPassword()));
        patient.setPhone(dto.getPhone());
        patient.setAddress(dto.getAddress());

        return patient;
    }
}
