package com.project.back_end.controllers;

import com.project.back_end.DTO.DoctorCreateDTO;
import com.project.back_end.DTO.DoctorDTO;
import com.project.back_end.DTO.DoctorProfileUpdateDTO;
import com.project.back_end.DTO.EmailLoginDTO;
import com.project.back_end.DTO.response.ApiDataResponseDTO;
import com.project.back_end.DTO.response.ApiResponseDTO;
import com.project.back_end.DTO.response.LoginResponseDTO;
import com.project.back_end.DTO.response.ResponseKeys;
import com.project.back_end.security.Role;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<ApiDataResponseDTO> getDoctor() {
        List<DoctorDTO> doctors = doctorService.getDoctors();

        return ResponseEntity.ok(ApiDataResponseDTO.of(ResponseKeys.DOCTORS, doctors));
    }

    @GetMapping("/filter/{name}/{time}/{specialty}")
    public ResponseEntity<ApiDataResponseDTO> filter(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String specialty) {
        List<DoctorDTO> doctors = service.filterDoctor(name, time, specialty);

        return ResponseEntity.ok(ApiDataResponseDTO.of(ResponseKeys.DOCTORS, doctors));
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

    @PutMapping("/{token}")
    public ResponseEntity<ApiResponseDTO> updateDoctor(
            @PathVariable String token,
            @Valid @RequestBody DoctorProfileUpdateDTO doctorToUpdate) {

        service.validateTokenOrThrow(token, Role.ADMIN);
        doctorService.updateDoctor(doctorToUpdate);

        return ResponseEntity.ok(ApiResponseDTO.success("Doctor updated successfully"));
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
