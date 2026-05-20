// util.js
export function setRole(role) {
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

export function getToken(token) {
    return localStorage.getItem("token");
}

export function setTokenAndRole(token, role) {
    setToken(token);
    setRole(role);
}
