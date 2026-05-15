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
        doctorRepository.deleteAllInBatch();
    }

    @Test
    void shouldReturnEmptyDoctorListWhenThereIsNoDoctor() throws Exception{
        mockMvc.perform(get("/api/doctors/list"))
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

        mockMvc.perform(get("/api/doctors/list"))
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

    @Test
    void shouldReturnFilteredDoctorsByNameOnly() throws Exception {
        Doctor doctorAM = new Doctor();
        doctorAM.setName("Jane Doe");
        doctorAM.setSpecialty("Cardiologist");
        doctorAM.setEmail("jane.doe@email.com");
        doctorAM.setPhone("5551012020");
        doctorAM.setPassword("hashed-password");
        doctorAM.setAvailableTimes(List.of("09:00-10:00", "10:00-11:00"));

        Doctor doctorPM = new Doctor();
        doctorPM.setName("John Doe");
        doctorPM.setSpecialty("Cardiologist");
        doctorPM.setEmail("john.doe@email.com");
        doctorPM.setPhone("5551012020");
        doctorPM.setPassword("hashed-password");
        doctorPM.setAvailableTimes(List.of("13:00-14:00", "14:00-15:00"));

        Doctor savedDoctorNamed = doctorRepository.save(doctorAM);
        doctorRepository.save(doctorPM);

        mockMvc.perform(get("/api/doctors/filter/jane/*/*"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctors").isArray())
                .andExpect(jsonPath("$.doctors.length()").value(1))
                .andExpect(jsonPath("$.doctors[0].id").value(savedDoctorNamed.getId()))
                .andExpect(jsonPath("$.doctors[0].name").value("Jane Doe"))
                .andExpect(jsonPath("$.doctors[0].email").value("jane.doe@email.com"))
                .andExpect(jsonPath("$.doctors[0].phone").value("5551012020"))
                .andExpect(jsonPath("$.doctors[0].password").doesNotExist())
                .andExpect(jsonPath("$.doctors[0].availableTimes.length()").value(2))
                .andExpect(jsonPath("$.doctors[0].availableTimes[0]").value("09:00-10:00"));
    }

    @Test
    void shouldReturnFilteredDoctorsByMorningSlotsOnly() throws Exception {
        Doctor doctorAM = new Doctor();
        doctorAM.setName("Jane Doe");
        doctorAM.setSpecialty("Cardiologist");
        doctorAM.setEmail("jane.doe@email.com");
        doctorAM.setPhone("5551012020");
        doctorAM.setPassword("hashed-password");
        doctorAM.setAvailableTimes(List.of("09:00-10:00", "10:00-11:00"));

        Doctor doctorPM = new Doctor();
        doctorPM.setName("John Doe");
        doctorPM.setSpecialty("Cardiologist");
        doctorPM.setEmail("john.doe@email.com");
        doctorPM.setPhone("5551012020");
        doctorPM.setPassword("hashed-password");
        doctorPM.setAvailableTimes(List.of("13:00-14:00", "14:00-15:00"));

        Doctor savedDoctorAM = doctorRepository.save(doctorAM);
        doctorRepository.save(doctorPM);

        mockMvc.perform(get("/api/doctors/filter/*/am/*"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctors").isArray())
                .andExpect(jsonPath("$.doctors.length()").value(1))
                .andExpect(jsonPath("$.doctors[0].id").value(savedDoctorAM.getId()))
                .andExpect(jsonPath("$.doctors[0].name").value("Jane Doe"))
                .andExpect(jsonPath("$.doctors[0].email").value("jane.doe@email.com"))
                .andExpect(jsonPath("$.doctors[0].phone").value("5551012020"))
                .andExpect(jsonPath("$.doctors[0].password").doesNotExist())
                .andExpect(jsonPath("$.doctors[0].availableTimes.length()").value(2))
                .andExpect(jsonPath("$.doctors[0].availableTimes[0]").value("09:00-10:00"));
    }

    @Test
    void shouldReturnFilteredDoctorsByAfternoonSlotsOnly() throws Exception {
        Doctor doctorAM = new Doctor();
        doctorAM.setName("Jane Doe");
        doctorAM.setSpecialty("Cardiologist");
        doctorAM.setEmail("jane.doe@email.com");
        doctorAM.setPhone("5551012020");
        doctorAM.setPassword("hashed-password");
        doctorAM.setAvailableTimes(List.of("09:00-10:00", "10:00-11:00"));

        Doctor doctorPM = new Doctor();
        doctorPM.setName("John Doe");
        doctorPM.setSpecialty("Cardiologist");
        doctorPM.setEmail("john.doe@email.com");
        doctorPM.setPhone("5551012020");
        doctorPM.setPassword("hashed-password");
        doctorPM.setAvailableTimes(List.of("13:00-14:00", "14:00-15:00"));

        doctorRepository.save(doctorAM);
        Doctor savedDoctorPM = doctorRepository.save(doctorPM);

        mockMvc.perform(get("/api/doctors/filter/*/pm/*"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctors").isArray())
                .andExpect(jsonPath("$.doctors.length()").value(1))
                .andExpect(jsonPath("$.doctors[0].id").value(savedDoctorPM.getId()))
                .andExpect(jsonPath("$.doctors[0].name").value("John Doe"))
                .andExpect(jsonPath("$.doctors[0].email").value("john.doe@email.com"))
                .andExpect(jsonPath("$.doctors[0].phone").value("5551012020"))
                .andExpect(jsonPath("$.doctors[0].password").doesNotExist())
                .andExpect(jsonPath("$.doctors[0].availableTimes.length()").value(2))
                .andExpect(jsonPath("$.doctors[0].availableTimes[0]").value("13:00-14:00"));
    }

    @Test
    void shouldReturnFilteredDoctorsBySpecialtyOnly() throws Exception {
        Doctor doctorAM = new Doctor();
        doctorAM.setName("Jane Doe");
        doctorAM.setSpecialty("Neurologist");
        doctorAM.setEmail("jane.doe@email.com");
        doctorAM.setPhone("5551012020");
        doctorAM.setPassword("hashed-password");
        doctorAM.setAvailableTimes(List.of("09:00-10:00", "10:00-11:00"));

        Doctor doctorSpecialty = new Doctor();
        doctorSpecialty.setName("John Doe");
        doctorSpecialty.setSpecialty("Cardiologist");
        doctorSpecialty.setEmail("john.doe@email.com");
        doctorSpecialty.setPhone("5551012020");
        doctorSpecialty.setPassword("hashed-password");
        doctorSpecialty.setAvailableTimes(List.of("13:00-14:00", "14:00-15:00"));

        doctorRepository.save(doctorAM);
        Doctor savedDoctorSpecialty = doctorRepository.save(doctorSpecialty);

        mockMvc.perform(get("/api/doctors/filter/*/*/cardiologist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctors").isArray())
                .andExpect(jsonPath("$.doctors.length()").value(1))
                .andExpect(jsonPath("$.doctors[0].id").value(savedDoctorSpecialty.getId()))
                .andExpect(jsonPath("$.doctors[0].name").value("John Doe"))
                .andExpect(jsonPath("$.doctors[0].email").value("john.doe@email.com"))
                .andExpect(jsonPath("$.doctors[0].phone").value("5551012020"))
                .andExpect(jsonPath("$.doctors[0].password").doesNotExist())
                .andExpect(jsonPath("$.doctors[0].availableTimes.length()").value(2))
                .andExpect(jsonPath("$.doctors[0].availableTimes[0]").value("13:00-14:00"));
    }
}
