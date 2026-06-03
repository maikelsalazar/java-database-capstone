package com.project.back_end.builders;

import com.project.back_end.models.Patient;

public class PatientBuilder {

    private String name = "John Doe";
    private String email = "john.doe@email.com";
    private String password = "password";
    private String phone = "1234567890";
    private String address = "Av. 5, NY";

    public static PatientBuilder aPatient() {
        return new PatientBuilder();
    }

    public PatientBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public Patient build() {
        Patient p = new Patient();
        p.setName(name);
        p.setEmail(email);
        p.setPassword(password);
        p.setPhone(phone);
        p.setAddress(address);
        return p;
    }
}
