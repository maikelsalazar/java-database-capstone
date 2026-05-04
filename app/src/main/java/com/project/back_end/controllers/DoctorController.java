package com.project.back_end.controllers;

import com.project.back_end.DTO.DoctorsDTO;
import com.project.back_end.services.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/list")
    public ResponseEntity<DoctorsDTO> getDoctor() {
        DoctorsDTO doctorsDTO = doctorService.findAllDoctor();

        return ResponseEntity.ok(doctorsDTO);
    }
}
