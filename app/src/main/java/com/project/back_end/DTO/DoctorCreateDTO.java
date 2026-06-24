package com.project.back_end.DTO;

import com.project.back_end.enums.AvailableTime;
import jakarta.validation.constraints.*;

import java.util.List;

public record DoctorCreateDTO(
    @NotBlank(message = "Doctor's name cannot be null or blank")
    @Size(min = 3, max = 100)
    String name,

    @NotBlank(message = "Doctor's specialty cannot be null or blank")
    @Size(min = 3, max = 50)
    String specialty,

    @Email(message = "Doctor's email must be a valid email address")
    @Size(min = 3, max = 50)
    String email,

    @NotNull(message = "Doctor's password is required")
    @Size(min = 8, max = 15)
    String password,

    @NotNull(message = "Doctor's phone number is required")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    String phone,

    @NotEmpty(message = "You have to select one available time slot at least")
    List<AvailableTime> availableTimes
) {
}
