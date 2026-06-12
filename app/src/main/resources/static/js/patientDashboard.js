import { getDoctors } from './services/doctorServices.js';
import { openModal } from './components/modals.js';
import { createDoctorCardList } from './components/doctorCardList.js';
import { filterDoctors } from './services/doctorServices.js';
import { patientSignup, patientLogin } from './services/patientServices.js';
import { debounce } from './utils/debounce.js';
import { setTokenAndRole } from './util.js';

// Filter Input
const debouncedFilter = debounce(filterDoctorsOnChange, 500);

const searchBarInput = document.getElementById("searchBar");
const filterTimeSelect = document.getElementById("filterTime");
const filterSpecialtySelect = document.getElementById("filterSpecialty");

searchBarInput.addEventListener("input", debouncedFilter);
filterTimeSelect.addEventListener("change", filterDoctorsOnChange);
filterSpecialtySelect.addEventListener("change", filterDoctorsOnChange);

// doctor's card container
const doctorCardListContainer = document.getElementById("content");

document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();

  const signupBtn = document.getElementById("patientSignup");
  const loginBtn = document.getElementById("patientLogin")

  if (signupBtn) {
    signupBtn.addEventListener("click", () => openModal("patientSignup"));
  }

  if (loginBtn) {
    loginBtn.addEventListener("click", () => openModal("patientLogin"));
  }
});

async function renderDoctorCards(promise, type) {
  const message = document.createElement("p");
  const isAll = type === "all";

  message.textContent = isAll
      ? "Loading..."
      : "Searching...";
  doctorCardListContainer.replaceChildren(message);

  try {
    const doctors = await promise;

    doctorCardListContainer.replaceChildren(
      createDoctorCardList(doctors, type)
    );
  } catch(error) {
    console.error(isAll
      ? "Failed to load doctors:"
      : "Failed to filter doctors:",
      error
    );

    const errorMessage = document.createElement("p");
    errorMessage.textContent = isAll
      ? "Unable to load doctors."
      : "Unable to search doctors.";

    doctorCardListContainer.replaceChildren(errorMessage);
  }
}

function loadDoctorCards() {
  return renderDoctorCards(
    getDoctors(),
    "all"
  );
}

function filterDoctorsOnChange() {
  const name = searchBarInput.value.trim();
  const time = filterTimeSelect.value;
  const specialty = filterSpecialtySelect.value;

  return renderDoctorCards(
    filterDoctors(name, time, specialty),
    "filter"
  );
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
