package com.project.back_end.DTO;

public record ApiResponseDTO (boolean success, String message) {

    public static ApiResponseDTO success(String message) {
        return new ApiResponseDTO(true, message);
    }
}
