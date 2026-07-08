import { DOCTOR_API } from "../config/config.js"

async function fetchDoctors(url) {
  try {
  const response = await fetch(url);

  if (!response.ok) {
    throw new Error("Failed to load doctors")
  }

  const data = await response.json();

  return data.doctors || [];
  } catch(error) {
    console.log(error);
    throw error;
  }
}

export function filterDoctors(name, time, specialty) {
  const filterName = name.length > 0 ? name : "*";
  const filterTime = time.length > 0 ? time : "*";
  const filterSpecialty = specialty.length > 0 ? specialty : "*";

  if (filterName === "*" && filterTime === "*" && filterSpecialty === "*") {
    return getDoctors();
  }

  const filterDoctorsUrl = `${DOCTOR_API}/filter/${filterName}/${filterTime}/${filterSpecialty}`;

  return fetchDoctors(filterDoctorsUrl);
}

export function getDoctors() {
  const fetchAllDoctorsUrl = `${DOCTOR_API}/list`;

  return fetchDoctors(fetchAllDoctorsUrl);
};

export async function saveDoctor(name, specialty, email, password, phone, availableTimes) {
    const token = localStorage.getItem("token");

    const doctor = { name, specialty, email, password, phone, availableTimes };

    try {
        const response = await fetch(`${DOCTOR_API}/${token}`, {
          method: "POST",
          headers: {
           "Content-Type": "application/json"
          },
          body: JSON.stringify(doctor),
        });

        if (!response.ok) {
            switch(response.status) {
                case 400:
                    const { errors } = await response.json();

                    return {
                      success: false,
                      message: "Validation fails",
                      errors: errors,
                    };

                case 401:
                    throw new Error("Forbidden");

                default:
                    throw new Error("Unknown Error");
            }
        }

        return {
          success: true,
          message: "",
          errors: {}
        };
    } catch(error) {
       return {
        success: false,
        message:
          error instanceof TypeError
          ? "Cannot connect to server"
          : error.message,
        errors: {}
       };
    }
};

export async function updateDoctor(doctor) {
  const token = localStorage.getItem("token");

  try {
      const response = await fetch(`${DOCTOR_API}/${token}`, {
        method: "PUT",
        headers: {
         "Content-Type": "application/json"
        },
        body: JSON.stringify(doctor),
      });

      if (!response.ok) {
          switch(response.status) {
              case 400:
                  const { errors } = await response.json();

                  return {
                    success: false,
                    message: "Validation fails",
                    errors: errors,
                  };

              case 401:
                  throw new Error("Forbidden");

              default:
                  throw new Error("Unknown Error");
          }
      }

      return {
        success: true,
        message: "",
        errors: {}
      };
  } catch(error) {
     return {
      success: false,
      message:
        error instanceof TypeError
        ? "Cannot connect to server"
        : error.message,
      errors: {}
     };
  }
}

export async function deleteDoctor(id) {
    const token = localStorage.getItem("token");

    try {
      const response = await fetch(`${DOCTOR_API}/${id}/${token}`, {
        method: "DELETE",
        headers: {
         "Content-Type": "application/json"
        }
      });

      if (response.ok) {
        return {
          success: true,
          message: "Doctor deleted successfully",
          errors: {}
        };
      }

      const { message } = await response.json();

      throw new Error(message);

    } catch(error) {
      console.error(error);

      return {
        success: false,
        message:
          error instanceof TypeError
          ? "Cannot connect to server"
          : error.message,
          errors: {}
      };
    }
};
