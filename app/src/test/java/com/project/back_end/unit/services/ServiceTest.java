package com.project.back_end.unit.services;

import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import com.project.back_end.services.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;

public class ServiceTest {

    private static final String FILTER_NAME = "Jane";
    private static final String FILTER_TIME = "AM";
    private static final String FILTER_SPECIALTY = "Cardiologist";

    @Mock
    private DoctorService doctorService;

    @Mock
    TokenService tokenService;

    @InjectMocks
    private Service service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRouteToFindByName() {
        when(doctorService.findDoctorByName(FILTER_NAME))
                .thenReturn(List.of());

        service.filterDoctor(FILTER_NAME, "", "");

        verify(doctorService).findDoctorByName(FILTER_NAME);
        verifyNoMoreInteractions(doctorService);
    }

    @Test
    void shouldRouteToFindByTime() {
        when(doctorService.findDoctorByTime(FILTER_TIME))
                .thenReturn(List.of());

        service.filterDoctor("", FILTER_TIME, "");

        verify(doctorService).findDoctorByTime(FILTER_TIME);
        verifyNoMoreInteractions(doctorService);
    }

    @Test
    void shouldRouteToFindBySpecialty() {
        when(doctorService.findDoctorBySpecialty(FILTER_SPECIALTY))
                .thenReturn(List.of());

        service.filterDoctor("", "", FILTER_SPECIALTY);

        verify(doctorService).findDoctorBySpecialty(FILTER_SPECIALTY);
        verifyNoMoreInteractions(doctorService);
    }

    @Test
    void shouldRouteToFilterByNameAndTime() {
        when(doctorService.filterDoctorByNameAndTime(FILTER_NAME, FILTER_TIME))
                .thenReturn(List.of());

        service.filterDoctor(FILTER_NAME, FILTER_TIME, "");

        verify(doctorService).filterDoctorByNameAndTime(FILTER_NAME, FILTER_TIME);
        verifyNoMoreInteractions(doctorService);
    }

    @Test
    void shouldRouteToFilterDoctorByNameAndSpecialty() {
        when(doctorService.filterDoctorByNameAndSpecialty(FILTER_NAME, FILTER_SPECIALTY))
                .thenReturn(List.of());

        service.filterDoctor(FILTER_NAME, "", FILTER_SPECIALTY);

        verify(doctorService).filterDoctorByNameAndSpecialty(FILTER_NAME, FILTER_SPECIALTY);
        verifyNoMoreInteractions(doctorService);
    }

    @Test
    void shouldRouteToFilterDoctorByTimeAndSpecialty() {
        when(doctorService.filterDoctorByTimeAndSpecialty(FILTER_TIME, FILTER_SPECIALTY))
                .thenReturn(List.of());

        service.filterDoctor("", FILTER_TIME, FILTER_SPECIALTY);

        verify(doctorService).filterDoctorByTimeAndSpecialty(FILTER_TIME, FILTER_SPECIALTY);
        verifyNoMoreInteractions(doctorService);
    }

    @Test
    void shouldRouteToFilterDoctorsByNameSpecialtyAndTime() {
        when(doctorService.filterDoctorsByNameAndSpecialtyAndTime(FILTER_NAME, FILTER_TIME, FILTER_SPECIALTY))
                .thenReturn(List.of());

        service.filterDoctor(FILTER_NAME, FILTER_TIME, FILTER_SPECIALTY);

        verify(doctorService)
                .filterDoctorsByNameAndSpecialtyAndTime(FILTER_NAME, FILTER_TIME, FILTER_SPECIALTY);
        verifyNoMoreInteractions(doctorService);
    }

    @Test
    void shouldRouteToGetDoctorsWhenThereIsNoFilters() {
        when(doctorService.getDoctors())
                .thenReturn(List.of());

        service.filterDoctor("", "", "");

        verify(doctorService).getDoctors();
        verifyNoMoreInteractions(doctorService);
    }

    @Test
    void shouldHandleNullInputs() {
        when(doctorService.getDoctors())
                .thenReturn(List.of());

        service.filterDoctor(null, null, null);

        verify(doctorService).getDoctors();
        verifyNoMoreInteractions(doctorService);
    }

    @Test
    void shouldTrimTrailingWhitespacesOnRoutingByName() {
        when(doctorService.findDoctorByName(FILTER_NAME))
                .thenReturn(List.of());

        service.filterDoctor("  Jane  ", "", "");

        verify(doctorService).findDoctorByName(FILTER_NAME);
        verifyNoMoreInteractions(doctorService);
    }

    @Test
    void shouldTrimTrailingWhitespacesOnRoutingByTime() {
        when(doctorService.findDoctorByTime(FILTER_TIME))
                .thenReturn(List.of());

        service.filterDoctor("  ", "  AM  ", "  ");

        verify(doctorService).findDoctorByTime(FILTER_TIME);
        verifyNoMoreInteractions(doctorService);
    }

    @Test
    void shouldTrimTrailingWhitespacesOnRoutingBySpecialty() {
        when(doctorService.findDoctorBySpecialty(FILTER_SPECIALTY))
                .thenReturn(List.of());

        service.filterDoctor("  ", "  ", "  Cardiologist ");

        verify(doctorService).findDoctorBySpecialty(FILTER_SPECIALTY);
        verifyNoMoreInteractions(doctorService);
    }
}
