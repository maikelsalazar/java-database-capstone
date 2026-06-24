package com.project.back_end.controllers;

import com.project.back_end.DTO.*;
import com.project.back_end.security.Role;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    private final DoctorService doctorService;

    private final Service service;

    public DoctorController(DoctorService doctorService, Service service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    @GetMapping("/list")
    public ResponseEntity<DoctorsDTO> getDoctor() {
        DoctorsDTO doctorsDTO = doctorService.getDoctors();

        return ResponseEntity.ok(doctorsDTO);
    }

    @GetMapping("/filter/{name}/{time}/{specialty}")
    public ResponseEntity<DoctorsDTO> filter(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String specialty) {
        DoctorsDTO doctorsDTO = service.filterDoctor(name, time, specialty);

        return ResponseEntity.ok(doctorsDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> doctorLogin(@Valid @RequestBody EmailLoginDTO loginRequest) {
        String token = service.validateDoctor(loginRequest);

        if (token == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(LoginResponseDTO.failure());
        }

        return ResponseEntity.ok(LoginResponseDTO.success(token));
    }

    @PostMapping("/{token}")
    public ResponseEntity<ApiResponseDTO> saveDoctor(@PathVariable String token, @Valid @RequestBody DoctorCreateDTO newDoctorRequest) {

        service.validateTokenOrThrow(token, Role.ADMIN);

        doctorService.saveDoctor(newDoctorRequest);

        return ResponseEntity.ok(ApiResponseDTO.success("Doctor added successfully"));
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Void> deleteDoctor(
            @PathVariable Long id,
            @PathVariable String token
    ) {

        service.validateTokenOrThrow(token, Role.ADMIN);
        doctorService.deleteDoctor(id);

        return ResponseEntity.noContent().build();
    }
}
