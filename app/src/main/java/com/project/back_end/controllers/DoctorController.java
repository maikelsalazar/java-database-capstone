package com.project.back_end.controllers;

import com.project.back_end.DTO.DoctorCreateDTO;
import com.project.back_end.DTO.DoctorLoginDTO;
import com.project.back_end.DTO.DoctorsDTO;
import com.project.back_end.security.Role;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    private final DoctorService doctorService;

    private final Service service;

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

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, Object>> saveDoctor(@PathVariable String token, @Valid @RequestBody DoctorCreateDTO newDoctorRequest) {

        service.validateTokenOrThrow(token, Role.ADMIN);

        doctorService.saveDoctor(newDoctorRequest);

        Map<String, Object> content = new HashMap<>();
        content.put("message", "Doctor added successfully");

        return ResponseEntity.ok(content);
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, Object>> deleteDoctor(
            @PathVariable Long id,
            @PathVariable String token
    ) {

        service.validateTokenOrThrow(token, Role.ADMIN);
        doctorService.deleteDoctor(id);

        return ResponseEntity.noContent().build();
    }
}
