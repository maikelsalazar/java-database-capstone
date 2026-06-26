package com.project.back_end.controllers;

import com.project.back_end.DTO.ApiResponseDTO;
import com.project.back_end.DTO.PrescriptionCreateDTO;
import com.project.back_end.DTO.PrescriptionListDTO;
import com.project.back_end.security.Role;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    @Autowired
    private Service service;

    @Autowired
    private PrescriptionService prescriptionService;

    @PostMapping("/{token}")
    public ResponseEntity<ApiResponseDTO> savePrescription(
            @PathVariable String token,
            @Valid @RequestBody PrescriptionCreateDTO prescriptionRequest) {

        String email = service.validateAndGetDoctorEmailFromToken(token);

        prescriptionService.savePrescription(prescriptionRequest, email);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("Prescription created successfully"));
    }


    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<PrescriptionListDTO> getPrescription(
            @PathVariable Long appointmentId,
            @PathVariable String token) {

        String email = service.validateAndGetDoctorEmailFromToken(token);

        return ResponseEntity.ok(prescriptionService.getPrescription(appointmentId, email));
    }
}
