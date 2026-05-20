package com.project.back_end.mvc;

import com.project.back_end.security.Role;
import com.project.back_end.services.Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Service service;

    @Test
    void shouldRenderAdminDashboardWhenTokenIsValid() throws Exception {

        when(service.validateToken("valid-token", Role.ADMIN))
                .thenReturn(true);

        mockMvc.perform(get("/adminDashboard/valid-token"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/adminDashboard"));
    }

    @Test
    void shouldRedirectWhenTokenIsInvalid() throws Exception {

        when(service.validateToken("invalid-token", Role.ADMIN))
                .thenReturn(false);

        mockMvc.perform(get("/adminDashboard/invalid-token"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index.html"));
    }
}
