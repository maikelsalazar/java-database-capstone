package com.project.back_end.exceptions;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException() {
        super("Patient Not Found");
    }
}
