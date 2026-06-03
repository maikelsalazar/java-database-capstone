package com.project.back_end.repo;

import com.project.back_end.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Doctor findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
        SELECT DISTINCT d FROM Doctor d
        JOIN d.availableTimes t
        WHERE (
            :time = 'AM' AND CAST(SUBSTRING(t, 1, 2) AS int) < 12
        )
        OR (
            :time = 'PM' AND CAST(SUBSTRING(t, 1, 2) AS int) >= 12
        )
    """)
    List<Doctor> findByTime(@Param("time") String time);

    @Query("""
            SELECT DISTINCT d FROM Doctor d
            WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))
            """)
    List<Doctor> findByName(@Param("name") String name);

    @Query("""
            SELECT DISTINCT d FROM Doctor d
            WHERE LOWER(d.specialty) LIKE LOWER(CONCAT('%', :specialty, '%'))
            """)
    List<Doctor> findBySpecialty(@Param("specialty") String specialty);

    @Query("""
            SELECT DISTINCT d FROM Doctor d
            WHERE LOWER(d.specialty) LIKE LOWER(CONCAT('%', :specialty, '%'))
            AND LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))
            """)
    List<Doctor> findByNameAndSpecialty(@Param("name") String name, @Param("specialty") String specialty);

    @Query("""
            SELECT DISTINCT d FROM Doctor d
            JOIN d.availableTimes t
            WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))
            AND (
                    (:time = 'AM' AND CAST(SUBSTRING(t, 1, 2) AS int) < 12)
                    OR
                    (:time = 'PM' AND CAST(SUBSTRING(t, 1, 2) AS int) >= 12)
                )
            """)
    List<Doctor> findByNameAndTime(String name, String time);

    @Query("""
            SELECT DISTINCT d FROM Doctor d
            JOIN d.availableTimes t
            WHERE LOWER(d.specialty) LIKE LOWER(CONCAT('%', :specialty, '%'))
            AND (
                    (:time = 'AM' AND CAST(SUBSTRING(t, 1, 2) AS int) < 12)
                    OR
                    (:time = 'PM' AND CAST(SUBSTRING(t, 1, 2) AS int) >= 12)
                )
            """)
    List<Doctor> findByTimeAndSpecialty(@Param("time") String time, @Param("specialty") String specialty);

    @Query("""
            SELECT DISTINCT d FROM Doctor d
            JOIN d.availableTimes t
            WHERE LOWER(d.specialty) LIKE LOWER(CONCAT('%', :specialty, '%'))
            AND LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))
            AND (
                    (:time = 'AM' AND CAST(SUBSTRING(t, 1, 2) AS int) < 12)
                    OR
                    (:time = 'PM' AND CAST(SUBSTRING(t, 1, 2) AS int) >= 12)
                )
            """)
    List<Doctor> findByNameAndTimeAndSpecialty(@Param("name") String name, @Param("time") String time, @Param("specialty") String specialty);

    @Query("""
            SELECT COUNT(t) > 0
            FROM Doctor d
            JOIN d.availableTimes t
            WHERE d.id = :id
            AND t LIKE CONCAT(:time, '-%')
            """)
    boolean hasAvailability(@Param("id") Long id, @Param("time") String time);
}