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

  const role = getRole();

  let headerContent = `<header class="header">
       <div class="logo-section">
         <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
         <span class="logo-title">Hospital CMS</span>
       </div>
       <nav>`;

  switch(role) {
    case 'patient':
        headerContent += `
            <button id="patientLogin" class="adminBtn">Login</button>
            <button id="patientSignup" class="adminBtn">Sign Up</button>
        `;
    break
  }

  headerContent += `
        </nav>
        </header>
        `;

  headerDiv.innerHTML = headerContent;
}

renderHeader();
