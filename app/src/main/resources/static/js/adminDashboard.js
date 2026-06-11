// adminDashboard.js
import { openModal } from './components/modals.js';
import { createDoctorCard } from './components/doctorCard.js';
import { adminLoadDoctorCards } from './components/adminDashboard.js';
import { filterDoctors } from './services/doctorServices.js';
import { debounce } from './utils/debounce.js';

document.addEventListener("DOMContentLoaded", () => {
  renderContent();
  adminLoadDoctorCards();

  const btnAddDoctor = document.getElementById("addDocBtn");
  if (btnAddDoctor) {
    btnAddDoctor.addEventListener("click", () => openModal("addDoctor"));
  }
});

const debouncedFilter = debounce(filterDoctorsOnChange, 500);

// Filter Input
document.getElementById("searchBar").addEventListener("input", debouncedFilter);
document.getElementById("filterTime").addEventListener("change", filterDoctorsOnChange);
document.getElementById("filterSpecialty").addEventListener("change", filterDoctorsOnChange);

function filterDoctorsOnChange() {
  const searchBar = document.getElementById("searchBar").value.trim();
  const filterTime = document.getElementById("filterTime").value;
  const filterSpecialty = document.getElementById("filterSpecialty").value;

  const name = searchBar.length > 0 ? searchBar : "*";
  const time = filterTime.length > 0 ? filterTime : "*";
  const specialty = filterSpecialty.length > 0 ? filterSpecialty : "*";

  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "<p>Searching...</>"

  filterDoctors(name, time, specialty)
    .then(doctors => {
      const contentDiv = document.getElementById("content");
      contentDiv.innerHTML = "";

      if (doctors.length > 0) {
        doctors.forEach(doctor => {
          const card = createDoctorCard(doctor);
          contentDiv.appendChild(card);
        });
      } else {
        contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
      }
    })
    .catch(error => {
      console.error("Failed to filter doctors:", error);
      alert("❌ An error occurred while filtering doctors.");
    });
};
