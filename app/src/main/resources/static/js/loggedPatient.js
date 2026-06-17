// loggedPatient.js 
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
});
