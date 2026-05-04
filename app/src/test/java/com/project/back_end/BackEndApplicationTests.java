package com.project.back_end;

import com.project.back_end.models.Doctor;
import com.project.back_end.repo.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class BackEndApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DoctorRepository doctorRepository;

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("cms")
            .withUsername("root")
            .withPassword("password");

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);

        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @BeforeEach
    void cleanDatabase() {
        doctorRepository.deleteAll();
    }

    @Test
    void shouldReturnEmptyDoctorListWhenThereIsNoDoctor() throws Exception{
        mockMvc.perform(get("/api/doctor/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctors").isArray())
                .andExpect(jsonPath("$.doctors.length()").value(0));
    }

	@Test
    void shouldReturnAllDoctorsWhenDataIsSaved() throws Exception {
        Doctor doctor = new Doctor();
        doctor.setName("Jane Doe");
        doctor.setSpecialty("Cardiologist");
        doctor.setEmail("jane.doe@email.com");
        doctor.setPhone("5551012020");
        doctor.setPassword("hashed-password");
        doctor.setAvailableTimes(List.of("09:00-10:00", "10:00-11:00"));

        Doctor savedDoctor = doctorRepository.save(doctor);

        mockMvc.perform(get("/api/doctor/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctors").isArray())
                .andExpect(jsonPath("$.doctors.length()").value(1))
                .andExpect(jsonPath("$.doctors[0].id").value(savedDoctor.getId()))
                .andExpect(jsonPath("$.doctors[0].name").value("Jane Doe"))
                .andExpect(jsonPath("$.doctors[0].email").value("jane.doe@email.com"))
                .andExpect(jsonPath("$.doctors[0].phone").value("5551012020"))
                .andExpect(jsonPath("$.doctors[0].password").doesNotExist())
                .andExpect(jsonPath("$.doctors[0].availableTimes.length()").value(2));
    }
}
