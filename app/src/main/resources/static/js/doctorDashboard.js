import { getAllAppointments } from './services/appointmentRecordService.js';
import { createPatientRow } from './components/patientRows.js';
import { debounce } from './utils/debounce.js';
import { getToken } from './util.js';

const tableBody = document.getElementById("patientTableBody");
const searchBar = document.getElementById("searchBar");
const datePicker = document.getElementById("datePicker");
const todayButton = document.getElementById("todayButton");
const token = getToken();

datePicker.value = getTodayDate();

function getTodayDate() {
  return new Date().toISOString().split("T")[0];
}

async function handleSearchFilterChange() {
  await loadAppointments();
}

async function handleTodayButtonClick() {
  const today = getTodayDate();

  if (datePicker.value === today) {
    return;
  }

  datePicker.value = today;
  await loadAppointments();
}

async function handleDatePickerChange() {
  await loadAppointments();
}

document.addEventListener("DOMContentLoaded", initializePage);

async function initializePage() {
  renderContent();

  searchBar.addEventListener("input", debounce(handleSearchFilterChange, 500));
  todayButton.addEventListener("click", handleTodayButtonClick);
  datePicker.addEventListener("change", handleDatePickerChange);

  await loadAppointments();
}

function renderAppointments(appointments) {
  tableBody.innerHTML = "";
  if (appointments.length === 0) {
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
      appointment.doctorId
    );

    tableBody.appendChild(row);
  });
}

async function loadAppointments() {
  try {
    const selectedDate = datePicker.value;
    const patientName = searchBar.value.trim() || null;

    const response = await getAllAppointments(
      selectedDate,
      patientName,
      token
    );

    renderAppointments(response?.appointments ?? []);
  } catch(error) {
    console.error(error);
    tableBody.innerHTML = `
        <tr>
            <td colspan="5">
                Error loading appointments. Try again later.
            </td>
        </tr>
    `;
  }
}
