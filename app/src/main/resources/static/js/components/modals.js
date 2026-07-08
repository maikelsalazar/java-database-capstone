// modals.js

import { addDoctorHandler, updateDoctorHandler } from './doctorActions.js';
import { adminLoginHandler, doctorLoginHandler } from '../services/index.js';

function populateDoctorForm(doctor) {
    document.getElementById("updateDoctorBtn").dataset.id = doctor.id;
    document.getElementById("doctorName").value = doctor.name;
    document.getElementById("doctorPhone").value = doctor.phone;
    document.getElementById("specialty").value = doctor.specialty;

    document
      .querySelectorAll('input[name="availability"]')
      .forEach(checkbox => {
          checkbox.checked = doctor.availableTimes.includes(checkbox.value);
      });
}

export function openModal(type, doctor) {
  let modalContent = '';
  if (type === 'addDoctor') {
    modalContent = `
         <h2>Add Doctor</h2>
         <span id="globalErrorMessage" class="error"></span>
         <input type="text" id="doctorName" placeholder="Doctor Name" class="input-field">
         <span id="nameMessage" class="error"></span>
         <select id="specialty" class="input-field select-dropdown">
            <option value="">Specialization</option>
            <option value="cardiologist">Cardiologist</option>
            <option value="dermatologist">Dermatologist</option>
            <option value="neurologist">Neurologist</option>
            <option value="pediatrician">Pediatrician</option>
            <option value="orthopedic">Orthopedic</option>
            <option value="gynecologist">Gynecologist</option>
            <option value="psychiatrist">Psychiatrist</option>
            <option value="dentist">Dentist</option>
            <option value="ophthalmologist">Ophthalmologist</option>
            <option value="ent">ENT Specialist</option>
            <option value="urologist">Urologist</option>
            <option value="oncologist">Oncologist</option>
            <option value="gastroenterologist">Gastroenterologist</option>
            <option value="general">General Physician</option>
        </select>
        <span id="specialtyMessage" class="error"></span>
        <input type="email" id="doctorEmail" placeholder="Email" class="input-field">
        <span id="emailMessage" class="error"></span>
        <input type="password" id="doctorPassword" placeholder="Password" class="input-field">
        <span id="passwordMessage" class="error"></span>
        <input type="text" id="doctorPhone" placeholder="Mobile No." class="input-field">
        <span id="phoneMessage" class="error"></span>
        <div class="availability-container">
        <label class="availabilityLabel">Select Availability:</label>
          <span id="availabilityMessage" class="error"></span>
          <div class="checkbox-group">
            <label><input type="checkbox" name="availability" value="08:00-09:00">&nbsp;&nbsp;8:00 AM -  &nbsp;&nbsp;9:00 AM</label>
            <label><input type="checkbox" name="availability" value="09:00-10:00">&nbsp;&nbsp;9:00 AM - 10:00 AM</label>
            <label><input type="checkbox" name="availability" value="10:00-11:00">10:00 AM - 11:00 AM</label>
            <label><input type="checkbox" name="availability" value="11:00-12:00">11:00 AM - 12:00 PM</label>
            <label><input type="checkbox" name="availability" value="12:00-13:00">12:00 PM -  &nbsp;&nbsp;1:00 PM</label>
            <label><input type="checkbox" name="availability" value="13:00-14:00">&nbsp;&nbsp;1:00 PM - &nbsp;&nbsp;2:00 PM</label>
            <label><input type="checkbox" name="availability" value="14:00-15:00">&nbsp;&nbsp;2:00 PM - &nbsp;&nbsp;3:00 PM</label>
            <label><input type="checkbox" name="availability" value="15:00-16:00">&nbsp;&nbsp;3:00 PM - &nbsp;&nbsp;4:00 PM</label>
            <label><input type="checkbox" name="availability" value="16:00-17:00">&nbsp;&nbsp;4:00 PM - &nbsp;&nbsp;5:00 PM</label>
            <label><input type="checkbox" name="availability" value="17:00-18:00">&nbsp;&nbsp;5:00 PM - &nbsp;&nbsp;6:00 PM</label>
          </div>
        </div>
        <button class="dashboard-btn" id="saveDoctorBtn">Save</button>
      `;
  } else if (type === 'editDoctor' && doctor) {
    modalContent = `
     <h2>Edit Doctor</h2>
     <span id="globalErrorMessage" class="error"></span>
     <input type="text" id="doctorName" placeholder="Doctor Name" class="input-field">
     <span id="nameMessage" class="error"></span>
     <select id="specialty" class="input-field select-dropdown">
        <option value="">Specialization</option>
        <option value="cardiologist">Cardiologist</option>
        <option value="dermatologist">Dermatologist</option>
        <option value="neurologist">Neurologist</option>
        <option value="pediatrician">Pediatrician</option>
        <option value="orthopedic">Orthopedic</option>
        <option value="gynecologist">Gynecologist</option>
        <option value="psychiatrist">Psychiatrist</option>
        <option value="dentist">Dentist</option>
        <option value="ophthalmologist">Ophthalmologist</option>
        <option value="ent">ENT Specialist</option>
        <option value="urologist">Urologist</option>
        <option value="oncologist">Oncologist</option>
        <option value="gastroenterologist">Gastroenterologist</option>
        <option value="general">General Physician</option>
    </select>
    <span id="specialtyMessage" class="error"></span>
    <input type="text" id="doctorPhone" placeholder="Mobile No." class="input-field">
    <span id="phoneMessage" class="error"></span>
    <div class="availability-container">
    <label class="availabilityLabel">Select Availability:</label>
      <span id="availabilityMessage" class="error"></span>
      <div class="checkbox-group">
        <label><input type="checkbox" name="availability" value="08:00-09:00">&nbsp;&nbsp;8:00 AM -  &nbsp;&nbsp;9:00 AM</label>
        <label><input type="checkbox" name="availability" value="09:00-10:00">&nbsp;&nbsp;9:00 AM - 10:00 AM</label>
        <label><input type="checkbox" name="availability" value="10:00-11:00">10:00 AM - 11:00 AM</label>
        <label><input type="checkbox" name="availability" value="11:00-12:00">11:00 AM - 12:00 PM</label>
        <label><input type="checkbox" name="availability" value="12:00-13:00">12:00 PM -  &nbsp;&nbsp;1:00 PM</label>
        <label><input type="checkbox" name="availability" value="13:00-14:00">&nbsp;&nbsp;1:00 PM - &nbsp;&nbsp;2:00 PM</label>
        <label><input type="checkbox" name="availability" value="14:00-15:00">&nbsp;&nbsp;2:00 PM - &nbsp;&nbsp;3:00 PM</label>
        <label><input type="checkbox" name="availability" value="15:00-16:00">&nbsp;&nbsp;3:00 PM - &nbsp;&nbsp;4:00 PM</label>
        <label><input type="checkbox" name="availability" value="16:00-17:00">&nbsp;&nbsp;4:00 PM - &nbsp;&nbsp;5:00 PM</label>
        <label><input type="checkbox" name="availability" value="17:00-18:00">&nbsp;&nbsp;5:00 PM - &nbsp;&nbsp;6:00 PM</label>
      </div>
    </div>
    <button class="dashboard-btn" id="updateDoctorBtn">Save</button>
  `;
  } else if (type === 'patientLogin') {
    modalContent = `
        <h2>Patient Login</h2>
        <span id="message"></span>
        <input type="text" id="email" placeholder="Email" class="input-field">
        <input type="password" id="password" placeholder="Password" class="input-field">
        <button class="dashboard-btn" id="loginBtn">Login</button>
      `;
  }
  else if (type === "patientSignup") {
    modalContent = `
      <h2>Patient Signup</h2>
      <span id="globalErrorMessage" class="error"></span>
      <input type="text" id="name" placeholder="Name" class="input-field">
      <span id="nameMessage" class="error"></span>
      <input type="email" id="email" placeholder="Email" class="input-field">
      <span id="emailMessage" class="error"></span>
      <input type="password" id="password" placeholder="Password" class="input-field">
      <span id="passwordMessage" class="error"></span>
      <input type="text" id="phone" placeholder="Phone" class="input-field">
      <span id="phoneMessage" class="error"></span>
      <input type="text" id="address" placeholder="Address" class="input-field">
      <span id="addressMessage" class="error"></span>
      <button class="dashboard-btn" id="signupBtn">Signup</button>
    `;

  } else if (type === 'adminLogin') {
    modalContent = `
        <h2>Admin Login</h2>
        <span id="message"></span>
        <input type="text" id="username" name="username" autocomplete="off" placeholder="Username" class="input-field">
        <input type="password" id="password" name="password" placeholder="Password" class="input-field">
        <button class="dashboard-btn" id="adminLoginSubmitBtn" >Login</button>
      `;
  } else if (type === 'doctorLogin') {
    modalContent = `
        <h2>Doctor Login</h2>
        <span id="message"></span>
        <input type="text" id="email" placeholder="Email" class="input-field">
        <input type="password" id="password" placeholder="Password" class="input-field">
        <button class="dashboard-btn" id="doctorLoginSubmitBtn" >Login</button>
      `;
  }

  document.getElementById('modal-body').innerHTML = modalContent;
  document.getElementById('modal').style.display = 'block';

  document.getElementById('closeModal').onclick = () => {
    document.getElementById('modal').style.display = 'none';
  };

  if (type === "patientSignup") {
    document.getElementById("signupBtn").addEventListener("click", signupPatient);
  }

  if (type === "patientLogin") {
    document.getElementById("loginBtn").addEventListener("click", loginPatient);
  }

  if (type === 'addDoctor') {
    document.getElementById('saveDoctorBtn').addEventListener('click', addDoctorHandler);
  }

  if (type === 'editDoctor' && doctor) {
    populateDoctorForm(doctor);
    document.getElementById('updateDoctorBtn').addEventListener('click', updateDoctorHandler);
  }

  if (type === 'adminLogin') {
    document.getElementById('adminLoginSubmitBtn').addEventListener('click', adminLoginHandler);
  }

  if (type === 'doctorLogin') {
    document.getElementById('doctorLoginSubmitBtn').addEventListener('click', doctorLoginHandler);
  }
}
