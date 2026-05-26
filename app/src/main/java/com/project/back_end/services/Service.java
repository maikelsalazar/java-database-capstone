package com.project.back_end.services;

import com.project.back_end.DTO.AdminLoginDTO;
import com.project.back_end.DTO.DoctorLoginDTO;
import com.project.back_end.DTO.DoctorsDTO;
import com.project.back_end.exceptions.UnauthorizedException;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@org.springframework.stereotype.Service
public class Service {

    private DoctorService doctorService;

    private AdminRepository adminRepository;

    private DoctorRepository doctorRepository;

    private PasswordEncoder passwordEncoder;

    private CustomUserDetailsService userDetailsService;

    private TokenService tokenService;

    public Service(DoctorService doctorService,
                   AdminRepository adminRepository,
                   DoctorRepository doctorRepository,
                   PasswordEncoder passwordEncoder,
                   CustomUserDetailsService userDetailsService,
                   TokenService tokenService) {
        this.doctorService = doctorService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
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

    public String validateDoctor(DoctorLoginDTO doctorLogin) {
        Doctor doctor = doctorRepository.findByEmail(doctorLogin.getEmail());

        if (doctor == null) return null;

        boolean valid = passwordEncoder.matches(
                doctorLogin.getPassword(),
                doctor.getPassword()
        );

        if (!valid) return null;

        UserDetails user = userDetailsService.buildUser(doctor);

        return tokenService.generateToken(user);
    }

    public boolean validateToken(String userToken, String role) {
        return (tokenService.validateToken(userToken, role));
    }

    public void validateTokenOrThrow(String userToken, String role) {
        if (!validateToken(userToken, role)) {
            throw new UnauthorizedException("Invalid token");
        }
    }
}
