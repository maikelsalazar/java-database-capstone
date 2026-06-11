import { getAllAppointments } from './services/appointmentRecordService.js';
import { createPatientRow } from './components/patientRows.js';
import { getToken } from './util.js';

const tableBody = document.getElementById("patientTableBody");
const searchBar = document.getElementById("searchBar");
const datePicker = document.getElementById("datePicker");
const todayButton = document.getElementById("todayButton");
const token = getToken();

let selectedDate = new Date().toISOString().split('T')[0];
let patientName = null;

datePicker.value = selectedDate;

searchBar.addEventListener("input", async (event) => {
    const value = event.target.value.trim();

    patientName = value !== "" ? value : null;

    await loadAppointments();
});

todayButton.addEventListener("click", async () => {
    selectedDate = new Date().toISOString().split("T")[0];

    datePicker.value = selectedDate;

    await loadAppointments();
});

datePicker.addEventListener("change", async (event) => {
    selectedDate = event.target.value;

    await loadAppointments();
});

async function loadAppointments() {
    try {
        const response = await getAllAppointments(selectedDate, patientName, token);

        const appointments = response.appointments ?? [];
        tableBody.innerHTML = "";

        if (appointments.length == 0) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="5">
                        No appointments found.
                    </td>
                </tr>
            `;
            return;
        }

        appointments.forEach((appointment) => {
            const patient = {
                id: appointment.patientId,
                name: appointment.patientName,
                phone: appointment.patientPhone,
                email: appointment.patientEmail
            };

            const row = createPatientRow(
                patient,
                appointment.appointmentId,
                appointment.doctorId);

            tableBody.appendChild(row);
        });

    } catch(error) {
        console.log(error);
        tableBody.innerHTML = `
            <tr>
                <td colspan="5">
                    Error loading appointments. Try again later.
                </td>
            </tr>
        `;
    }
};

document.addEventListener("DOMContentLoaded", async () => {
    renderContent();
    await loadAppointments();
});
