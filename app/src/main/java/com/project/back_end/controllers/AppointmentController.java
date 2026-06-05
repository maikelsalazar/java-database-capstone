package com.project.back_end.controllers;

import com.project.back_end.DTO.*;
import com.project.back_end.security.Role;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("${api.path}appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private Service service;

    @PostMapping("/{token}")
    public ResponseEntity<ApiResponseDTO> bookAppointment(
            @PathVariable String token,
            @RequestBody AppointmentCreateDTO appointmentCreate
    ) {

        String email = getPatientEmailFromToken(token);

        appointmentService.createAppointment(appointmentCreate, email);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("Appointment created successfully"));
    }

    @PutMapping("/{token}")
    public ResponseEntity<ApiResponseDTO> updateAppointment(
            @PathVariable String token,
            @RequestBody AppointmentUpdateDTO appointmentUpdate
    ) {

        String email = getPatientEmailFromToken(token);

        appointmentService.updateAppointment(appointmentUpdate, email);

        return ResponseEntity.ok(ApiResponseDTO.success("Appointment updated successfully"));
    }

    @GetMapping("/{appointmentDate}/{token}")
    public ResponseEntity<DoctorAppointmentsResponseDTO> getAppointmentsByDate(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate appointmentDate,
            @PathVariable String token
    ) {
        String email = getDoctorEmailFromToken(token);

        List<DoctorAppointmentDTO> appointments = appointmentService
                .getAppointmentsByDate(appointmentDate, email);

        return ResponseEntity.ok(
                new DoctorAppointmentsResponseDTO(appointments)
        );
    }

    @GetMapping("/{appointmentDate}/search/{patientName}/{token}")
    public ResponseEntity<DoctorAppointmentsResponseDTO> getAppointmentsByDateAndPatientName(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate appointmentDate,
            @PathVariable String patientName,
            @PathVariable String token
    ) {
        String email = getDoctorEmailFromToken(token);

        List<DoctorAppointmentDTO> appointments = appointmentService
                .getAppointmentsByDateAndName(appointmentDate, patientName, email);

        return ResponseEntity.ok(
                new DoctorAppointmentsResponseDTO(appointments)
        );
    }

    private String getDoctorEmailFromToken(String token) {
        service.validateTokenOrThrow(token, Role.DOCTOR);

        return service.extractEmailFromToken(token);
    }

    private String getPatientEmailFromToken(String token) {
        service.validateTokenOrThrow(token, Role.PATIENT);

        return service.extractEmailFromToken(token);
    }
}
