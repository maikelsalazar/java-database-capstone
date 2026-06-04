package com.project.back_end.exceptions;

public class AppointmentTimeInPastException extends RuntimeException {
    public AppointmentTimeInPastException()
    {
        super("Appointment time cannot be in the past");
    }
}
