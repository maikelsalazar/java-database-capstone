function renderHeader() {
  const headerDiv = document.getElementById("header");

  const isSelectRolePage = window.location.pathname.endsWith("/") ||
                window.location.pathname.endsWith("/index.html");

  if (isSelectRolePage) {
    localStorage.removeItem("userRole");
    localStorage.removeItem("token");
    headerDiv.innerHTML = `
      <header class="header">
        <div class="logo-section">
          <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
          <span class="logo-title">Hospital CMS</span>
        </div>
      </header>`;
    return;
  }

  const role = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");

  let headerContent = `<header class="header">
       <div class="logo-section">
         <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
         <span class="logo-title">Hospital CMS</span>
       </div>
       <nav>`;

  if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
      localStorage.removeItem("userRole");
      alert("Session expired or invalid login. Please log in again.");
      window.location.href = "/";
      return;
  }

  switch(role) {
    case "admin":
        headerContent += `
              <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
              <a href="#" onclick="logout()">Logout</a>`;
    break;
    case 'doctor':
        headerContent += `
            <button class="adminBtn"  onclick="selectRole('doctor')">Home</button>
            <a href="#" onclick="logout()">Logout</a>
        `;
    break;
    case 'patient':
        headerContent += `
            <button id="patientLogin" class="adminBtn">Login</button>
            <button id="patientSignup" class="adminBtn">Sign Up</button>
        `;
    break;
  }

  headerContent += `
        </nav>
        </header>
        `;

  headerDiv.innerHTML = headerContent;
}

function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");

    window.location.href = "/";
}

renderHeader();
