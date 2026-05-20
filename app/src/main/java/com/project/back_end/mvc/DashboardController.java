package com.project.back_end.mvc;

import com.project.back_end.security.Role;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DashboardController {

    @Autowired
    private Service service;

    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        if (service.validateToken(token, Role.ADMIN)) {
            return "admin/adminDashboard";
        }

        return "redirect:/index.html";
    }
}
