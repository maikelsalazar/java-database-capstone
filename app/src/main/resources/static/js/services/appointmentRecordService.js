// appointmentRecordService.js
import { APPOINTMENT_API } from "../config/config.js";

//This is for the doctor to get all the patient Appointments
export async function getAllAppointments(date, patientName, token) {
  const hasPatientName =
      patientName != null && patientName.trim() !== "";

  const url = hasPatientName
    ? `${APPOINTMENT_API}/${date}/search/${encodeURIComponent(patientName)}/${token}`
    : `${APPOINTMENT_API}/${date}/${token}`;

  const response = await fetch(url);
  if (!response.ok) {
    throw new Error("Failed to fetch appointments");
  }

  return await response.json();
}

export async function bookAppointment(appointment, token) {
  try {
    const response = await fetch(`${APPOINTMENT_API}/${token}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(appointment)
    });

    const data = await response.json();
    return {
      success: response.ok,
      message: data.message || null,
      errors: data.errors || {}
    };
  } catch (error) {
    console.error("Error while booking appointment:", error);
    return {
      success: false,
      message: "Network error. Please try again later.",
      errors: {}
    };
  }
}

export async function updateAppointment(appointment, token) {
  try {
    const response = await fetch(`${APPOINTMENT_API}/${token}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(appointment)
    });

    const data = await response.json();
    return {
      success: response.ok,
      message: data.message || "Something went wrong",
      errors: data.errors || {}
    };
  } catch (error) {
    console.error("Error while updating appointment:", error);
    return {
      success: false,
      message: "Network error. Please try again later.",
      errors: {}
    };
  }
}
