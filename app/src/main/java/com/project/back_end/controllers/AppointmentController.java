package com.project.back_end.controllers;

import com.project.back_end.DTO.AppointmentRequestDTO;
import com.project.back_end.security.Role;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, Object>> updateAppointment(
            @PathVariable String token,
            @RequestBody AppointmentRequestDTO appointmentUpdate
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
