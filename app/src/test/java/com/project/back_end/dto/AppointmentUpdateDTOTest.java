package com.project.back_end.dto;

import com.project.back_end.DTO.AppointmentUpdateDTO;
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
public class AppointmentUpdateDTOTest {

    @Autowired
    private JacksonTester<AppointmentUpdateDTO> json;

    @Test
    void appointmentUpdateDTODSerializationTest() throws IOException {
        AppointmentUpdateDTO appointmentUpdateDTO = anAppointmentUpdateDTO();

        JsonContent<AppointmentUpdateDTO> content = json.write(appointmentUpdateDTO);

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

        AppointmentUpdateDTO expected = anAppointmentUpdateDTO();
        AppointmentUpdateDTO actual = json.parseObject(appointmentUpdateJson);

        assertThat(expected).isEqualTo(actual);

    }

    private static AppointmentUpdateDTO anAppointmentUpdateDTO() {
        return new AppointmentUpdateDTO(1L,
                new DoctorIdDTO(2L),
                new PatientIdDTO(3L),
                LocalDateTime.of(2026, 2, 1, 10, 0),
                0
        );
    }
}
