/**
 * Converts a start time like "01:00" into a one-hour time slot
 * like "01:00-02:00".
 */
export function toAppointmentTimeSlot(startAppointmentTime) {
  const startTime = Number(startAppointmentTime.split(":")[0]);
  const endTime = (startTime + 1) % 24;

  return `${String(startTime).padStart(2, "0")}:00-${String(endTime).padStart(2, "0")}:00`;
}
