package com.project.back_end.DTO;

public record PatientDTO (Long id,
                          String name,
                          String email,
                          String phone,
                          String address) {
}
