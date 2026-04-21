package com.project.back_end.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "prescriptions")
public class Prescription {

    @Id
    private String id;

    @NotNull
    @Indexed(unique = true)
    private Long appointmentId;

    @NotNull
    @Size(min = 3, max = 100)
    private String patientName;

    @Valid
    @NotEmpty
    private List<MedicationTime> medications;

    @Size(max = 200)
    private String doctorNotes;

    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

    public Prescription() {
    }

    public Prescription(Long appointmentId, String patientName,
                        List<MedicationTime> medications, String doctorNotes) {
        this.appointmentId = appointmentId;
        this.patientName = patientName;
        this.medications = List.copyOf(medications);
        this.doctorNotes = doctorNotes;
    }

    public String getId() {
        return id;
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

    public List<MedicationTime> getMedications() {
        return medications;
    }

    public void setMedications(List<MedicationTime> medications) {
        this.medications = medications;
    }

    public String getDoctorNotes() {
        return doctorNotes;
    }

    public void setDoctorNotes(String doctorNotes) {
        this.doctorNotes = doctorNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
