package com.project.back_end.DTO;

import jakarta.validation.constraints.*;

public class PatientCreateDTO {

    @NotBlank(message = "Patient's name cannot be null or blank")
    @Size(min = 3, max = 100)
    private String name;

    @NotBlank(message = "Email cannot be null or blank")
    @Email
    @Size(min = 6, max = 100)
    private String email;

    @NotBlank(message = "Password cannot be null or blank")
    @Size(min = 8, max = 15)
    private String password;

    @NotBlank(message = "Phone number cannot be null or blank")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    private String phone;

    @NotBlank(message = "Address cannot be null or blank")
    @Size(min = 3, max = 255)
    private String address;

    public PatientCreateDTO() {
    }

    public PatientCreateDTO(String name, String email, String password, String phone, String address) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
