package com.project.back_end.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PatientLoginDTO {

    @NotBlank(message = "email cannot be null or blank")
    @Email
    @Size(min = 3, max = 100)
    private String email;

    @NotBlank(message = "Password cannot be null or blank")
    @Size(min = 8, max = 15)
    private String password;

    public PatientLoginDTO() {
    }

    public PatientLoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
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
