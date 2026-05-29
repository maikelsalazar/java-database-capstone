package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.DTO.PatientCreateDTO;
import com.project.back_end.DTO.PatientDTO;
import com.project.back_end.exceptions.DuplicateEmailException;
import com.project.back_end.exceptions.NotAllowedException;
import com.project.back_end.mappers.AppointmentMapper;
import com.project.back_end.mappers.PatientCreateDTOMapper;
import com.project.back_end.mappers.PatientMapper;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    private final AppointmentRepository appointmentRepository;

    private final PasswordEncoder passwordEncoder;

    public PatientService(PatientRepository patientRepository, AppointmentRepository appointmentRepository, PasswordEncoder passwordEncoder) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void createPatient(PatientCreateDTO newPatientDTO) {
        Patient newPatient = PatientCreateDTOMapper.toEntity(newPatientDTO, passwordEncoder);

        try {
            patientRepository.save(newPatient);
        } catch (DataIntegrityViolationException ex) {
            // This could be improved
            // But email duplication is the only constraint we're assuming here
            throw new DuplicateEmailException();
        }
    }

    public PatientDTO getPatient(String email) {
        Patient patient = patientRepository.findByEmail(email);
        if (patient == null) {
            throw new NoSuchElementException();
        }

        return PatientMapper.toDTO(patient);
    }

    @Transactional
    public List<AppointmentDTO> getPatientAppointments(Long patientId) {
        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);

        return AppointmentMapper.toDTOList(appointments);
    }

    public void validateOwnershipOrThrow(Long id, String email) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(NotAllowedException::new);

        if (!patient.getEmail().equalsIgnoreCase(email)) {
            throw new NotAllowedException();
        }
    }


}
