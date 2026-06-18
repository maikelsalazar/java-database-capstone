package com.project.back_end.mappers;

import com.project.back_end.DTO.DoctorCreateDTO;
import com.project.back_end.models.Doctor;
import org.springframework.security.crypto.password.PasswordEncoder;

public class DoctorDTOMapper {

    public static Doctor fromCreate(DoctorCreateDTO dto, PasswordEncoder passwordEncoder) {
        Doctor doctor = new Doctor();

        doctor.setName(dto.name());
        doctor.setSpecialty(dto.specialty());
        doctor.setEmail(dto.email());
        doctor.setPassword(
                passwordEncoder.encode(dto.password())
        );
        doctor.setPhone(dto.phone());
        doctor.setAvailableTimes(dto.availableTimes());

        return doctor;
    }
}
