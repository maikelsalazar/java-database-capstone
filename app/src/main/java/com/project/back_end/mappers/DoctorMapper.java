package com.project.back_end.mappers;

import com.project.back_end.DTO.DoctorCreateDTO;
import com.project.back_end.DTO.DoctorDTO;
import com.project.back_end.DTO.DoctorProfileUpdateDTO;
import com.project.back_end.enums.AvailableTime;
import com.project.back_end.models.Doctor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

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

    public static List<DoctorDTO> toDTOList(List<Doctor> doctorList) {
        return doctorList.stream()
                .map(DoctorMapper::toDTO)
                .toList();
    }

    public static List<DoctorDTO> toDTOList(List<Doctor> doctorList, String time) {
        if (time == null || time.isBlank()) {
            return toDTOList(doctorList);
        }

        String timeRequested = time.toUpperCase();

        if (!timeRequested.equals("AM") && !timeRequested.equals("PM")) {
            return toDTOList(doctorList);
        }

        Set<AvailableTime> slots =
                timeRequested.equals("AM")
                        ? AvailableTime.amTimes()
                        : AvailableTime.pmTimes();

        return doctorList.stream()
                .filter(doctor -> doctor.getAvailableTimes().stream()
                        .anyMatch(slots::contains)
                )
                .map(doctor -> {
                    DoctorDTO dto = DoctorMapper.toDTO(doctor);
                    dto.setAvailableTimes(
                            dto.getAvailableTimes().stream()
                                    .filter(slots::contains)
                                    .toList()
                    );
                    return dto;
                })
                .toList();
    }

    public static Doctor fromCreateDTO(DoctorCreateDTO dto, PasswordEncoder passwordEncoder) {
        Doctor doctor = new Doctor();

        doctor.setName(dto.name());
        doctor.setSpecialty(dto.specialty());
        doctor.setEmail(dto.email());
        doctor.setPassword(passwordEncoder.encode(dto.password()));
        doctor.setPhone(dto.phone());
        doctor.setAvailableTimes(dto.availableTimes());

        return doctor;
    }
}
