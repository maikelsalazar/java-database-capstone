package com.project.back_end.dto;

import com.project.back_end.DTO.DoctorDTO;
import com.project.back_end.DTO.DoctorsDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class DoctorsDTOJsonTest {

    @Autowired
    private JacksonTester<DoctorsDTO> json;

    @Test
    void doctorsDTOSerializationTest() throws IOException {
        DoctorsDTO doctorsDTO = new DoctorsDTO(
                List.of(
                        new DoctorDTO(1L, "Jane Doe", "Cardiologist", "jane.doe@email.com", "555-101-2020", List.of("09:00-10:00", "10:00-11:00")),
                        new DoctorDTO(2L, "John Doe", "Neurologist", "john.doe@email.com", "444-101-2020", List.of())
                )
        );

        assertThat(json.write(doctorsDTO)).isEqualToJson("/dto/doctors.json");
    }

    @Test
    void doctorsDTODeserializationTest() throws IOException {
        String expected = """
                {
                  "doctors": [
                    {
                      "id": 1,
                      "name": "Jane Doe",
                      "specialty": "Cardiologist",
                      "email": "jane.doe@email.com",
                      "phone": "555-101-2020",
                      "availableTimes": ["09:00-10:00", "10:00-11:00"]
                    }
                  ]
                }
                """;

        DoctorsDTO result = json.parseObject(expected);

        assertThat(result.getDoctors()).hasSize(1);
        assertThat(result.getDoctors().get(0).getId()).isEqualTo(1);
        assertThat(result.getDoctors().get(0).getName()).isEqualTo("Jane Doe");
        assertThat(result.getDoctors().get(0).getSpecialty()).isEqualTo("Cardiologist");
        assertThat(result.getDoctors().get(0).getEmail()).isEqualTo("jane.doe@email.com");
        assertThat(result.getDoctors().get(0).getPhone()).isEqualTo("555-101-2020");
        assertThat(result.getDoctors().get(0).getAvailableTimes().get(0)).isEqualTo("09:00-10:00");
    }
}
