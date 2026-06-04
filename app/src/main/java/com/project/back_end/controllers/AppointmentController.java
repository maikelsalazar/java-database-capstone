package com.project.back_end.controllers;

import com.project.back_end.DTO.AppointmentCreateDTO;
import com.project.back_end.DTO.AppointmentUpdateDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.security.Role;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private Service service;

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, Object>> bookAppointment(
            @PathVariable String token,
            @RequestBody AppointmentCreateDTO appointmentCreate
            ) {

        service.validateTokenOrThrow(token, Role.PATIENT);

        String email = service.extractEmailFromToken(token);

        appointmentService.createAppointment(appointmentCreate, email);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "appointment created successfully");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, Object>> updateAppointment(
            @PathVariable String token,
            @RequestBody AppointmentUpdateDTO appointmentUpdate
    ) {

        service.validateTokenOrThrow(token, Role.PATIENT);

        String email = service.extractEmailFromToken(token);

        appointmentService.updateAppointment(appointmentUpdate, email);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "appointment updated successfully");

        return ResponseEntity.ok(response);
    }



}
