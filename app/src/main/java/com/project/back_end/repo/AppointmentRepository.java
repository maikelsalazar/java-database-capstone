package com.project.back_end.repo;

import com.project.back_end.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientIdOrderByAppointmentTimeDesc(Long patientId);

    boolean existsByDoctorIdAndAppointmentTimeAndIdNot(
            Long doctorId,
            LocalDateTime appointmentTime,
            Long id);

    boolean existsByPatientIdAndAppointmentTimeAndIdNot(
            Long patientId,
            LocalDateTime localDateTime,
            Long id);

    boolean existsByDoctorIdAndAppointmentTime(Long doctorId, LocalDateTime appointmentTime);

    boolean existsByPatientIdAndAppointmentTime(Long patientId, LocalDateTime appointmentTime);

    List<Appointment> findByDoctorIdAndPatientIdAndAppointmentTimeAndStatus(
            Long doctorId, Long patientId, LocalDateTime appointmentTime, int status
    );

    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(Long id, LocalDateTime start, LocalDateTime end);

    List<Appointment> findByDoctorIdAndPatientNameContainingAndAppointmentTimeBetween(Long id, String patientName, LocalDateTime start, LocalDateTime end);
}
