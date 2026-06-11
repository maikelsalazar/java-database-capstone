import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";

const ADMIN_API = `${API_BASE_URL}/admin/login`;
const DOCTOR_API = `${API_BASE_URL}/doctors/login`;

document.addEventListener("DOMContentLoaded", () => {
    const adminLogin = document.getElementById("adminLoginBtn");
    const doctorLogin = document.getElementById("doctorLoginBtn");


    if (adminLogin) {
        adminLogin.addEventListener("click", () => openModal("adminLogin"));
    }

    if (doctorLogin) {
        doctorLogin.addEventListener("click", () => openModal("doctorLogin"));
    }
});

function showMessage(type, message) {
    const messageElement = document.getElementById("message");

    if (!messageElement) {
        console.error("Message element does not exist");
        return;
    }

    messageElement.className = type;
    messageElement.textContent = message;
}

export async function adminLoginHandler() {
    const usernameInput = document.getElementById("username");
    const passwordInput = document.getElementById("password");

    const username = usernameInput?.value.trim() || "";
    const password = passwordInput?.value.trim() || "";

    if (username.length < 3 || username.length > 100) {
        showMessage("warning", "Username must have between 3 and 100 chars");
        return;
    }

    if (password.length < 8 || password.length > 15) {
        showMessage("warning", "Password must have between 8 and 15 chars");
        return;
    }

    const adminCredentials = { username, password };

    try {
        const response = await fetch(ADMIN_API, {
          method: "POST",
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify(adminCredentials)
        });

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
        setTokenAndRole(data.token, "admin");
    } catch(error) {
        console.error("Error occurred: " + error);
        showMessage("error", error?.message || "Network error");
    }
};

export async function doctorLoginHandler() {
    const tryEmail = document.getElementById("email")?.value;
    const tryPassword = document.getElementById("password")?.value;

    if (tryEmail === undefined) {
        console.log("Email field does not exist");
        return;
    }

    if (tryPassword === undefined) {
        console.log("Password field does not exist");
        return;
    }

    const email = tryEmail.trim();
    const password = tryPassword.trim();

    const loginMessage = document.getElementById("loginMessage");

    if (email === "" || password === "") {
        loginMessage.classList.add("error");
        loginMessage.textContent = "Please enter both email and password";
        return;
    }

    const credentials = { email, password };

    try {
        const response = await fetch(DOCTOR_API, {
          method: "POST",
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify(credentials)
        });

        if (!response.ok) {
          const result = await response.json();
          loginMessage.classList.add("error");
          loginMessage.textContent = result.message;
          return;
        }

        const data = await response.json();
        setTokenAndRole(data.token, "doctor");
    } catch(error) {
        console.error("Error occurred: " + error);
        loginMessage.classList.add("error");
        loginMessage.textContent = "Error: " + error;
    }
};

function setTokenAndRole(token, role) {
    localStorage.setItem("token", token);
    localStorage.setItem("userRole", role);
    selectRole(role);
}