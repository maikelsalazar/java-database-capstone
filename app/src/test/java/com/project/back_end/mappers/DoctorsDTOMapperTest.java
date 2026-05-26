package com.project.back_end.mappers;

import com.project.back_end.DTO.DoctorDTO;
import com.project.back_end.DTO.DoctorsDTO;
import com.project.back_end.enums.AvailableTime;
import com.project.back_end.models.Doctor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DoctorsDTOMapperTest {

    @Test
    void shouldReturnDoctorListWithAvailableTimeSlotsOnlyInAMPeriod() {

        List<Doctor> doctorList = buildDoctor();

        DoctorDTO expectedDoctorDTO = new DoctorDTO();
        expectedDoctorDTO.setName("Jonh Doe");
        expectedDoctorDTO.setSpecialty("Cardiologist");
        expectedDoctorDTO.setEmail("jane.doe@email.com");
        expectedDoctorDTO.setPhone("5551012020");
        expectedDoctorDTO.setAvailableTimes(List.of(
                AvailableTime.SLOT_09_10,
                AvailableTime.SLOT_10_11
        ));


        DoctorsDTO expectedDTO = new DoctorsDTO(List.of(expectedDoctorDTO));
        DoctorsDTO actualDTO = DoctorsDTOMapper.fromDoctorList(doctorList, "AM");

        assertEquals(expectedDTO.getDoctors().get(0).getAvailableTimes(), actualDTO.getDoctors().get(0).getAvailableTimes());

    }

    private static List<Doctor> buildDoctor() {
        Doctor doctor = new Doctor();
        doctor.setName("John Doe");
        doctor.setSpecialty("Cardiologist");
        doctor.setEmail("jane.doe@email.com");
        doctor.setPhone("5551012020");
        doctor.setPassword("hashed-password");
        doctor.setAvailableTimes(List.of(
                AvailableTime.SLOT_09_10,
                AvailableTime.SLOT_10_11,
                AvailableTime.SLOT_13_14
        ));

        return List.of(doctor);
    }
}
