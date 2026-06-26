package com.project.back_end.services;

import com.project.back_end.DTO.PrescriptionCreateDTO;
import com.project.back_end.DTO.PrescriptionListDTO;
import com.project.back_end.exceptions.AppointmentNotFoundException;
import com.project.back_end.exceptions.NotAllowedException;
import com.project.back_end.exceptions.AppointmentAlreadyHasPrescriptionException;
import com.project.back_end.mappers.PrescriptionMapper;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Prescription;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PrescriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository, AppointmentRepository appointmentRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public PrescriptionListDTO getPrescription(Long appointmentId, String doctorEmail) {
        validateAppointmentAndOwnership(appointmentId, doctorEmail);

        return PrescriptionMapper.toListDTO(prescriptionRepository.findByAppointmentId(appointmentId));
    }

    @Transactional
    public void savePrescription(PrescriptionCreateDTO prescriptionRequest, String doctorEmail) {
        Long appointmentId = prescriptionRequest.appointmentId();

        Appointment appointment = validateAppointmentAndOwnership(appointmentId, doctorEmail);
        if (prescriptionRepository.existsByAppointmentId(appointmentId)) {
            throw new AppointmentAlreadyHasPrescriptionException();
        }

        prescriptionRepository.save(PrescriptionMapper.toEntity(prescriptionRequest));

        appointment.setStatus(Appointment.STATUS_COMPLETED);
        appointmentRepository.save(appointment);
    }

    private Appointment validateAppointmentAndOwnership(Long appointmentId, String doctorEmail) {
        Appointment appointment = appointmentRepository
                .findById(appointmentId)
                .orElseThrow(AppointmentNotFoundException::new);

        if (!appointment.getDoctor().getEmail().equals(doctorEmail)) {
            throw new NotAllowedException("Doctor does not own this appointment");
        }

        return appointment;
    }
}
