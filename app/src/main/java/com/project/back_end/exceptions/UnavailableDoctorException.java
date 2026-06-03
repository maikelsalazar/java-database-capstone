package com.project.back_end.exceptions;

public class UnavailableDoctorException extends RuntimeException {
    public static final String UNAVAILABLE_DOCTOR = "Doctor is unavailable at this time";

    public UnavailableDoctorException() {
        super(UNAVAILABLE_DOCTOR);
    }
}
