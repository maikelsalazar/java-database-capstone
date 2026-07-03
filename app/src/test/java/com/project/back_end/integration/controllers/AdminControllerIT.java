package com.project.back_end.integration.controllers;

import com.project.back_end.integration.shared.IntegrationTest;
import com.project.back_end.models.Admin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdminControllerIT extends IntegrationTest {

    private static final String LOGIN_URI = "/api/admin/login";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldReturnJwtTokenOnAdminLogin() throws Exception {
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword(
                passwordEncoder.encode("admin@1234")
        );

        adminRepository.save(admin);

        String credentials = """
                {
                     "username": "admin",
                     "password": "admin@1234"
                }
                """;

        mockMvc.perform(post(LOGIN_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentials)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").isBoolean())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.message")
                        .value("Login successful"));
    }

    @Test
    void shouldReturnUnauthorizedOnInvalidCredentials() throws Exception {
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword(
                passwordEncoder.encode("admin@1234")
        );

        adminRepository.save(admin);

        String credentials = """
                {
                     "username": "admin",
                     "password": "admin@1235"
                }
                """;

        mockMvc.perform(post(LOGIN_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentials)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").isBoolean())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.message")
                        .value("Invalid credentials"));
    }

    @Test
    void shouldReturnUnauthorizedWhenUsernameDoesNotExist() throws Exception {
        String credentials = """
                {
                     "username": "non-existing-user",
                     "password": "admin@1235"
                }
                """;

        mockMvc.perform(post(LOGIN_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentials)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").isBoolean())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.message")
                        .value("Invalid credentials"));
    }

    @Test
    void shouldReturnBadRequestOnInvalidBody() throws Exception {
        String credentials = """
                {
                     "username": "",
                     "password": ""
                }
                """;

        mockMvc.perform(post(LOGIN_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentials)
                )
                .andExpect(status().isBadRequest());
    }
}
