package com.project.back_end.DTO;

import java.util.List;

public class DoctorsDTO {

    private List<DoctorDTO> doctors;

    public DoctorsDTO() {
    }

    public DoctorsDTO(List<DoctorDTO> doctors) {
        this.doctors = doctors;
    }

    public void setDoctors(List<DoctorDTO> doctors) {
        this.doctors = doctors;
    }

    public List<DoctorDTO> getDoctors() {
        return doctors;
    }
}
