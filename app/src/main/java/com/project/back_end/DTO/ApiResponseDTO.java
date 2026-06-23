package com.project.back_end.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponseDTO (
        boolean success,
        String message,
        Map<String, String> errors
) {

    public static ApiResponseDTO success(String message) {
        return new ApiResponseDTO(true, message, null);
    }

    public static ApiResponseDTO failure(String message){
        return new ApiResponseDTO(false, message, null);
    }

    public static ApiResponseDTO failure(String message, Map<String, String> errors){
        return new ApiResponseDTO(false, message, errors);
    }
}
