package com.project.back_end.mappers;

import com.project.back_end.DTO.DoctorDTO;
import com.project.back_end.models.Doctor;

public class DoctorMapper {

    public static DoctorDTO toDTO(Doctor doctor) {
        return new DoctorDTO(
                doctor.getId(),
                doctor.getName(),
                doctor.getSpecialty(),
                doctor.getEmail(),
                doctor.getPhone(),
                doctor.getAvailableTimes()
        );
    }
}
