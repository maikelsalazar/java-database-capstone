package com.project.back_end.exceptions;

public class AppointmentNotFoundException extends RuntimeException {
    public static final String APPOINTMENT_NOT_FOUND = "Appointment not found";

    public AppointmentNotFoundException() {
        super(APPOINTMENT_NOT_FOUND);
    }
}
