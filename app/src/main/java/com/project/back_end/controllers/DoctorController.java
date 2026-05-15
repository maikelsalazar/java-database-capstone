package com.project.back_end.controllers;

import com.project.back_end.DTO.DoctorsDTO;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
