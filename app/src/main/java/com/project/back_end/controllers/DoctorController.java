package com.project.back_end.controllers;

import com.project.back_end.DTO.DoctorDTO;
import com.project.back_end.DTO.DoctorLoginDTO;
import com.project.back_end.DTO.DoctorsDTO;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("${api.path}doctors")
public class DoctorController {

    private DoctorService doctorService;

    private Service service;

    public DoctorController(DoctorService doctorService, Service service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    @GetMapping("/list")
    public ResponseEntity<DoctorsDTO> getDoctor() {
        DoctorsDTO doctorsDTO = doctorService.getDoctors();

        return ResponseEntity.ok(doctorsDTO);
    }

    @GetMapping("/filter/{name}/{time}/{specialty}")
    public ResponseEntity<DoctorsDTO> filter(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String specialty) {
        DoctorsDTO doctorsDTO = service.filterDoctor(name, time, specialty);

        return ResponseEntity.ok(doctorsDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> doctorLogin(@Valid @RequestBody DoctorLoginDTO doctorLogin) {
        String token = service.validateDoctor(doctorLogin);
        Map<String, Object> content = new HashMap<>();
        if (token == null) {
            content.put("success", false);
            content.put("message","Invalid credentials");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(content);
        }

        content.put("success", true);
        content.put("message","Login successful");
        content.put("token", token);

        return ResponseEntity.ok(content);
    }
}
