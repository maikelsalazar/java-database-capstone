package com.project.back_end.controllers;

import com.project.back_end.DTO.AppointmentCreateDTO;
import com.project.back_end.DTO.AppointmentUpdateDTO;
import com.project.back_end.DTO.DoctorAppointmentDTO;
import com.project.back_end.DTO.response.ApiDataResponseDTO;
import com.project.back_end.DTO.response.ApiResponseDTO;
import com.project.back_end.DTO.response.ResponseKeys;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
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
            @RequestBody @Valid  AppointmentCreateDTO appointmentCreate
    ) {

        String email = service.validateAndGetPatientEmailFromToken(token);

        appointmentService.createAppointment(appointmentCreate, email);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("Appointment created successfully"));
    }

    @PutMapping("/{token}")
    public ResponseEntity<ApiResponseDTO> updateAppointment(
            @PathVariable String token,
            @RequestBody @Valid AppointmentUpdateDTO appointmentUpdate
    ) {

        String email = service.validateAndGetPatientEmailFromToken(token);

        appointmentService.updateAppointment(appointmentUpdate, email);

        return ResponseEntity.ok(ApiResponseDTO.success("Appointment updated successfully"));
    }

    @GetMapping("/{appointmentDate}/{token}")
    public ResponseEntity<ApiDataResponseDTO> getAppointmentsByDate(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate appointmentDate,
            @PathVariable String token
    ) {
        String email = service.validateAndGetDoctorEmailFromToken(token);

        List<DoctorAppointmentDTO> appointments = appointmentService
                .getAppointmentsByDate(appointmentDate, email);

        return ResponseEntity.ok(
                ApiDataResponseDTO.of(ResponseKeys.APPOINTMENTS, appointments)
        );
    }

    @GetMapping("/{appointmentDate}/search/{patientName}/{token}")
    public ResponseEntity<ApiDataResponseDTO> getAppointmentsByDateAndPatientName(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate appointmentDate,
            @PathVariable String patientName,
            @PathVariable String token
    ) {
        String email = service.validateAndGetDoctorEmailFromToken(token);

        List<DoctorAppointmentDTO> appointments = appointmentService
                .getAppointmentsByDateAndName(appointmentDate, patientName, email);

        return ResponseEntity.ok(
                ApiDataResponseDTO.of(ResponseKeys.APPOINTMENTS, appointments)
        );
    }
}
