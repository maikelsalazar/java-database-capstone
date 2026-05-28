package com.project.back_end.controllers;

import com.project.back_end.DTO.PatientCreateDTO;
import com.project.back_end.DTO.PatientLoginDTO;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
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
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody PatientLoginDTO patientLoginDTO) {
        String token = service.validatePatient(patientLoginDTO);

        Map<String, Object> body = new HashMap<>();
        if (token == null) {
            body.put("success", false);
            body.put("message", "Invalid credentials");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(body);
        }

        body.put("success", true);
        body.put("message", "Login successful");
        body.put("token", token);

        return ResponseEntity
                .ok(body);
    }
}


