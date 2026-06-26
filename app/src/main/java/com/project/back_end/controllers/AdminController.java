
package com.project.back_end.controllers;

import com.project.back_end.DTO.AdminLoginDTO;
import com.project.back_end.DTO.response.LoginResponseDTO;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.path}admin")
public class AdminController {

    @Autowired
    private Service service;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> adminLogin(@Valid @RequestBody AdminLoginDTO loginRequest) {
        String token = service.validateAdmin(loginRequest);

        if (token == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(LoginResponseDTO.failure());
        }

        return ResponseEntity.ok(LoginResponseDTO.success(token));
    }
}
