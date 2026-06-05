package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentCreateDTO;
import com.project.back_end.DTO.AppointmentUpdateDTO;
import com.project.back_end.DTO.DoctorAppointmentDTO;
import com.project.back_end.enums.AvailableTime;
import com.project.back_end.exceptions.*;
import com.project.back_end.mappers.DoctorAppointmentDTOMapper;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final Clock clock;

    public AppointmentService(AppointmentRepository appointmentRepository, DoctorRepository doctorRepository, PatientRepository patientRepository, Clock clock) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.clock = clock;
    }

    @Transactional
    public void createAppointment(AppointmentCreateDTO appointmentCreate, String email) {
        Appointment newAppointment = validateAndBuildAppointment(appointmentCreate, email);

        appointmentRepository.save(newAppointment);
    }

    @Transactional
    public void updateAppointment(AppointmentUpdateDTO appointmentUpdateDTO, String email) {
        Appointment appointmentToUpdate = validateAppointmentUpdate(appointmentUpdateDTO, email);

        appointmentRepository.save(appointmentToUpdate);
    }

    private Appointment validateAndBuildAppointment(AppointmentCreateDTO appointmentCreate, String email) {
        LocalDateTime appointmentTime = appointmentCreate.appointmentTime();

        Patient patient = patientRepository.findByEmail(email);
        if (patient == null) {
            throw new PatientNotFoundException();
        }

        Patient patientRequested = patientRepository.findById(appointmentCreate.patient().id())
                .orElseThrow(PatientNotFoundException::new);
        if (!patientRequested.getId().equals(patient.getId())) {
            throw new NotAllowedException();
        }

        Doctor doctor = doctorRepository.findById(appointmentCreate.doctor().id())
                .orElseThrow(DoctorNotFoundException::new);

        validateAppointmentTime(appointmentTime);
        validateAppointmentAvailabilityForDoctor(doctor, appointmentTime);
        validateAppointmentAvailabilityForPatient(patient, appointmentTime);

        Appointment newAppointment = new Appointment();
        newAppointment.setDoctor(doctor);
        newAppointment.setPatient(patient);
        newAppointment.setAppointmentTime(appointmentTime);
        newAppointment.setStatus(Appointment.STATUS_SCHEDULED);

        return newAppointment;
    }

    @Transactional(readOnly = true)
    public List<DoctorAppointmentDTO> getAppointmentsByDate(LocalDate appointmentDate, String email) {
        return getAppointments(appointmentDate, email, Optional.empty());
    }

    @Transactional(readOnly = true)
    public List<DoctorAppointmentDTO> getAppointmentsByDateAndName(LocalDate appointmentDate, String name, String email) {
        return getAppointments(appointmentDate, email, Optional.ofNullable(name));
    }

    private List<DoctorAppointmentDTO> getAppointments(LocalDate appointmentDate, String email, Optional<String> patientName) {
        Doctor doctor = doctorRepository.findByEmail(email);
        if (doctor == null) {
            throw new DoctorNotFoundException();
        }

        LocalDateTime start = appointmentDate.atStartOfDay();
        LocalDateTime end = appointmentDate.atTime(LocalTime.MAX);

        String name = patientName.orElse("").trim();

        List<Appointment> appointments =
                name.isEmpty()
                ? appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctor.getId(), start, end)
                : appointmentRepository.findByDoctorIdAndPatientNameContainingAndAppointmentTimeBetween(doctor.getId(), name, start, end);

        return DoctorAppointmentDTOMapper.fromList(appointments);
    }

    private Appointment validateAppointmentUpdate(AppointmentUpdateDTO appointmentUpdateDTO, String email) {
        Appointment appointment = appointmentRepository.findById(appointmentUpdateDTO.id())
                .orElseThrow(AppointmentNotFoundException::new);

        validateAppointmentIntegrity(appointmentUpdateDTO, appointment, email);

        LocalDateTime newAppointmentTime = appointmentUpdateDTO.appointmentTime();
        if (appointment.getAppointmentTime().isEqual(newAppointmentTime)) {
            return appointment;
        }

        long appointmentId = appointment.getId();
        Doctor doctor = appointment.getDoctor();
        Patient patient = appointment.getPatient();

        validateAppointmentTime(newAppointmentTime);
        validateAppointmentAvailabilityForDoctor(doctor, newAppointmentTime, appointmentId);
        validateAppointmentAvailabilityForPatient(patient, newAppointmentTime, appointmentId);

        appointment.setAppointmentTime(newAppointmentTime);

        return appointment;
    }

    private static void validateAppointmentIntegrity(AppointmentUpdateDTO appointmentUpdateDTO, Appointment appointment, String email) {
        if (!appointment.getDoctor().getId().equals(appointmentUpdateDTO.doctor().id())) {
            throw new IllegalAppointmentUpdateException("distinct doctor");
        }

        if (!appointment.getPatient().getId().equals(appointmentUpdateDTO.patient().id())) {
            throw new IllegalAppointmentUpdateException("distinct patient");
        }

        if (!appointment.getPatient().getEmail().equalsIgnoreCase(email)) {
            throw new NotAllowedException();
        }

        if (!appointment.isScheduled()) {
            throw IllegalAppointmentUpdateException.illegalAppointmentStatus();
        }
    }

    private void validateAppointmentTime(LocalDateTime appointmentTime) {
        LocalDateTime now = LocalDateTime.now(clock);
        if (now.isAfter(appointmentTime)) {
            throw new AppointmentTimeInPastException();
        }
    }

    private void validateAppointmentAvailabilityForDoctor(Doctor doctor, LocalDateTime appointmentTime) {
        if (!doctor.getAvailableTimes().contains(AvailableTime.fromStartTime(appointmentTime))) {
            throw new UnavailableDoctorException();
        }

        if (appointmentRepository.existsByDoctorIdAndAppointmentTime(doctor.getId(), appointmentTime)) {
            throw AppointmentAlreadyExistsException.doctorAlreadyBooked();
        }
    }

    private void validateAppointmentAvailabilityForDoctor(Doctor doctor, LocalDateTime appointmentTime, long appointmentId) {
        if (!doctor.getAvailableTimes().contains(AvailableTime.fromStartTime(appointmentTime))) {
            throw new UnavailableDoctorException();
        }

        if (appointmentRepository.existsByDoctorIdAndAppointmentTimeAndIdNot(doctor.getId(), appointmentTime, appointmentId)) {
            throw AppointmentAlreadyExistsException.doctorAlreadyBooked();
        }
    }

    private void validateAppointmentAvailabilityForPatient(Patient patient, LocalDateTime appointmentTime) {
        if (appointmentRepository.existsByPatientIdAndAppointmentTime(patient.getId(), appointmentTime)) {
            throw AppointmentAlreadyExistsException.patientHasAnotherAppointment();
        }
    }

    private void validateAppointmentAvailabilityForPatient(Patient patient, LocalDateTime appointmentTime, long appointmentId) {
        if (appointmentRepository.existsByPatientIdAndAppointmentTimeAndIdNot(patient.getId(), appointmentTime, appointmentId)) {
            throw AppointmentAlreadyExistsException.patientHasAnotherAppointment();
        }
    }
}
