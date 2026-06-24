// config.js

/**
 * Configuration file for defining global constants and environment-specific settings.
 * 
 * API_BASE_URL:
 * - Base URL for all API requests made from the frontend.
 * - Easily switchable for different environments (development, staging, production).
 * 
 * Example usage:
 *   fetch(`${API_BASE_URL}/api/appointments`)
 */

// Base url
export const API_BASE_URL = "http://localhost:8080/api";

// Login
export const LOGIN_ADMIN_URL = `${API_BASE_URL}/admin/login`;
export const LOGIN_DOCTOR_URL = `${API_BASE_URL}/doctor/login`;

// APIs
export const DOCTOR_API = `${API_BASE_URL}/doctor`;
export const PATIENT_API = `${API_BASE_URL}/patient`;
export const PRESCRIPTION_API = `${API_BASE_URL}/prescription`;
export const APPOINTMENT_API = `${API_BASE_URL}/appointments`;
