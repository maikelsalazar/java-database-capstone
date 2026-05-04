package com.project.back_end.services;

import com.project.back_end.DTO.DoctorDTO;
import com.project.back_end.DTO.DoctorsDTO;
import com.project.back_end.mappers.DoctorMapper;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public DoctorsDTO findAllDoctor() {
        List<Doctor> doctorList = doctorRepository.findAll();

        List<DoctorDTO> doctors = doctorList.stream()
                .map(DoctorMapper::toDTO)
                .toList();

        return new DoctorsDTO(doctors);
    }
}
