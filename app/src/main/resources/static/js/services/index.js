import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";
import { setTokenAndRole } from "../util.js";

const ADMIN_API = `${API_BASE_URL}/admin/login`;

const adminLogin = document.getElementById("adminLoginBtn");

if (adminLogin) {
    adminLogin.addEventListener("click", () => openModal("adminLogin"));
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
          loginMessage.classList.add("error");
          loginMessage.textContent = "Error: " + error;
          return;
        }

        const data = await response.json();
        setTokenAndRole(data.token, "admin");
        selectRole("admin");

    } catch(error) {
        console.error("Error occurred: " + error);
        loginMessage.classList.add("error");
        loginMessage.textContent = "Error: " + error;
    }
};
