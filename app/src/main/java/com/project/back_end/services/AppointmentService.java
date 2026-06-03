package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentRequestDTO;
import com.project.back_end.enums.AvailableTime;
import com.project.back_end.exceptions.*;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AppointmentService {

    private AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public void updateAppointment(AppointmentRequestDTO appointmentRequestDTO, String email) {
        Appointment appointment = appointmentRepository.findById(appointmentRequestDTO.id())
                .orElseThrow(AppointmentNotFoundException::new);

        long appointmentId = appointment.getId();
        Doctor doctor = appointment.getDoctor();
        Patient patient = appointment.getPatient();

        validateAppointmentIntegrity(appointmentRequestDTO, appointment, email);

        LocalDateTime newAppointmentTime = appointmentRequestDTO.appointmentTime();

        if (!doctor.getAvailableTimes().contains(AvailableTime.fromStartTime(newAppointmentTime))){
            throw new UnavailableDoctorException();
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(newAppointmentTime)) {
            throw IllegalAppointmentUpdateException.appointmentTimeAlreadyPast();
        }

        if (appointmentRepository.existsByDoctorIdAndAppointmentTimeAndIdNot(
                doctor.getId(),
                newAppointmentTime,
                appointmentId

        ))  {
            throw AppointmentAlreadyExistsException.doctorAlreadyBooked();
        }

        if (appointmentRepository.existsByPatientIdAndAppointmentTimeAndIdNot(
                patient.getId(),
                newAppointmentTime,
                appointmentId
        )) {
            throw AppointmentAlreadyExistsException.patientHasAnotherAppointment();
        }

        if (appointment.getAppointmentTime().isEqual(newAppointmentTime)) {
            return;
        }
        
        appointment.setAppointmentTime(newAppointmentTime);

        appointmentRepository.save(appointment);
    }

    private static void validateAppointmentIntegrity(AppointmentRequestDTO appointmentRequestDTO, Appointment appointment, String email) {
        if (!appointment.getDoctor().getId().equals(appointmentRequestDTO.doctor().id())){
            throw new IllegalAppointmentUpdateException("distinct doctor");
        }

        if (!appointment.getPatient().getId().equals(appointmentRequestDTO.patient().id())){
            throw new IllegalAppointmentUpdateException("distinct patient");
        }

        if (!appointment.getPatient().getEmail().equalsIgnoreCase(email)){
            throw new NotAllowedException();
        }

        if (!appointment.isScheduled()) {
            throw IllegalAppointmentUpdateException.illegalAppointmentStatus();
        }
    }
}
