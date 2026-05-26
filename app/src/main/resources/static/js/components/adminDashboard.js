import { getDoctors, saveDoctor } from '../services/doctorServices.js';
import { createDoctorCard } from './doctorCard.js';

function adminResetFilterForm() {
    document.getElementById("searchBar").value = "";
    document.getElementById("filterTime").value = "*";
    document.getElementById("filterSpecialty").value = "*";
};

export async function adminLoadDoctorCards() {
  getDoctors()
    .then(doctors => {
      const contentDiv = document.getElementById("content");
      contentDiv.innerHTML = "";

      doctors.forEach(doctor => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
      });
    })
    .catch(error => {
      console.error("Failed to load doctors:", error);
    });
};

export async function adminAddDoctor() {
    // doctor's information
    const name = document.getElementById("doctorName").value.trim();
    const specialty = document.getElementById("specialty").value.trim();
    const email = document.getElementById("doctorEmail").value.trim();
    const password = document.getElementById("doctorPassword").value.trim();
    const phone = document.getElementById("doctorPhone").value.trim();
    const availabilityTimes = Array.from(
      document.querySelectorAll('input[name="availability"]:checked')
    ).map(checkbox => checkbox.value);

    if (name == "" || specialty == "" || email == "" || password == "" || phone == "" || availabilityTimes.length == 0) {
        const globalErrorMessage = document.getElementById("globalErrorMessage");
        globalErrorMessage.textContent = "All fields are required";
        return;
    }

    const saved = await saveDoctor(
        name,
        specialty,
        email,
        password,
        phone,
        availabilityTimes
    );

    if (saved) {
        document.getElementById('modal-body').innerHTML =
            "<p class='success'>Doctor added successfully</p>";

        adminResetFilterForm();
        adminLoadDoctorCards();

        setTimeout(() => {
            document.getElementById("modal").style.display = "none";
        }, 3000);
    }
};
