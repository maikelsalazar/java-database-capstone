package com.project.back_end.exceptions;

public class NotAllowedException extends RuntimeException {

    public NotAllowedException() {
        super("Not allowed");
    }

    public NotAllowedException(String message) {
        super(message);
    }
}
