export function createDoctorCard(doctor) {
    const card = document.createElement("div");
    card.classList.add("doctor-card");

    const doctorInfoDiv = document.createElement("div");
    doctorInfoDiv.classList.add("doctor-info");

    const doctorName = document.createElement("h3")
    doctorName.textContent = doctor.name;

    const doctorSpecialty = document.createElement("p");
    doctorSpecialty.textContent = doctor.specialty;

    const email = document.createElement("p");
    email.textContent = `Email: ${doctor.email}`;

    const phone = document.createElement("p");
    phone.textContent = `Phone: ${doctor.phone}`;

    const availableTimes = document.createElement("ul");
    availableTimes.classList.add("available-times");

    doctor.availableTimes?.forEach(time => {
        const item = document.createElement("li");
        item.textContent = time;
        availableTimes.appendChild(item);
    });

    doctorInfoDiv.append(doctorName, doctorSpecialty, email, phone, availableTimes);

    card.appendChild(doctorInfoDiv);

    return card;
}
