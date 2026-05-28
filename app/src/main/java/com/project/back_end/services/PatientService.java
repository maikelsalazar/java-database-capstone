package com.project.back_end.services;

import com.project.back_end.DTO.PatientCreateDTO;
import com.project.back_end.DTO.PatientLoginDTO;
import com.project.back_end.exceptions.DuplicateEmailException;
import com.project.back_end.mappers.PatientCreateDTOMapper;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.PatientRepository;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    private final PasswordEncoder passwordEncoder;

    public PatientService(PatientRepository patientRepository, PasswordEncoder passwordEncoder) {
        this.patientRepository = patientRepository;
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
}
