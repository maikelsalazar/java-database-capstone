package com.project.back_end.DTO;

import jakarta.validation.constraints.*;

public record PatientCreateDTO(
    @NotBlank(message = "Patient's name cannot be null or blank")
    @Size(min = 3, max = 100)
    String name,

    @Email(message = "Email must be a valid email address")
    @Size(min = 6, max = 100)
    String email,

    @NotBlank(message = "Password cannot be null or blank")
    @Size(min = 8, max = 15)
    String password,

    @NotBlank(message = "Phone number cannot be null or blank")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    String phone,

    @NotBlank(message = "Address cannot be null or blank")
    @Size(min = 3, max = 255)
    String address
) {

}
