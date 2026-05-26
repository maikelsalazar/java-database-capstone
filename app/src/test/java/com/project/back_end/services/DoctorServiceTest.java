package com.project.back_end.services;

import com.project.back_end.DTO.DoctorsDTO;
import com.project.back_end.enums.AvailableTime;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class DoctorServiceTest {

    private static final String FULL_NAME = "Jane Doe";
    private static final String NAME = "Jane";
    private static final String SPECIALTY = "Cardiologist";
    private static final String AM = "AM";
    private static final String PM = "PM";

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private DoctorService doctorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldGetDoctors() {
        when(doctorRepository.findAll()).thenReturn(List.of(buildDoctor()));

        DoctorsDTO result = doctorService.getDoctors();

        assertEquals(1, result.getDoctors().size());
        verify(doctorRepository).findAll();
    }

    @Test
    void shouldFindDoctorsByName() {
        when(doctorRepository.findByName(NAME)).thenReturn(List.of(buildDoctor()));

        DoctorsDTO result = doctorService.findDoctorByName(NAME);

        assertEquals(1, result.getDoctors().size());
        assertEquals(FULL_NAME, result.getDoctors().get(0).getName());

        verify(doctorRepository).findByName(NAME);
    }

    @Test
    void shouldFindDoctorsBySpecialty() {
        when(doctorRepository.findBySpecialty(SPECIALTY)).thenReturn(List.of(buildDoctor()));

        DoctorsDTO result = doctorService.findDoctorBySpecialty(SPECIALTY);

        assertEquals(1, result.getDoctors().size());

        verify(doctorRepository).findBySpecialty(SPECIALTY);
    }

    @Test
    void shouldFindDoctorsByTime() {
        when(doctorRepository.findByTime(AM)).thenReturn(List.of(buildDoctor()));
        when(doctorRepository.findByTime(PM)).thenReturn(List.of(buildDoctor()));

        DoctorsDTO amResult = doctorService.findDoctorByTime(AM);
        DoctorsDTO pmResult = doctorService.findDoctorByTime(PM);

        assertEquals(1, amResult.getDoctors().size());
        assertEquals(List.of(AvailableTime.SLOT_09_10, AvailableTime.SLOT_10_11), amResult.getDoctors().get(0).getAvailableTimes());
        assertEquals(List.of(AvailableTime.SLOT_13_14), pmResult.getDoctors().get(0).getAvailableTimes());

        verify(doctorRepository).findByTime(AM);
        verify(doctorRepository).findByTime(PM);
    }



    @Test
    void shouldFilterDoctorByNameAndTime() {
        when(doctorRepository.findByNameAndTime(NAME, AM))
                .thenReturn(List.of(buildDoctor()));

        DoctorsDTO result = doctorService.filterDoctorByNameAndTime(NAME, AM);

        assertEquals(1, result.getDoctors().size());
        assertEquals(FULL_NAME, result.getDoctors().get(0).getName());
        assertEquals(List.of(AvailableTime.SLOT_09_10, AvailableTime.SLOT_10_11), result.getDoctors().get(0).getAvailableTimes());

        verify(doctorRepository).findByNameAndTime(NAME, AM);
    }

    @Test
    void shouldFilterDoctorByNameAndSpecialty() {
        when(doctorRepository.findByNameAndSpecialty(NAME, SPECIALTY))
                .thenReturn(List.of(buildDoctor()));

        DoctorsDTO result =
                doctorService.filterDoctorByNameAndSpecialty(NAME, SPECIALTY);

        assertEquals(1, result.getDoctors().size());
        assertEquals(FULL_NAME, result.getDoctors().get(0).getName());
        assertEquals(SPECIALTY, result.getDoctors().get(0).getSpecialty());

        verify(doctorRepository).findByNameAndSpecialty(NAME, SPECIALTY);
    }

    @Test
    void shouldFilterDoctorByTimeAndSpecialtyAM() {
        when(doctorRepository.findByTimeAndSpecialty(AM, SPECIALTY))
                .thenReturn(List.of(buildDoctor()));

        DoctorsDTO result =
                doctorService.filterDoctorByTimeAndSpecialty(AM, SPECIALTY);

        assertEquals(1, result.getDoctors().size());
        assertEquals(List.of(AvailableTime.SLOT_09_10, AvailableTime.SLOT_10_11), result.getDoctors().get(0).getAvailableTimes());
        assertEquals(SPECIALTY, result.getDoctors().get(0).getSpecialty());

        verify(doctorRepository).findByTimeAndSpecialty(AM, SPECIALTY);
    }

    @Test
    void shouldFilterDoctorByTimeAndSpecialtyPM() {
        when(doctorRepository.findByTimeAndSpecialty(PM, SPECIALTY))
                .thenReturn(List.of(buildDoctor()));

        DoctorsDTO result =
                doctorService.filterDoctorByTimeAndSpecialty(PM, SPECIALTY);

        assertEquals(1, result.getDoctors().size());
        assertEquals(List.of(AvailableTime.SLOT_13_14), result.getDoctors().get(0).getAvailableTimes());
        assertEquals(SPECIALTY, result.getDoctors().get(0).getSpecialty());

        verify(doctorRepository).findByTimeAndSpecialty(PM, SPECIALTY);
    }

    @Test
    void shouldFilterDoctorsByNameAndSpecialtyAndTime() {
        when(doctorRepository.findByNameAndTimeAndSpecialty(NAME, PM, SPECIALTY))
                .thenReturn(List.of(buildDoctor()));

        DoctorsDTO result =
                doctorService.filterDoctorsByNameAndSpecialtyAndTime(NAME, PM, SPECIALTY);

        assertEquals(1, result.getDoctors().size());
        assertEquals(FULL_NAME, result.getDoctors().get(0).getName());
        assertEquals(SPECIALTY, result.getDoctors().get(0).getSpecialty());
        assertEquals(List.of(AvailableTime.SLOT_13_14), result.getDoctors().get(0).getAvailableTimes());

        verify(doctorRepository).findByNameAndTimeAndSpecialty(NAME, PM, SPECIALTY);
    }



    @Test
    void shouldReturnEmptyListWhenNoDoctorsFoundByName() {
        when(doctorRepository.findByName("John"))
                .thenReturn(List.of());

        DoctorsDTO result =
                doctorService.findDoctorByName("John");

        assertEquals(0, result.getDoctors().size());

        verify(doctorRepository).findByName("John");
    }

    @Test
    void shouldReturnEmptyListWhenNoDoctorsFoundBySpecialty() {
        when(doctorRepository.findBySpecialty("Foo"))
                .thenReturn(List.of());

        DoctorsDTO result = doctorService.findDoctorByName("Foo");

        assertEquals(0, result.getDoctors().size());

        verify(doctorRepository).findByName("Foo");
    }

    @Test
    void shouldReturnEmptyOrSafeBehaviorOnInvalidDataFlow() {
        when(doctorRepository.findByTime("INVALID")).thenReturn(List.of());

        DoctorsDTO result = doctorService.findDoctorByTime("INVALID");

        assertEquals(0, result.getDoctors().size());
    }


    private static Doctor buildDoctor() {
        Doctor doctor = new Doctor();
        doctor.setName(FULL_NAME);
        doctor.setSpecialty("Cardiologist");
        doctor.setEmail("jane.doe@email.com");
        doctor.setPhone("5551012020");
        doctor.setPassword("hashed-password");
        doctor.setAvailableTimes(List.of(
                AvailableTime.SLOT_09_10,
                AvailableTime.SLOT_10_11,
                AvailableTime.SLOT_13_14
        ));

        return doctor;
    }
}
