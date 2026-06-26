package com.project.back_end.controllers;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.DTO.EmailLoginDTO;
import com.project.back_end.DTO.PatientCreateDTO;
import com.project.back_end.DTO.PatientDTO;
import com.project.back_end.DTO.response.ApiDataResponseDTO;
import com.project.back_end.DTO.response.ApiResponseDTO;
import com.project.back_end.DTO.response.LoginResponseDTO;
import com.project.back_end.DTO.response.ResponseKeys;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.path}patient")
public class PatientController {

    private final PatientService patientService;
    private final Service service;

    public PatientController(PatientService patientService, Service service) {
        this.patientService = patientService;
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO> createPatient(@Valid @RequestBody PatientCreateDTO newPatient) {
        patientService.createPatient(newPatient);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("Patient created successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody EmailLoginDTO loginRequest) {
        String token = service.validatePatient(loginRequest);

        if (token == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(LoginResponseDTO.failure());
        }

        return ResponseEntity
                .ok(LoginResponseDTO.success(token));
    }

    @GetMapping("/{token}")
    public ResponseEntity<ApiDataResponseDTO> getPatient(@PathVariable String token) {
        String email = service.validateAndGetPatientEmailFromToken(token);

        PatientDTO patient = patientService.getPatient(email);

        return ResponseEntity.ok(
                ApiDataResponseDTO.of(ResponseKeys.PATIENT, patient)
        );
    }

    @GetMapping("/{id}/patient/{token}")
    public ResponseEntity<ApiDataResponseDTO> getPatientAppointment(
            @PathVariable Long id,
            @PathVariable String token) {

        String email = service.validateAndGetPatientEmailFromToken(token);

        patientService.validateOwnershipOrThrow(id, email);

        List<AppointmentDTO> patientAppointments = patientService.getPatientAppointments(id);

        return ResponseEntity.ok(
                ApiDataResponseDTO.of(ResponseKeys.APPOINTMENTS, patientAppointments)
        );
    }
}
