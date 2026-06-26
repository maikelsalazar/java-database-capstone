package com.project.back_end.controllers;

import com.fasterxml.jackson.core.JsonParseException;
import com.project.back_end.DTO.response.ApiResponseDTO;
import com.project.back_end.exceptions.*;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponseDTO> handleBadRequest(MethodArgumentTypeMismatchException ex) {
        return failure(HttpStatus.BAD_REQUEST, "Invalid value: " + ex.getName());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO> handleBadRequest(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(
                        error -> errors.put(error.getField(), error.getDefaultMessage())
                );

        return failure(HttpStatus.BAD_REQUEST, "Request validation failed", errors);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponseDTO> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return failure(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Method %s not allowed".formatted(ex.getMethod())
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseDTO> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String message = "Invalid request body";

        Throwable cause = ex.getMostSpecificCause();
        if (cause instanceof JsonParseException) {
            message = "Invalid JSON";
        }
        else if (cause instanceof DateTimeParseException) {
            message = "Invalid date and time";
        }

        return failure(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(AppointmentTimeInPastException.class)
    public ResponseEntity<ApiResponseDTO> handleAppointmentTimeInPast(AppointmentTimeInPastException ex) {
        return failure(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(NotAllowedException.class)
    public ResponseEntity<ApiResponseDTO> handleNotAllowed(NotAllowedException ex) {
        return failure(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler({
            DuplicateEmailException.class,
            IllegalAppointmentUpdateException.class,
            AppointmentAlreadyExistsException.class,
            AppointmentAlreadyHasPrescriptionException.class,
            UnavailableDoctorException.class

    })
    public ResponseEntity<ApiResponseDTO> handleConflict(RuntimeException ex) {
        return failure(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler({
            AppointmentNotFoundException.class,
            DoctorNotFoundException.class,
            PatientNotFoundException.class
    })
    public ResponseEntity<ApiResponseDTO> handleNotFound(RuntimeException ex) {
        return failure(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({
            UnauthorizedException.class,
            SignatureException.class,
            MalformedJwtException.class
    })
    public ResponseEntity<ApiResponseDTO> handleUnauthorized(Exception ex) {
        return failure(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO> handleGeneric(Exception ex) {
        return failure(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    private ResponseEntity<ApiResponseDTO> failure(HttpStatus status, String message, Map<String, String> errors) {
        return ResponseEntity.status(status)
                .body(ApiResponseDTO.failure(message, errors));
    }

    private ResponseEntity<ApiResponseDTO> failure(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(ApiResponseDTO.failure(message));
    }
}
