package com.project.back_end.services;

import com.project.back_end.DTO.AdminLoginDTO;
import com.project.back_end.DTO.DoctorsDTO;
import com.project.back_end.models.Admin;
import com.project.back_end.repo.AdminRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@org.springframework.stereotype.Service
public class Service {

    private DoctorService doctorService;

    private AdminRepository adminRepository;

    private PasswordEncoder passwordEncoder;

    private CustomUserDetailsService userDetailsService;

    private TokenService tokenService;

    public Service(DoctorService doctorService,
                   AdminRepository adminRepository,
                   PasswordEncoder passwordEncoder,
                   CustomUserDetailsService userDetailsService,
                   TokenService tokenService) {
        this.doctorService = doctorService;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.tokenService = tokenService;
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

    public String validateAdmin(AdminLoginDTO adminRequest) {
        Admin admin = adminRepository.findByUsername(adminRequest.getUsername());

        if (admin == null) return null;

        boolean valid = passwordEncoder.matches(
                adminRequest.getPassword(),
                admin.getPassword()
        );

        if (!valid) return null;

        UserDetails user = userDetailsService.buildUser(admin);

        return tokenService.generateToken(user);
    }

    public boolean validateToken(String userToken, String role) {
        return (tokenService.validateToken(userToken, role));
    }
}
