import { API_BASE_URL } from "../config/config.js"

const DOCTOR_API = `${API_BASE_URL}/doctors`

export async function filterDoctors(name, time, specialty) {
    try {
        const response = await fetch(`${DOCTOR_API}/filter/${name}/${time}/${specialty}`);

        const data = await response.json();

        return data.doctors || [];
    } catch(error) {
        console.log("Error fetching doctors", error)
        return [];
    }
}

export async function getDoctors() {
    try {
        const response = await fetch(`${DOCTOR_API}/list`);

        const data = await response.json();

        return data.doctors || [];
    } catch(error) {
        console.log("Error fetching doctors", error)
        return [];
    }
};

export async function saveDoctor(name, specialty, email, password, phone, availableTimes) {
    const token = localStorage.getItem("token");

    const doctor = { name, specialty, email, password, phone, availableTimes };
    const globalErrorMessage = document.getElementById("globalErrorMessage");

    try {
        const response = await fetch(`${DOCTOR_API}/${token}`, {
          method: "POST",
          headers: {
           "Content-Type": "application/json"
          },
          body: JSON.stringify(doctor),
        });

        if (!response.ok) {
            switch(response.status) {
                case 400:
                    const { success, errors } = await response.json();

                    Object.entries(errors).forEach(([field, message]) => {
                      const fieldMessage = document.getElementById(field + "Message");
                      fieldMessage.textContent = message;
                    });
                break;
                case 401:
                    globalErrorMessage.textContent = "Forbidden";
                break;
                default:
                    globalErrorMessage.textContent = "Unknown error";
                break;
            }
            return false;
        }

        return true;
    } catch(error) {
        if (error instanceof TypeError) {
          globalErrorMessage.textContent = "Cannot connect to server";
        } else {
          globalErrorMessage.textContent = "Unexpected internal error";
       }

       return false;
    }
};

export async function deleteDoctor(id) {
    const token = localStorage.getItem("token");

    try {
            const response = await fetch(`${DOCTOR_API}/${id}/${token}`, {
              method: "DELETE",
              headers: {
               "Content-Type": "application/json"
              }
            });

            if (response.ok) return true;

            const { success, message } = await response.json();

            console.log(message);

            return false;
    } catch(error) {
        console.error(error);
        return false;
    }
};
