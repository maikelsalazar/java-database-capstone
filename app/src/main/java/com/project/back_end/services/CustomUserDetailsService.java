package com.project.back_end.services;

import com.project.back_end.models.Admin;
import com.project.back_end.security.Role;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService {

    public UserDetails buildUser(Admin admin) {
        return User.builder()
                .username(admin.getUsername())
                .password(admin.getPassword())
                .roles(Role.ADMIN)
                .build();
    }
}
