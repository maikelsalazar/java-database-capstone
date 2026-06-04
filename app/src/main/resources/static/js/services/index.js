import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";

const ADMIN_API = `${API_BASE_URL}/admin/login`;
const DOCTOR_API = `${API_BASE_URL}/doctors/login`;

const adminLogin = document.getElementById("adminLoginBtn");
const doctorLogin = document.getElementById("doctorLoginBtn");

if (adminLogin) {
    adminLogin.addEventListener("click", () => openModal("adminLogin"));
}

if (doctorLogin) {
    doctorLogin.addEventListener("click", () => openModal("doctorLogin"));
}

export async function adminLoginHandler() {
    const tryUsername = document.getElementById("username")?.value;
    const tryPassword = document.getElementById("password")?.value;

    if (tryUsername === undefined) {
        console.log("Username field does not exist");
        return;
    }

    if (tryPassword === undefined) {
        console.log("Password field does not exist");
        return;
    }

    const username = tryUsername.trim();
    const password = tryPassword.trim();

    const loginMessage = document.getElementById("loginMessage");

    if (username === "" || password === "") {
        loginMessage.classList.add("error");
        loginMessage.textContent = "Please enter both username and password";
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
          const result = await response.json();
          loginMessage.classList.add("error");
          loginMessage.textContent = result.message;
          return;
        }

        const data = await response.json();
        setTokenAndRole(data.token, "admin");
    } catch(error) {
        console.error("Error occurred: " + error);
        loginMessage.classList.add("error");
        loginMessage.textContent = "Error: " + error;
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
