// util.js
function setRole(role) {
    localStorage.setItem("userRole", role);
}

function getRole() {
    return localStorage.getItem("userRole");
}

function clearRole() {
    localStorage.removeItem("userRole");
}

function setToken(token) {
    return localStorage.setItem("token", token);
}

function getToken() {
    return localStorage.getItem("token");
}

function setTokenAndRole(token, role) {
    localStorage.setItem("token", token);
    localStorage.setItem("userRole", role);
    selectRole(role); // from ./js/render.js
}

export {
 setRole,
 getRole,
 clearRole,
 setToken,
 getToken,
 setTokenAndRole
}
