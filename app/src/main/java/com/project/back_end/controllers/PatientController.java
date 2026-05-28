package com.project.back_end.controllers;

import com.project.back_end.DTO.PatientCreateDTO;
import com.project.back_end.services.PatientService;
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

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping("/")
    public ResponseEntity<Map<String, Object>> createPatient(@Valid @RequestBody PatientCreateDTO newPatient) {
        patientService.createPatient(newPatient);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Patient created successfully");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}


