package com.project.back_end.exceptions;

public class IllegalAppointmentUpdateException extends RuntimeException {
    public static final String APPOINTMENT_TIME_ALREADY_PAST = "Appointment time has already past";
    public static final String ILLEGAL_APPOINTMENT_STATUS = "Only scheduled appointments can be updated";

    public IllegalAppointmentUpdateException(String message) {
        super(message);
    }

    public static IllegalAppointmentUpdateException appointmentTimeAlreadyPast() {
        return new IllegalAppointmentUpdateException(IllegalAppointmentUpdateException.APPOINTMENT_TIME_ALREADY_PAST);
    }

    public static IllegalAppointmentUpdateException illegalAppointmentStatus() {
        return new IllegalAppointmentUpdateException(IllegalAppointmentUpdateException.ILLEGAL_APPOINTMENT_STATUS);
    }
}
