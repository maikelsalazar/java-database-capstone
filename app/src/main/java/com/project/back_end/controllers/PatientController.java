package com.project.back_end.controllers;

import com.project.back_end.DTO.*;
import com.project.back_end.security.Role;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Map<String, Object>> createPatient(@Valid @RequestBody PatientCreateDTO newPatient) {
        patientService.createPatient(newPatient);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Patient created successfully");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
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
    public ResponseEntity<Map<String, PatientDTO>> getPatient(@PathVariable String token) {
        service.validateTokenOrThrow(token, Role.PATIENT);

        String email = service.extractEmailFromToken(token);

        PatientDTO patient = patientService.getPatient(email);

        Map<String, PatientDTO> response = new HashMap<>();
        response.put("patient", patient);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/patient/{token}")
    public ResponseEntity<Map<String, Object>> getPatientAppointment(
            @PathVariable Long id,
            @PathVariable String token) {

        service.validateTokenOrThrow(token, Role.PATIENT);

        String email = service.extractEmailFromToken(token);

        patientService.validateOwnershipOrThrow(id, email);

        List<AppointmentDTO> patientAppointments =  patientService.getPatientAppointments(id);

        Map<String, Object> response = new HashMap<>();
        response.put("appointments", patientAppointments);

        return ResponseEntity.ok(response);
    }
}


