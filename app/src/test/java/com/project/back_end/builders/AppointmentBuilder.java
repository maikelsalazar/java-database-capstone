package com.project.back_end.builders;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;

import java.time.LocalDateTime;

public class AppointmentBuilder {

    private Doctor doctor;
    private Patient patient;
    private int status = Appointment.STATUS_SCHEDULED;
    private LocalDateTime appointmentTime;


    public static AppointmentBuilder anAppointment() {
        return new AppointmentBuilder();
    }

    public AppointmentBuilder withDoctor(Doctor doctor) {
        this.doctor = doctor;
        return this;
    }

    public AppointmentBuilder withPatient(Patient patient) {
        this.patient = patient;
        return this;
    }

    public AppointmentBuilder withStatus(int status) {
        this.status = status;
        return this;
    }

    public AppointmentBuilder withAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
        return this;
    }

    public Appointment build(){
        if (doctor == null
                || patient == null
                || appointmentTime == null
        ) {
            throw new IllegalStateException("Missing required fields");
        }

        Appointment copy = new Appointment();
        copy.setPatient(patient);
        copy.setDoctor(doctor);
        copy.setAppointmentTime(appointmentTime);
        copy.setStatus(status);

        return copy;
    }
}
