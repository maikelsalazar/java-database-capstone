package com.project.back_end.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "appointment", indexes = {
        @Index(name = "idx_doctor_time", columnList = "doctor_id,appointmentTime", unique = true)
})
public class Appointment {

    public static final int STATUS_SCHEDULED = 0;
    public static final int STATUS_COMPLETED = 1;
    public static final int STATUS_CANCELED = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false)
    private LocalDateTime appointmentTime;

    @Column(nullable = false)
    private int status; // 0=scheduled, 1=completed, 2=cancelled

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getEndTime() {
        return appointmentTime != null ? appointmentTime.plusHours(1) : null;
    }

    public LocalDate getAppointmentDate() {
        return appointmentTime != null ? appointmentTime.toLocalDate() : null;
    }

    public LocalTime getAppointmentTimeOnly() {
        return appointmentTime != null ? appointmentTime.toLocalTime() : null;
    }

    public boolean isScheduled() {
        return status == this.STATUS_SCHEDULED;
    }
}

