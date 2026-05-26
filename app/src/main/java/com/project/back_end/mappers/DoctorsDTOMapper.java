package com.project.back_end.mappers;

import com.project.back_end.DTO.DoctorDTO;
import com.project.back_end.DTO.DoctorsDTO;
import com.project.back_end.enums.AvailableTime;
import com.project.back_end.models.Doctor;

import java.util.List;
import java.util.Set;

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

        String timeRequested = time.toUpperCase();

        if (!timeRequested.equals("AM") && !timeRequested.equals("PM")) {
            return fromDoctorList(doctorList);
        }

        Set<AvailableTime> slots =
                timeRequested.equals("AM")
                    ? AvailableTime.amTimes()
                    : AvailableTime.pmTimes();

        List<DoctorDTO> doctors = doctorList.stream()
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

        return new DoctorsDTO(doctors);
    }
}
