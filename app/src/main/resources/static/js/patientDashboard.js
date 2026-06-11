// patientDashboard.js
import { getDoctors } from './services/doctorServices.js';
import { openModal } from './components/modals.js';
import { createDoctorCard } from './components/doctorCard.js';
import { filterDoctors } from './services/doctorServices.js';
import { patientSignup, patientLogin } from './services/patientServices.js';
import { setTokenAndRole } from './util.js';

document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();

  const signupBtn = document.getElementById("patientSignup");
  const loginBtn = document.getElementById("patientLogin")

  if (signupBtn) {
    signupBtn.addEventListener("click", () => openModal("patientSignup"));
  }

  if (loginBtn) {
    loginBtn.addEventListener("click", () => {
      openModal("patientLogin")
    })
  }
});

function loadDoctorCards() {
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
}

let debounceTimer;

function debounce(callback, delay) {
  return function (...args) {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(() => {
      callback.apply(this, args);
    }, delay);
  };
}

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
        console.log("Nothing");
      }
    })
    .catch(error => {
      console.error("Failed to filter doctors:", error);
      alert("❌ An error occurred while filtering doctors.");
    });
}

window.signupPatient = async function () {
  try {
    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const phone = document.getElementById("phone").value;
    const address = document.getElementById("address").value;

    const data = { name, email, password, phone, address };
    const { success, message } = await patientSignup(data);
    if (success) {
      alert(message);
      document.getElementById("modal").style.display = "none";
      window.location.reload();
    }
    else alert(message);
  } catch (error) {
    console.error("Signup failed:", error);
    alert("❌ An error occurred while signing up.");
  }
};

function showMessage(type, message) {
    const messageElement = document.getElementById("message");

    if (!messageElement) {
        console.error("Message element does not exist");
        return;
    }

    messageElement.className = type;
    messageElement.textContent = message;
}

window.loginPatient = async function () {
  const emailInput = document.getElementById("email");
  const passwordInput = document.getElementById("password");

  const email = emailInput?.value.trim() || "";
  const password = passwordInput?.value.trim() || "";

  if (email.length < 3 || email.length > 100) {
      showMessage("warning", "Email must have between 3 and 100 chars");
      return;
  }

  if (password.length < 8 || password.length > 15) {
      showMessage("warning", "Password must have between 8 and 15 chars");
      return;
  }

  const credentials = { email, password };

  try {
    const response = await patientLogin(credentials);
    if (!response.ok) {
      let result = {};
      try {
        const text = await response.text();
        result = text ? JSON.parse(text) : {};
      } catch (e) {
        console.error("Failed to parse JSON response:", e);
        showMessage("error", "Invalid server response");
        return;
      }

      if (result.message) {
        showMessage("error", result.message);
        return;
      }

      const entries = Object.entries(result?.errors ?? {});

      if (entries.length > 0) {
        const message = entries
          .map(([field, error]) => `${field}: ${error}`)
          .join(", ");

        showMessage("error", message);
        return;
      }

      console.error(result);

      showMessage("error", "An unexpected error occurred");
      return;
    }

    const data = await response.json();
    setTokenAndRole(data.token, "loggedPatient");
    window.location.href = '/pages/loggedPatientDashboard.html';
  } catch (error) {
    console.error("Error occurred: " + error);
    showMessage("error", error?.message || "Network error");
  }
}
