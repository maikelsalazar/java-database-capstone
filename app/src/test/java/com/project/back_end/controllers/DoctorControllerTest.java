package com.project.back_end.controllers;

import com.project.back_end.DTO.DoctorDTO;
import com.project.back_end.DTO.DoctorsDTO;
import com.project.back_end.services.DoctorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DoctorController.class)
@TestPropertySource(properties = "api.path=/api/")
public class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DoctorService doctorService;

    @Test
    void shouldReturnListOfDoctors() throws Exception {

        List<DoctorDTO> doctorList = buildDoctors();

        DoctorsDTO mockResponse = new DoctorsDTO(doctorList);

        when(doctorService.findAllDoctor()).thenReturn(mockResponse);

        mockMvc.perform(get("/api/doctor/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctors").isArray())
                .andExpect(jsonPath("$.doctors[0].id").value(1))
                .andExpect(jsonPath("$.doctors[0].name").value("Jane Doe"))
                .andExpect(jsonPath("$.doctors[0].specialty").value("Cardiologist"))
                .andExpect(jsonPath("$.doctors[0].email").value("jane.doe@email.com"))
                .andExpect(jsonPath("$.doctors[0].phone").value("555-101-2020"))
                .andExpect(jsonPath("$.doctors[0].availableTimes[0]").value("09:00-10:00"))
                .andExpect(jsonPath("$.doctors[0].availableTimes[1]").value("10:00-11:00"))
                .andExpect(jsonPath("$.doctors[1].id").value(2))
                .andExpect(jsonPath("$.doctors[1].name").value("John Doe"))
                .andExpect(jsonPath("$.doctors[1].specialty").value("Neurologist"))
                .andExpect(jsonPath("$.doctors[1].email").value("john.doe@email.com"))
                .andExpect(jsonPath("$.doctors[1].phone").value("444-101-2020"))
                .andExpect(jsonPath("$.doctors[1].availableTimes").isArray())
                .andExpect(jsonPath("$.doctors[1].availableTimes.length()").value(0));

        verify(doctorService).findAllDoctor();
        verifyNoMoreInteractions(doctorService);
    }

    @Test
    void shouldReturnEmptyList() throws Exception{
        when(doctorService.findAllDoctor()).thenReturn(new DoctorsDTO(List.of()));

        mockMvc.perform(get("/api/doctor/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctors").isArray())
                .andExpect(jsonPath("$.doctors.length()").value(0));

        verify(doctorService).findAllDoctor();
        verifyNoMoreInteractions(doctorService);
    }

    private List<DoctorDTO> buildDoctors() {
        return List.of(
                new DoctorDTO(1L, "Jane Doe", "Cardiologist", "jane.doe@email.com", "555-101-2020", List.of("09:00-10:00", "10:00-11:00")),
                new DoctorDTO(2L, "John Doe", "Neurologist", "john.doe@email.com", "444-101-2020", List.of())
        );
    }
}
