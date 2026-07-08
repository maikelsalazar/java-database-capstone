import { bookAppointment } from '../services/appointmentRecordService.js';

export function showBookingOverlay(e, doctor, patient) {
  const button = e.target;
  const rect = button.getBoundingClientRect();
  const ripple = document.createElement("div");
  ripple.classList.add("ripple-overlay");
  ripple.style.left = `${e.clientX}px`;
  ripple.style.top = `${e.clientY}px`;
  document.body.appendChild(ripple);

  setTimeout(() => ripple.classList.add("active"), 50);

  const modalApp = document.createElement("div");
  modalApp.classList.add("modalApp");

  modalApp.innerHTML = `
    <h2>Book Appointment</h2>
    <span id="globalErrorMessage" class="error"></span>
    <input class="input-field" name="patient" type="text" value="${patient.name}" disabled />
    <span id="patientMessage" class="error"></span>
    <input class="input-field" name="doctor" type="text" value="${doctor.name}" disabled />
    <span id="doctorMessage" class="error"></span>
    <input class="input-field" name="specialty" type="text" value="${doctor.specialty}" disabled/>
    <input class="input-field" name="email" type="email" value="${doctor.email}" disabled/>
    <span id="appointmentTimeMessage" class="error"></span>
    <input class="input-field" id="appointment-date" type="date" id="appointment-date" />
    <span id="dateMessage" class="error"></span>
    <select class="input-field" id="appointment-time">
      <option value="">Select time</option>
      ${doctor.availableTimes.map(t => `<option value="${t}">${t}</option>`).join('')}
    </select>
    <span id="timeMessage" class="error"></span>
    <br />
    <button class="confirm-booking">Confirm Booking</button>
  `;

  document.body.appendChild(modalApp);

  setTimeout(() => modalApp.classList.add("active"), 600);

  modalApp.querySelector(".confirm-booking").addEventListener("click", async () => {
    document.querySelectorAll("[id$='Message']").forEach(elem => {
      elem.textContent = "";
    });

    const date = modalApp.querySelector("#appointment-date").value.trim();
    const time = modalApp.querySelector("#appointment-time").value.trim();
    const token = localStorage.getItem("token");
    const startTime = time.split('-')[0];
    const appointmentTime = `${date}T${startTime}:00`;

    if (date === "") {
      document.getElementById("dateMessage").textContent = "Select a date";
      return;
    }

    if (time === "") {
      document.getElementById("timeMessage").textContent = "Select a time";
      return;
    }

    const appointment = {
        doctor: { id: doctor.id },
        patient: { id: patient.id },
        appointmentTime: `${date}T${startTime}:00`,
        status: 0
    };

    const result = await bookAppointment(appointment, token);
    console.log(result);

    if (!result.success) {
      const entries = Object.entries(result?.errors ?? {});
      if (entries.length > 0) {
        document.getElementById("globalErrorMessage").textContent = "Validation errors";
        entries.forEach(([field, message]) => {
          const fieldMessage = document.getElementById(`${field}Message`);
          if (fieldMessage) {
            console.log(message);
            fieldMessage.textContent = `${field}: ${message}`;
          }
        });
      } else if (result.message) {
        document.getElementById("globalErrorMessage").textContent = result.message;
      } else {
        document.getElementById("globalErrorMessage").textContent = "Unexpected error while booking appointment";
      }

      return;
    }

    alert("Appointment Booked successfully");
    ripple.remove();
    modalApp.remove();
  });
}
