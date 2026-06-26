package com.project.back_end.DTO.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import java.util.Map;

public record ApiDataResponseDTO(@JsonAnyGetter Map<String, Object> data) {
    public static ApiDataResponseDTO of(String key, Object value) {
        return new ApiDataResponseDTO(Map.of(key, value));
    }
}
