package com.project.back_end.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PatientLoginDTO(
    @Email(message = "Email's must be an valid email address")
    @Size(min = 3, max = 100)
    String email,

    @NotBlank(message = "Password cannot be null or blank")
    @Size(min = 8, max = 15)
    String password
) {

}
