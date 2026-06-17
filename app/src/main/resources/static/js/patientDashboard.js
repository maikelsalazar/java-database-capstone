import { openModal } from './components/modals.js';
import { initDoctorListView, loadDoctorCards } from './components/doctorListView.js';
import { patientSignup, patientLogin } from './services/patientServices.js';
import { setTokenAndRole } from './util.js';


document.addEventListener("DOMContentLoaded", () => {
  initDoctorListView({
    container: document.getElementById("content"),
    filterByName: document.getElementById("searchBar"),
    filterTime: document.getElementById("filterTime"),
    filterSpecialty: document.getElementById("filterSpecialty")
  });

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

window.signupPatient = async function () {
  // clean error messages
  document.querySelectorAll("[id$='Message']").forEach(elem => {
      elem.textContent = "";
  });

  // processing form
  const name = document.getElementById("name").value;
  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;
  const phone = document.getElementById("phone").value;
  const address = document.getElementById("address").value;

  const data = { name, email, password, phone, address };
  const response = await patientSignup(data);

  if (response.success) {
    document.getElementById('modal-body').innerHTML =
                "<p class='success'>" + response.message + "</p>";

    setTimeout(() => {
        document.getElementById("modal").style.display = "none";
    }, 3000);

    return;
  }

  if (response.message) {
    document.getElementById("globalErrorMessage").textContent = response.message;
    return;
  }

  Object.entries(response.errors || {}).forEach(([field, message]) => {
    const messageElem = document.getElementById(field + "Message");
    if (messageElem) messageElem.textContent = field + ": " + message;
  });
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
