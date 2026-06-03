package com.project.back_end.dto;

import com.project.back_end.DTO.AppointmentRequestDTO;
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

@JsonTest
public class AppointmentRequestDTOTest {

    @Autowired
    private JacksonTester<AppointmentRequestDTO> json;

    @Test
    void appointmentUpdateDTODSerializationTest() throws IOException {
        AppointmentRequestDTO appointmentRequestDTO = anAppointmentRequestDTO();

        JsonContent<AppointmentRequestDTO> content = json.write(appointmentRequestDTO);

        assertThat(content).isEqualToJson("/dto/appointment_update.json");
    }

    @Test
    void appointmentUpdateDTODeserializationTest() throws IOException {
        String appointmentUpdateJson = """
                {
                    "id": 1,
                    "doctor": {
                        "id": 2
                    },
                    "patient": {
                        "id": 3
                    },
                    "appointmentTime": "2026-02-01T10:00:00",
                    "status": 0
                }
                """;

        AppointmentRequestDTO expected = anAppointmentRequestDTO();
        AppointmentRequestDTO actual = json.parseObject(appointmentUpdateJson);

        assertThat(expected).isEqualTo(actual);

    }

    private static AppointmentRequestDTO anAppointmentRequestDTO() {
        return new AppointmentRequestDTO(1L,
                new DoctorIdDTO(2L),
                new PatientIdDTO(3L),
                LocalDateTime.of(2026, 2, 1, 10, 0),
                0
        );
    }
}
