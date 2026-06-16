// adminDashboard.js
import { openModal } from './components/modals.js';
import { initDoctorListView, loadDoctorCards } from './components/doctorListView.js';

document.addEventListener("DOMContentLoaded", () => {
  renderContent();
  initDoctorListView({
    container: document.getElementById("content"),
    filterByName: document.getElementById("searchBar"),
    filterTime: document.getElementById("filterTime"),
    filterSpecialty: document.getElementById("filterSpecialty")
  });

  loadDoctorCards();

  const btnAddDoctor = document.getElementById("addDocBtn");
  if (btnAddDoctor) {
    btnAddDoctor.addEventListener("click", () => openModal("addDoctor"));
  }
});
