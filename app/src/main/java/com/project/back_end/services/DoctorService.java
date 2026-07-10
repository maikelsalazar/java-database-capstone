package com.project.back_end.services;

import com.project.back_end.DTO.DoctorCreateDTO;
import com.project.back_end.DTO.DoctorDTO;
import com.project.back_end.DTO.DoctorProfileUpdateDTO;
import com.project.back_end.enums.AvailableTime;
import com.project.back_end.exceptions.DoctorNotFoundException;
import com.project.back_end.exceptions.DuplicateEmailException;
import com.project.back_end.mappers.DoctorMapper;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;

    public DoctorService(DoctorRepository doctorRepository, AppointmentRepository appointmentRepository, PasswordEncoder passwordEncoder) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void saveDoctor(DoctorCreateDTO dto) {
        if (doctorRepository.existsByEmail(dto.email())) {
            throw new DuplicateEmailException();
        }

        Doctor doctor = DoctorMapper.fromCreateDTO(dto, passwordEncoder);
        doctorRepository.save(doctor);
    }

    @Transactional
    public void updateDoctor(DoctorProfileUpdateDTO dto) {
        Doctor doctor = doctorRepository
                .findById(dto.id())
                .orElseThrow(DoctorNotFoundException::new);

        doctor.setName(dto.name());
        doctor.setSpecialty(dto.specialty());
        doctor.setPhone(dto.phone());
        doctor.setAvailableTimes(dto.availableTimes());
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

    public Doctor validateDoctor(String email, String password) {

        Doctor doctor = doctorRepository.findByEmail(email);

        if (doctor == null) {
            return null;
        }

        if (!passwordEncoder.matches(password, doctor.getPassword())) {
            return null;
        }

        return doctor;
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

    public List<AvailableTime> getDoctorAvailability(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(DoctorNotFoundException::new);

        List<AvailableTime> doctorAvailableTimes = new ArrayList<>(
                doctor.getAvailableTimes()
        );

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Appointment> appointments =
                appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);

        Set<AvailableTime> bookedTimes = appointments.stream()
                .map(appointment -> AvailableTime.fromStartTime(appointment.getAppointmentTime()))
                .collect(Collectors.toSet());

        doctorAvailableTimes.removeAll(bookedTimes);

        return doctorAvailableTimes;
    }
}
