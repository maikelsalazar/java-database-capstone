package com.project.back_end.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.back_end.enums.AvailableTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctor")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Doctor's name cannot be null or blank")
    @Size(min = 3, max = 100)
    @Column(nullable = false, length = 100)
    private String name; // Doctor's full name

    @NotBlank(message = "Doctor's specialty cannot be null or blank")
    @Size(min = 3, max = 50)
    @Column(nullable = false, length = 50)
    private String specialty;

    @Email(message = "Doctor's email must be a valid email address")
    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @NotBlank(message = "Doctor's password is required")
    @Size(min = 8, max = 15)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false, length = 100)
    private String password;

    @NotNull(message = "Doctor's phone number is required")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    @Column(nullable = false, length = 15)
    private String phone;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<AvailableTime> availableTimes = new ArrayList<>();

    @OneToMany(
            mappedBy = "doctor",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Appointment> appointments = new ArrayList<>();

    public Doctor() {
    }

    public Doctor(String name, String specialty, String email, String password, String phone, List<AvailableTime> availableTimes, List<Appointment> appointments) {
        this.name = name;
        this.specialty = specialty;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.availableTimes = availableTimes;
        this.appointments = appointments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }
}
