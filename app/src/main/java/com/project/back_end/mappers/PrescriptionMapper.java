package com.project.back_end.mappers;

import com.project.back_end.DTO.PrescriptionCreateDTO;
import com.project.back_end.DTO.PrescriptionDTO;
import com.project.back_end.DTO.PrescriptionListDTO;
import com.project.back_end.models.Prescription;

import java.util.List;

public class PrescriptionMapper {

    public static PrescriptionListDTO toListDTO(List<Prescription> prescriptions) {
        List<PrescriptionDTO> prescriptionDTOs = prescriptions.stream()
                .map(PrescriptionMapper::toDTO)
                .toList();

        return new PrescriptionListDTO(prescriptionDTOs);
    }

    public static PrescriptionDTO toDTO(Prescription p) {
        return new PrescriptionDTO(
                p.getAppointmentId(),
                p.getPatientName(),
                p.getMedication(),
                p.getDosage(),
                p.getDoctorNotes());
    }

    public static PrescriptionCreateDTO toCreateDTO(Prescription p) {
        return new PrescriptionCreateDTO(
                p.getAppointmentId(),
                p.getPatientName(),
                p.getMedication(),
                p.getDosage(),
                p.getDoctorNotes());
    }

    public static Prescription toEntity(PrescriptionCreateDTO p) {
        Prescription prescription = new Prescription();
        prescription.setAppointmentId(p.appointmentId());
        prescription.setPatientName(p.patientName());
        prescription.setMedication(p.medication());
        prescription.setDosage(p.dosage());
        prescription.setDoctorNotes(p.doctorNotes());

        return prescription;
    }
}
