package com.project.back_end.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DoctorLoginDTO {

    @NotNull(message = "email cannot be null")
    @Email
    @Size(min = 3, max = 100)
    private String email;

    @NotNull(message = "Password cannot be null")
    @Size(min = 8, max = 15)
    private String password;

    public DoctorLoginDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
