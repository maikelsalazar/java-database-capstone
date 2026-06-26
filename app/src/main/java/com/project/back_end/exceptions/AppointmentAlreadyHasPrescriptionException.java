package com.project.back_end.exceptions;

public class AppointmentAlreadyHasPrescriptionException extends RuntimeException {

    public AppointmentAlreadyHasPrescriptionException() {
        super("Appointment already has prescription");
    }
}
