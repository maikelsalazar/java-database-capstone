package com.project.back_end.builders;

import com.project.back_end.enums.AvailableTime;
import com.project.back_end.models.Doctor;

import java.util.ArrayList;
import java.util.List;

public class DoctorBuilder {

    private String name = "Jane Doe";
    private String email = "jane.doe@email.com";
    private String password = "doctor@1234";
    private String phone = "1010101010";
    private String specialty = "Neurologist";
    private List<AvailableTime> availableTimes = List.of(
            AvailableTime.SLOT_09_10,
            AvailableTime.SLOT_10_11
    );

    public static DoctorBuilder aDoctor() {
        return new DoctorBuilder();
    }

    public DoctorBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public DoctorBuilder withAvailableTimes(List<AvailableTime> availableTimes) {
        this.availableTimes = new ArrayList<>(availableTimes);
        return this;
    }

    public Doctor build() {
        Doctor d = new Doctor();
        d.setName(name);
        d.setEmail(email);
        d.setPhone(phone);
        d.setPassword(password);
        d.setSpecialty(specialty);
        d.setAvailableTimes(new ArrayList<>(availableTimes));

        return d;
    }
}
