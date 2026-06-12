import { createDoctorCard } from './doctorCard.js';

export function createDoctorCardList(doctors, type = "all") {
  const fragment = document.createDocumentFragment();

  if (doctors.length === 0) {
    const message = document.createElement("p");
    message.textContent = type === "filter"
        ? "No doctors found with the given filters."
        : "No doctors found.";

    fragment.appendChild(message);
    return fragment;
  }

  doctors.forEach(doctor => {
    fragment.appendChild(createDoctorCard(doctor));
  });

  return fragment;
}
