package com.project.back_end.mappers;

import com.project.back_end.DTO.DoctorDTO;
import com.project.back_end.DTO.DoctorsDTO;
import com.project.back_end.models.Doctor;

import java.util.List;

public class DoctorsDTOMapper {

    public static DoctorsDTO fromDoctorList(List<Doctor> doctorList) {
        List<DoctorDTO> doctors = doctorList.stream()
                .map(DoctorMapper::toDTO)
                .toList();

        return new DoctorsDTO(doctors);
    }

    public static DoctorsDTO fromDoctorList(List<Doctor> doctorList, String time) {
        if (time == null || time.isBlank()) {
            return fromDoctorList(doctorList);
        }

        if (!time.equalsIgnoreCase("AM") && !time.equalsIgnoreCase("PM")) {
            return fromDoctorList(doctorList);
        }


        List<DoctorDTO> doctors = doctorList.stream()
                .map(doctor -> {
                    DoctorDTO dto = DoctorMapper.toDTO(doctor);
                    dto.setAvailableTimes(filterTimes(dto.getAvailableTimes(), time.toUpperCase()));

                    return dto;
                })
                .toList();

        return new DoctorsDTO(doctors);
    }

    private static List<String> filterTimes(List<String> times, String time) {
        if (time.equalsIgnoreCase("AM")) {
            return times.stream()
                    .filter(t -> Integer.parseInt(t.split(":")[0]) < 12)
                    .toList();
        } else {
            return times.stream()
                    .filter(t -> Integer.parseInt(t.split(":")[0]) >= 12)
                    .toList();
        }
    }
}
