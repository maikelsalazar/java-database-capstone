package com.project.back_end.services;

import com.project.back_end.DTO.DoctorsDTO;

@org.springframework.stereotype.Service
public class Service {

    private DoctorService doctorService;

    public Service(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    public DoctorsDTO filterDoctor(String nameQuery, String timeQuery, String specialtyQuery) {
        String name = sanitizeQuery(nameQuery);
        String time = sanitizeQuery(timeQuery);
        String specialty = sanitizeQuery(specialtyQuery);


        String filter = (name.isEmpty() ? "_" : "N")
                        + (time.isEmpty() ? "_" : "T")
                        + (specialty.isEmpty() ? "_" : "S");

        return switch (filter) {
            case "N__" -> doctorService.findDoctorByName(name);
            case "_T_" -> doctorService.findDoctorByTime(time);
            case "__S" -> doctorService.findDoctorBySpecialty(specialty);
            case "NT_" -> doctorService.filterDoctorByNameAndTime(name, time);
            case "N_S" -> doctorService.filterDoctorByNameAndSpecialty(name, specialty);
            case "_TS" -> doctorService.filterDoctorByTimeAndSpecialty(time, specialty);
            case "NTS" -> doctorService.filterDoctorsByNameAndSpecialtyAndTime(name, time, specialty);
            default -> doctorService.getDoctors();
        };
    }

    private String sanitizeQuery(String query) {
        if (query == null) {
            return "";
        }

        String value = query.trim();

        if (value.equals("*")) {
            return "";
        }

        return value;
    }
}
