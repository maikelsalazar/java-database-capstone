// adminDashboard.js
import { openModal } from './components/modals.js';
import { adminLoadDoctorCards } from './components/adminDashboard.js';

document.addEventListener("DOMContentLoaded", () => {
  adminLoadDoctorCards();

  const btnAddDoctor = document.getElementById("addDocBtn");
  if (btnAddDoctor) {
    btnAddDoctor.addEventListener("click", () => openModal("addDoctor"));
  }
});
