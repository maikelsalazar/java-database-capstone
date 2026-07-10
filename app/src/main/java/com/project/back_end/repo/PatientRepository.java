package com.project.back_end.repo;

import com.project.back_end.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailOrPhone(String email, String phone);

    Patient findByEmail(String email);

    Patient findByEmailOrPhone(String email, String phone);
}
