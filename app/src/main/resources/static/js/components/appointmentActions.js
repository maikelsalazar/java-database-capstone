import { clearMessages, showMessages, showFieldMessage } from '../utils/formHelpers.js';
import { updateAppointment } from "../services/appointmentRecordService.js";
import { getDoctors } from "../services/doctorServices.js";
import { toAppointmentTimeSlot } from "../utils/toAppointmentTimeSlot.js";
import { getToken } from '../util.js';

const appointmentsUrl = "/pages/patientAppointments.html";

export async function initializeUpdateAppointmentForm({
  form,
  appointmentId,
  patientId,
  doctorId,
  patientName,
  doctorName,
  appointmentDate,
  appointmentTime
}) {

  const token = getToken();

  if (!form) {
    console.error("Update appointment form not found");
    return;
  }

  if (!token || !patientId) {
    form.innerHTML = '<p class="success">Missing session data, redirecting to appointments page.</p>';
    setTimeout(() => {
      window.location.href = appointmentsUrl; // Redirect back to the appointments page
    }, 3000);
    return;
  }

  const context = {
    token,
    appointmentId,
    patientId,
    doctorId
  };

  const appointmentTimeSlot = toAppointmentTimeSlot(appointmentTime);

  // get doctor to display only the available time of doctor
  try {
    const doctors = await getDoctors();
    const doctor = doctors.find(d => d.id === Number(doctorId));
    if (!doctor) {
      showFieldMessage("doctorMessage", "Doctor not found");
      return;
    }

    // Fill the form with the appointment data passed in the URL
    document.getElementById("patientName").value = patientName || "You";
    document.getElementById("doctorName").value = doctorName;
    document.getElementById("appointmentDate").value = appointmentDate;

    const timeSelect = document.getElementById("appointmentTime");
    doctor.availableTimes.forEach(time => {
      const option = document.createElement("option");
      option.value = time;
      option.textContent = time;
      timeSelect.appendChild(option);
    });
    timeSelect.value = appointmentTimeSlot;

    // Handle form submission for updating the appointment
    form.addEventListener("submit", (e) => handleUpdateAppointment(e, context));

  } catch(error) {
    console.error("Error fetching doctors:", error);
    showFieldMessage("globalErrorMessage", "❌ Failed to load doctor data.");
  }
}

async function handleUpdateAppointment(e, context) {
  e.preventDefault(); // Prevent default form submission

  const form = e.currentTarget;

  clearMessages();

  const {
    token,
    appointmentId,
    patientId,
    doctorId
  } = context;

  const date = document.getElementById("appointmentDate").value;
  const time = document.getElementById("appointmentTime").value;
  if (!date) {
    showFieldMessage("appointmentDateMessage", "Select a Date");
    return;
  }

  if (!time) {
    showFieldMessage("appointmentTimeMessage", "Select a Time");
    return;
  }
  const startTime = time.split('-')[0];

  const updatedAppointment = {
    id: appointmentId,
    doctor: { id: doctorId },
    patient: { id: patientId },
    appointmentTime: `${date}T${startTime}:00`,
    status: 0
  };

  try {
    const updateResponse = await updateAppointment(updatedAppointment, token);

    if (updateResponse.success) {
      form.innerHTML = '<p class="success">Appointment updated successfully!</p>';
      setTimeout(() => {
        window.location.href = appointmentsUrl; // Redirect back to the appointments page
      }, 3000);
    } else {
      showMessages(updateResponse);
    }
  } catch(error) {
    console.error(error);
    showFieldMessage(
      "globalErrorMessage",
      "Failed to update appointment."
    );
  }
}
