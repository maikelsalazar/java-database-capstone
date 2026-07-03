package com.project.back_end.unit.dto;

import com.project.back_end.DTO.AppointmentDTO;
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
public class AppointmentDTOJsonTest {

    @Autowired
    JacksonTester<AppointmentDTO> json;

    @Test
    void appointmentSerializationTest() throws IOException {
        AppointmentDTO appointmentDTO = anAppointmentDTO();

        JsonContent<AppointmentDTO> content = json.write(appointmentDTO);

        assertThat(content).isEqualToJson("/dto/appointment.json");
        assertThat(content)
                .extractingJsonPathStringValue("$.appointmentTime")
                .isEqualTo("2025-05-10 10:15:00");

        assertThat(content)
                .extractingJsonPathStringValue("$.appointmentDate")
                .isEqualTo("2025-05-10");

        assertThat(content)
                .extractingJsonPathStringValue("$.appointmentTimeOnly")
                .isEqualTo("10:15");
    }

    @Test
    void appointmentDeserializationTest() throws IOException {
        String appointmentJson = """
                {
                    "id": 1,
                    "doctorId": 2,
                    "doctorName": "John Doe",
                    "patientId": 3,
                    "patientName": "Jane Doe",
                    "patientEmail": "jane.doe@email.com",
                    "patientPhone": "1234567890",
                    "patientAddress": "Av. 12, NY",
                    "appointmentTime": "2025-05-10 10:15:00",
                    "status": 1
                }
                """;

        AppointmentDTO expectedDTO = anAppointmentDTO();
        AppointmentDTO actualDTO = json.parseObject(appointmentJson);

        assertEquals(expectedDTO, actualDTO);
    }

    private static AppointmentDTO anAppointmentDTO() {
        return new AppointmentDTO(
                1L,
                2L,
                "John Doe",
                3L,
                "Jane Doe",
                "jane.doe@email.com",
                "1234567890",
                "Av. 12, NY",
                LocalDateTime.of(2025, 5, 10, 10, 15),
                1
        );
    }
}
