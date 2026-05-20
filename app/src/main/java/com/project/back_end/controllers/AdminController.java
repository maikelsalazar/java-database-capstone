
package com.project.back_end.controllers;

import com.project.back_end.DTO.AdminLoginDTO;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}admin")
public class AdminController {

    @Autowired
    private Service service;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> adminLogin(@Valid @RequestBody AdminLoginDTO adminRequested) {
        String token = service.validateAdmin(adminRequested);

        return handleResponse(token);
    }

    private ResponseEntity<Map<String, Object>> handleResponse(String token) {
        Map<String, Object> response = new HashMap<>();

        if (token == null) {
            response.put("success", false);
            response.put("message", "Invalid credentials");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        response.put("success", true);
        response.put("token", token);
        response.put("message", "Login successful");

        return ResponseEntity.ok(response);
    }
}
