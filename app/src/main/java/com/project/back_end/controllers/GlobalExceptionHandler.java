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

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(MethodArgumentTypeMismatchException ex) {
        return badRequest("Bad Request");
    }

    @ExceptionHandler(AppointmentTimeInPastException.class)
    public ResponseEntity<Map<String, Object>> handleAppointmentTimeInPast(AppointmentTimeInPastException ex) {
        return badRequest(ex.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleNoSuchElement(NoSuchElementException ex) {
        return notFound(ex.getMessage());
    }

    @ExceptionHandler(NotAllowedException.class)
    public ResponseEntity<Map<String, Object>> handleNotAllowed(NotAllowedException ex) {
        return forbidden();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(
                        error -> errors.put(error.getField(), error.getDefaultMessage())
                );

        return badRequest(errors);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateEmail(DuplicateEmailException ex) {
        return conflict(ex.getMessage());
    }

    @ExceptionHandler(IllegalAppointmentUpdateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalAppointmentUpdate(IllegalAppointmentUpdateException ex) {
        return conflict(ex.getMessage());
    }

    @ExceptionHandler(AppointmentAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleAppointmentAlreadyExists(AppointmentAlreadyExistsException ex) {
        return conflict(ex.getMessage());
    }

    @ExceptionHandler(UnavailableDoctorException.class)
    public ResponseEntity<Map<String, Object>> handleUnavailableDoctor(UnavailableDoctorException ex) {
        return conflict(ex.getMessage());
    }

    @ExceptionHandler(AppointmentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAppointmentNotFound(AppointmentNotFoundException ex) {
        return notFound(ex.getMessage());
    }

    @ExceptionHandler(DoctorNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleDoctorNotFound(DoctorNotFoundException ex) {
        return notFound(ex.getMessage());
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePatientNotFound(PatientNotFoundException ex) {
        return notFound(ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handledUnauthorized(UnauthorizedException ex) {
        return unauthorized();
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<Map<String, Object>> handledUnauthorized(SignatureException ex) {
        return unauthorized();
    }

    private ResponseEntity<Map<String, Object>> badRequest(Map<String, String> errors) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("errors", errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    private ResponseEntity<Map<String, Object>> badRequest(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    private ResponseEntity<Map<String, Object>> conflict(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    private ResponseEntity<Map<String, Object>> forbidden() {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .build();
    }

    private ResponseEntity<Map<String, Object>> notFound(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    private ResponseEntity<Map<String, Object>> unauthorized() {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", "Unauthorized");

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(body);
    }
}
