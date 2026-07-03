package com.project.back_end.DTO;

import com.project.back_end.enums.AvailableTime;
import jakarta.validation.constraints.*;

import java.util.List;

public record DoctorProfileUpdateDTO(
        @NotNull(message = "Doctor id is required")
        Long id,

        @NotBlank(message = "Doctor's name cannot be null or blank")
        @Size(min = 3, max = 100)
        String name,

        @NotBlank(message = "Doctor's specialty cannot be null or blank")
        @Size(min = 3, max = 50)
        String specialty,

        @NotNull(message = "Doctor's phone number is required")
        @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
        String phone,

        @NotEmpty(message = "You have to select one available time slot at least")
        List<AvailableTime> availableTimes
) {
}
