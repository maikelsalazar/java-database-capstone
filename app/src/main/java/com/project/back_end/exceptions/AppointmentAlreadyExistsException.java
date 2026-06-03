package com.project.back_end.exceptions;

public class AppointmentAlreadyExistsException extends RuntimeException {
    public static final String DOCTOR_ALREADY_BOOKED = "Doctor is already booked at this time";
    public static final String PATIENT_HAS_ANOTHER_APPOINTMENT = "Patient has another appointment booked at this time";

    public AppointmentAlreadyExistsException(String message) {
        super(message);
    }

    public static AppointmentAlreadyExistsException doctorAlreadyBooked() {
        return new AppointmentAlreadyExistsException(DOCTOR_ALREADY_BOOKED);
    }

    public static AppointmentAlreadyExistsException patientHasAnotherAppointment() {
        return new AppointmentAlreadyExistsException(PATIENT_HAS_ANOTHER_APPOINTMENT);
    }
}
