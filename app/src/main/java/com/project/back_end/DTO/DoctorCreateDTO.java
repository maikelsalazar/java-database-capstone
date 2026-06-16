package com.project.back_end.DTO;

import com.project.back_end.enums.AvailableTime;
import jakarta.validation.constraints.*;

import java.util.List;

public class DoctorCreateDTO {

    @NotBlank(message = "Doctor's name cannot be null or blank")
    @Size(min = 3, max = 100)
    private String name;

    @NotBlank(message = "Doctor's specialty cannot be null or blank")
    @Size(min = 3, max = 50)
    private String specialty;

    @NotBlank(message = "Doctor's email must be a valid email address")
    @Email
    @Size(min = 3, max = 50)
    private String email;

    @NotNull(message = "Doctor's password is required")
    @Size(min = 8, max = 15)
    private String password;

    @NotNull
    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    private String phone;

    @NotEmpty(message = "You have to select one available time slot at least")
    private List<AvailableTime> availableTimes;

    public DoctorCreateDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
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

    public List<AvailableTime> getAvailableTimes() {
        return availableTimes;
    }

    public void setAvailableTimes(List<AvailableTime> availableTimes) {
        this.availableTimes = availableTimes;
    }
}
