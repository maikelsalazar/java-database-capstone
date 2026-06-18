package com.project.back_end.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AdminLoginDTO(@NotNull(message = "Username cannot be null")
                            @Size(min = 3, max = 100)
                            String username,
                            @NotNull(message = "Password cannot be null")
                            @Size(min = 8, max = 15)
                            String password
) {

}
