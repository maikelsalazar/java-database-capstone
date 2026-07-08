import { createDoctorCardList } from "./doctorCardList.js";
import { getDoctors, filterDoctors } from '../services/doctorServices.js';
import { debounce } from '../utils/debounce.js';

const doctorView = {
  container: null,
  filterByName: null,
  filterByTime: null,
  filterBySpecialty: null,
};

function initDoctorListView({
  container,
  filterByName,
  filterTime,
  filterSpecialty
}) {

  if (!container) {
    throw new Error("DoctorListView requires a container");
  }

  doctorView.container = container;
  doctorView.filterByName = filterByName;
  doctorView.filterTime = filterTime;
  doctorView.filterSpecialty = filterSpecialty;

  const debouncedFilter = debounce(filterDoctorsOnChange, 500);

  doctorView.filterByName?.addEventListener("input", debouncedFilter);
  doctorView.filterTime?.addEventListener("change", filterDoctorsOnChange);
  doctorView.filterSpecialty?.addEventListener("change", filterDoctorsOnChange);
}

async function renderDoctorCards(promise, type) {
  const message = document.createElement("p");
  const isAll = type === "all";

  message.textContent = isAll
      ? "Loading..."
      : "Searching...";
  doctorView.container.replaceChildren(message);

  try {
    const doctors = await promise;

    doctorView.container.replaceChildren(
      createDoctorCardList(doctors, type)
    );
  } catch(error) {
    console.error(isAll
      ? "Failed to load doctors:"
      : "Failed to filter doctors:",
      error
    );

    const errorMessage = document.createElement("p");
    errorMessage.textContent = isAll
      ? "Unable to load doctors."
      : "Unable to search doctors.";

    doctorView.container.replaceChildren(errorMessage);
  }
}

function resetFilterForm() {
  const { filterByName, filterTime, filterSpecialty } = doctorView;

  if(filterByName) filterByName.value = "";
  if(filterTime) filterTime.value = "*";
  if(filterSpecialty) filterSpecialty.value = "*";
}

function loadDoctorCards() {
  return renderDoctorCards(
    getDoctors(),
    "all"
  );
}

function filterDoctorsOnChange() {
  const name = doctorView.filterByName?.value.trim() ?? "";
  const time = doctorView.filterTime?.value ?? "*";
  const specialty = doctorView.filterSpecialty?.value ?? "*";

  return renderDoctorCards(
    filterDoctors(name, time, specialty),
    "filter"
  );
}

export {
  initDoctorListView,
  loadDoctorCards,
  resetFilterForm
}
