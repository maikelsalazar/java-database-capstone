package com.project.back_end.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EmailLoginDTO(
        @Email(message = "Email must be a valid email address")
        @Size(min = 3, max = 100)
        String email,

        @NotNull(message = "Password cannot be null")
        @Size(min = 8, max = 15)
        String password
) {

}
