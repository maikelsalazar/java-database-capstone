package com.project.back_end.services;

import com.project.back_end.DTO.DoctorCreateDTO;
import com.project.back_end.DTO.DoctorDTO;
import com.project.back_end.exceptions.DuplicateEmailException;
import com.project.back_end.mappers.DoctorMapper;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    public DoctorService(DoctorRepository doctorRepository, PasswordEncoder passwordEncoder) {
        this.doctorRepository = doctorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void getDoctorAvailability() {
        throw new UnsupportedOperationException("No implemented yet");
    }

    @Transactional
    public void saveDoctor(DoctorCreateDTO dto) {
        if (doctorRepository.existsByEmail(dto.email())) {
            throw new DuplicateEmailException();
        }

        Doctor doctor = DoctorMapper.fromCreateDTO(dto, passwordEncoder);
        doctorRepository.save(doctor);
    }

    public void updateDoctor() {
        throw new UnsupportedOperationException("No implemented yet");
    }

    @Transactional
    public List<DoctorDTO> getDoctors() {
        List<Doctor> doctorList = doctorRepository.findAll();

        return DoctorMapper.toDTOList(doctorList);
    }

    @Transactional
    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }

    public void validateDoctor() {
        throw new UnsupportedOperationException("No implemented yet");
    }

    @Transactional
    public List<DoctorDTO> findDoctorByName(String name) {
        List<Doctor> doctorList = doctorRepository.findByName(name);

        return DoctorMapper.toDTOList(doctorList);
    }

    @Transactional
    public List<DoctorDTO> findDoctorByTime(String time) {
        List<Doctor> doctorList = doctorRepository.findByTime(time);

        return DoctorMapper.toDTOList(doctorList, time);
    }

    @Transactional
    public List<DoctorDTO> findDoctorBySpecialty(String specialty) {
        List<Doctor> doctorList = doctorRepository.findBySpecialty(specialty);

        return DoctorMapper.toDTOList(doctorList);
    }

    @Transactional
    public List<DoctorDTO> filterDoctorByNameAndTime(String name, String time) {
        List<Doctor> doctorList = doctorRepository.findByNameAndTime(name, time);

        return DoctorMapper.toDTOList(doctorList, time);
    }

    @Transactional
    public List<DoctorDTO> filterDoctorByNameAndSpecialty(String name, String specialty) {
        List<Doctor> doctorList = doctorRepository.findByNameAndSpecialty(name, specialty);

        return DoctorMapper.toDTOList(doctorList);
    }

    @Transactional
    public List<DoctorDTO> filterDoctorByTimeAndSpecialty(String time, String specialty) {
        List<Doctor> doctorList = doctorRepository.findByTimeAndSpecialty(time, specialty);

        return DoctorMapper.toDTOList(doctorList, time);
    }

    @Transactional
    public List<DoctorDTO> filterDoctorsByNameAndSpecialtyAndTime(String name, String time, String specialty) {
        List<Doctor> doctorList = doctorRepository.findByNameAndTimeAndSpecialty(name, time, specialty);

        return DoctorMapper.toDTOList(doctorList, time);
    }
}
