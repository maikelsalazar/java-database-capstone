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
