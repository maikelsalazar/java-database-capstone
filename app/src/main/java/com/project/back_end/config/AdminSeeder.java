package com.project.back_end.config;

import com.project.back_end.models.Admin;
import com.project.back_end.repo.AdminRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("!test")
public class AdminSeeder {

    @Value("${admin.seed.username}")
    private String username;

    @Value("${admin.seed.password}")
    private String password;

    @Bean
    @ConditionalOnProperty(
            name = "admin.seed.enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    CommandLineRunner seedAdmin(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (adminRepository.existsByUsername(username)) {
                System.out.println("admin already exists");
                return;
            }

            Admin admin = new Admin(
                    username,
                    passwordEncoder.encode(password)
            );

            adminRepository.save(admin);
            System.out.println("Admin created");
        };
    }
}
