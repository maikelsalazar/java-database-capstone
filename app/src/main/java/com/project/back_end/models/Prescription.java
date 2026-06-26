package com.project.back_end.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "prescriptions")
public class Prescription {

    @Id
    private String id;

    @Indexed(unique = true)
    private Long appointmentId;

    private String patientName;

    private String medication;

    private String dosage;

    private String doctorNotes;

    public Prescription() {
    }

    public Prescription(Long appointmentId, String patientName, String medication, String dosage, String doctorNotes) {
        this.appointmentId = appointmentId;
        this.patientName = patientName;
        this.medication = medication;
        this.dosage = dosage;
        this.doctorNotes = doctorNotes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getDoctorNotes() {
        return doctorNotes;
    }

    public void setDoctorNotes(String doctorNotes) {
        this.doctorNotes = doctorNotes;
    }
}
