// updateAppointment.js
import { initializeUpdateAppointmentForm } from './components/appointmentActions.js';

document.addEventListener("DOMContentLoaded", initializePage);

async function initializePage() {
  renderContent();

  const urlParams = new URLSearchParams(window.location.search);

  await initializeUpdateAppointmentForm ( {
    form: document.getElementById("updateAppointmentForm"),
    appointmentId: urlParams.get("appointmentId"),
    patientId: urlParams.get("patientId"),
    doctorId: urlParams.get("doctorId"),
    patientName: urlParams.get("patientName"),
    doctorName: urlParams.get("doctorName"),
    appointmentDate: urlParams.get("appointmentDate"),
    appointmentTime: urlParams.get("appointmentTime"),
  });
}
