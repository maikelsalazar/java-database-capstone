package com.project.back_end.unit.dto;

import com.project.back_end.DTO.AppointmentCreateDTO;
import com.project.back_end.DTO.DoctorIdDTO;
import com.project.back_end.DTO.PatientIdDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class AppointmentCreateDTOJsonTest {

    @Autowired
    JacksonTester<AppointmentCreateDTO> json;

    @Test
    void appointmentCreateDTOSerialization() throws IOException {
        AppointmentCreateDTO dto = anAppointmentCreateDTO();

        JsonContent<AppointmentCreateDTO> content = json.write(dto);

        assertThat(content).isEqualToJson("/dto/appointment_create_dto.json");
        assertThat(content)
                .extractingJsonPathStringValue("$.appointmentTime")
                .isEqualTo("2026-06-03T10:15:00");
        assertThat(content)
                .extractingJsonPathNumberValue("$.doctor.id")
                .isEqualTo(1);
        assertThat(content)
                .extractingJsonPathNumberValue("$.patient.id")
                .isEqualTo(2);
        assertThat(content)
                .extractingJsonPathNumberValue("$.status")
                .isEqualTo(0);
    }

    @Test
    void appointmentCreateDTODeserialization() throws Exception {
        AppointmentCreateDTO expected = anAppointmentCreateDTO();
        AppointmentCreateDTO actual = json.readObject("/dto/appointment_create_dto.json");

        assertEquals(expected, actual);
    }

    @Test
    void shouldNormalizeAppointmentTimeToMinutePrecision() {
        AppointmentCreateDTO dto = new AppointmentCreateDTO(
                new DoctorIdDTO(1L),
                new PatientIdDTO(2L),
                LocalDateTime.of(2026, 6, 3, 10, 15, 45),
                0
        );

        assertEquals(
                LocalDateTime.of(2026, 6, 3, 10, 15),
                dto.appointmentTime()
        );
    }

    private AppointmentCreateDTO anAppointmentCreateDTO() {
        return new AppointmentCreateDTO(
                new DoctorIdDTO(1L),
                new PatientIdDTO(2L),
                LocalDateTime.of(2026, 6, 3, 10, 15).withSecond(10),
                0
        );
    }
}
