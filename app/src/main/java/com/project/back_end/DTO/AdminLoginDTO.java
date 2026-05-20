package com.project.back_end.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AdminLoginDTO {

    @NotNull(message = "Username cannot be null")
    @Size(min = 3, max = 100)
    private String username;

    @NotNull(message = "Password cannot be null")
    @Size(min = 8, max = 15)
    private String password;

    public AdminLoginDTO() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
