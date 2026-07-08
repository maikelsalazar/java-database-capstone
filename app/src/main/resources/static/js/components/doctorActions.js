import { loadDoctorCards, resetFilterForm } from './doctorListView.js';
import { saveDoctor, updateDoctor, deleteDoctor } from '../services/doctorServices.js';

export async function addDoctorHandler() {
  document.querySelectorAll("[id$='Message']").forEach(elem => {
    elem.textContent = "";
  });

  // doctor's information
  const name = document.getElementById("doctorName").value.trim();
  const specialty = document.getElementById("specialty").value.trim();
  const email = document.getElementById("doctorEmail").value.trim();
  const password = document.getElementById("doctorPassword").value.trim();
  const phone = document.getElementById("doctorPhone").value.trim();
  const availabilityTimes = Array.from(
    document.querySelectorAll('input[name="availability"]:checked')
  ).map(checkbox => checkbox.value);

  const globalErrorMessage = document.getElementById("globalErrorMessage");

  if (!name ||
      !specialty ||
      !email ||
      !password ||
      !phone ||
      availabilityTimes.length === 0
    ) {
    globalErrorMessage.textContent = "All fields are required";
    return;
  }

  const result = await saveDoctor(
      name,
      specialty,
      email,
      password,
      phone,
      availabilityTimes
  );

  if (!result.success) {
    globalErrorMessage.textContent = result.message;

    Object.entries(result.errors).forEach(([field, message]) => {
      const fieldMessage = document.getElementById(`${field}Message`);
      if (fieldMessage) {
        fieldMessage.textContent = `${field}: ${message}`;
      }
    });

    return;
  }

  document.getElementById('modal-body').innerHTML = "<p class='success'>Doctor added successfully</p>";

  resetFilterForm();
  loadDoctorCards();

  setTimeout(() => {
      document.getElementById("modal").style.display = "none";
  }, 3000);
};

export async function updateDoctorHandler() {
  const id = this.dataset.id;
  const name = document.getElementById("doctorName").value.trim();
  const specialty = document.getElementById("specialty").value.trim();
  const phone = document.getElementById("doctorPhone").value.trim();
  const availableTimes = Array.from(
    document.querySelectorAll('input[name="availability"]:checked')
  ).map(checkbox => checkbox.value);


  const doctorToUpdate = { id, name, specialty, phone, availableTimes };
  const globalErrorMessage = document.getElementById("globalErrorMessage");

  const result = await updateDoctor(doctorToUpdate);

  if (!result.success) {
    globalErrorMessage.textContent = result.message;

    Object.entries(result.errors).forEach(([field, message]) => {
      const fieldMessage = document.getElementById(`${field}Message`);
      if (fieldMessage) {
        fieldMessage.textContent = `${field}: ${message}`;
      }
    });

    return;
  }

  document.getElementById('modal-body').innerHTML = "<p class='success'>Doctor updated successfully</p>";

  resetFilterForm();
  loadDoctorCards();

  setTimeout(() => {
      document.getElementById("modal").style.display = "none";
  }, 3000);
};

export async function deleteDoctorHandler() {
  if (confirm("Are you sure you want to delete this doctor")) {
      const { success, message } = await deleteDoctor(this.dataset.id);

      if (success) {
        document.getElementById("doctorCard" + this.dataset.id)?.remove();
      } else {
        alert(message);
      }
  }
};
