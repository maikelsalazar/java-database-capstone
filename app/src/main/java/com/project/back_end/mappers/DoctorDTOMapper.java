package com.project.back_end.mappers;

import com.project.back_end.DTO.DoctorCreateDTO;
import com.project.back_end.models.Doctor;
import org.springframework.security.crypto.password.PasswordEncoder;

public class DoctorDTOMapper {

    public static Doctor fromCreate(DoctorCreateDTO dto, PasswordEncoder passwordEncoder) {
        Doctor doctor = new Doctor();

        doctor.setName(dto.getName());
        doctor.setSpecialty(dto.getSpecialty());
        doctor.setEmail(dto.getEmail());
        doctor.setPassword(
                passwordEncoder.encode(dto.getPassword())
        );
        doctor.setPhone(dto.getPhone());
        doctor.setAvailableTimes(dto.getAvailableTimes());

        return doctor;
    }
}
