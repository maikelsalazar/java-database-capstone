package com.project.back_end.services;

import com.project.back_end.DTO.DoctorCreateDTO;
import com.project.back_end.DTO.DoctorsDTO;
import com.project.back_end.exceptions.DuplicateEmailException;
import com.project.back_end.mappers.DoctorDTOMapper;
import com.project.back_end.mappers.DoctorsDTOMapper;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
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
        if (doctorRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmailException();
        }

        Doctor doctor = DoctorDTOMapper.fromCreate(dto, passwordEncoder);
        doctorRepository.save(doctor);
    }

    public void updateDoctor() {
        throw new UnsupportedOperationException("No implemented yet");
    }

    @Transactional
    public DoctorsDTO getDoctors() {
        List<Doctor> doctorList = doctorRepository.findAll();

        return DoctorsDTOMapper.fromDoctorList(doctorList);
    }

    @Transactional
    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }

    public void validateDoctor() {
        throw new UnsupportedOperationException("No implemented yet");
    }

    @Transactional
    public DoctorsDTO findDoctorByName(String name) {
        List<Doctor> doctorList = doctorRepository.findByName(name);

        return DoctorsDTOMapper.fromDoctorList(doctorList);
    }

    @Transactional
    public DoctorsDTO findDoctorByTime(String time) {
        List<Doctor> doctorList = doctorRepository.findByTime(time);

        return DoctorsDTOMapper.fromDoctorList(doctorList, time);
    }

    @Transactional
    public DoctorsDTO findDoctorBySpecialty(String specialty) {
        List<Doctor> doctorList = doctorRepository.findBySpecialty(specialty);

        return DoctorsDTOMapper.fromDoctorList(doctorList);
    }

    @Transactional
    public DoctorsDTO filterDoctorByNameAndTime(String name, String time) {
        List<Doctor> doctorList = doctorRepository.findByNameAndTime(name, time);

        return DoctorsDTOMapper.fromDoctorList(doctorList, time);
    }

    @Transactional
    public DoctorsDTO filterDoctorByNameAndSpecialty(String name, String specialty) {
        List<Doctor> doctorList = doctorRepository.findByNameAndSpecialty(name, specialty);

        return DoctorsDTOMapper.fromDoctorList(doctorList);
    }

    @Transactional
    public DoctorsDTO filterDoctorByTimeAndSpecialty(String time, String specialty) {
        List<Doctor> doctorList = doctorRepository.findByTimeAndSpecialty(time, specialty);

        return DoctorsDTOMapper.fromDoctorList(doctorList, time);
    }

    @Transactional
    public DoctorsDTO filterDoctorsByNameAndSpecialtyAndTime(String name, String time, String specialty) {
        List<Doctor> doctorList = doctorRepository.findByNameAndTimeAndSpecialty(name, time, specialty);

        return DoctorsDTOMapper.fromDoctorList(doctorList, time);
    }
}
