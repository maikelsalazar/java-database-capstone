// adminDashboard.js
import { getDoctors } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';

document.addEventListener("DOMContentLoaded", () => {
    console.log("content loaded");
    loadDoctorCards();
});

function loadDoctorCards() {
  console.log("getting doctors");
  getDoctors()
    .then(doctors => {
      const contentDiv = document.getElementById("content");
      contentDiv.innerHTML = "";

      doctors.forEach(doctor => {
        console.log("Creating card for: " + doctor.name);
        const card = createDoctorCard(doctor);
        console.log(card);
        contentDiv.appendChild(card);
      });
    })
    .catch(error => {
      console.error("Failed to load doctors:", error);
    });
};