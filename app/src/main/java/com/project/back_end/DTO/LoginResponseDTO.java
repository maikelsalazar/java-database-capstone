package com.project.back_end.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginResponseDTO(boolean success,
                               String message,
                               String token) {

    public static LoginResponseDTO success(String token) {
        return new LoginResponseDTO(true, "Login successful", token);
    }

    public static LoginResponseDTO failure() {
        return failure("Invalid credentials");
    }

    public static LoginResponseDTO failure(String message) {
        return new LoginResponseDTO(false, message, null);
    }
}
