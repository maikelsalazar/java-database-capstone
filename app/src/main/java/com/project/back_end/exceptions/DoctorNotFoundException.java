package com.project.back_end.exceptions;

public class DoctorNotFoundException extends RuntimeException {
    public DoctorNotFoundException() {
        super("Doctor Not Found");
    }
}
