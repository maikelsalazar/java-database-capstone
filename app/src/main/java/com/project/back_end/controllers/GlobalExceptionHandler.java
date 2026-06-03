package com.project.back_end.controllers;

import com.project.back_end.exceptions.*;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handledUnauthorized(UnauthorizedException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", "Unauthorized");

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(body);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<Map<String, Object>> handledUnauthorized(SignatureException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", "Unauthorized");

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(body);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleNoSuchElement(NoSuchElementException ex) {

        return ResponseEntity
                .notFound()
                .build();
    }

    @ExceptionHandler(NotAllowedException.class)
    public ResponseEntity<Map<String, Object>> handleNotAllowed(NotAllowedException ex) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                        .forEach(
                                error -> errors.put(error.getField(), error.getDefaultMessage())
                        );

        body.put("errors", errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(MethodArgumentTypeMismatchException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", "Bad Request");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateEmail(DuplicateEmailException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(body);
    }

    @ExceptionHandler(IllegalAppointmentUpdateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalAppointmentUpdate(IllegalAppointmentUpdateException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(body);
    }

    @ExceptionHandler(AppointmentAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleAppointmentAlreadyExists(AppointmentAlreadyExistsException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(body);
    }

    @ExceptionHandler(AppointmentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAppointmentNotFound(AppointmentNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(body);
    }

    @ExceptionHandler(UnavailableDoctorException.class)
    public ResponseEntity<Map<String, Object>> handleUnavailableDoctor(UnavailableDoctorException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(body);
    }

}
