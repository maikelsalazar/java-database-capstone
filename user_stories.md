## Admin User Stories

### Login As Admin
_As an Admin user, I want to log into the system, so that I can access the admin dashboard._

**Feature (Gherkin)**
```gherkin
Feature: Login As Admin

Scenario: Successfully log in as admin
  Given I am not an authenticated user
  And I am on the role selection page

  When I click the "Admin" role button
  Then the admin login modal should be displayed

  When I enter a valid username and password
  And I submit the login form
  Then I should be redirected to the admin dashboard page

Scenario: Attempt to log in with invalid credentials
  Given I am not an authenticated user
  And I am on the role selection page

  When I click the "Admin" role button
  Then the admin login modal should be displayed

  When I enter an invalid username and password
  And I submit the login form
  Then I should remain on the admin login modal
  And An authentication error message should be displayed
```  

**Acceptance Criteria:**

1. A guest user can log in using a registered username and password.
2. After successful authentication, the user is redirected to the admin dashboard page.
3. The system denies access when invalid credentials are provided.
4. When authentication fails, an appropriate error message is displayed.

**Priority**: High
**Story Points**: 3

**Notes:**
- Admin uses username instead of email for authentication.
- Password input should hide typed characters.

---

### Log Out As Admin
_As an authenticated admin user, I want to log out of the system, so that I can securely end my session._

**Feature (Gherkin)**
```gherkin
Feature: Log Out As Admin

Scenario: Successfully log out as admin
  Given I am authenticated as an admin user
  When I click on the "Logout" button
  Then I should be redirected to the index page
  And my authentication token/session must be deleted
  And I should be redirected to the index page

Scenario: Access admin dashboard after logout
  Given I have logged out
  When I try to access the admin dashboard URL
  Then I should be redirected to the index page
```
**Acceptance Criteria:**
1. The authentication token/session must be deleted
2. The user must be redirected to the index page
3. After logging out, the user must not be able to access the admin dashboard

**Priority:** High
**Story Points:** 1
**Notes:**

---

### View Admin Dashboard
_As an authenticated admin user, I want to access the admin dashboard where I can view the "Log Out" button,
the "Add Doctor" button, a "Filter Doctors" form, and the list of doctors with available time slots where each doctor card displays
the doctor's information and a "Delete Doctor" button, so that I can manage doctors and system operations._

**Feature (Gherkin)**
```gherkin
Feature: View Admin Dashboard

Scenario: Successfully view the admin dashboard
  Given I am an authenticated admin user
  When I access the admin dashboard
  Then I should see the "Add Doctor" button
  And I should see the "Log Out" button
  And I should see the "Filter Doctors" form
  And I should see the list of doctors

  And each doctor card should display:
    - Name
    - Specialty
    - Phone
    - Email
    - Available time slots
    - The "Delete Doctor" button

Scenario: Attempt to access the admin dashboard without admin privileges
  Given I am not an authenticated admin user
  When I attempt to access the admin dashboard
  Then I should be redirected to the "Select Role" page
```

**Acceptance Criteria:**
1. Only authenticated admin users can access the admin dashboard.
2. The dashboard displays the Add Doctor button.
3. The dashboard displays the Log Out button.
4. The dashboard displays the Filter Doctors form.
5. The dashboard automatically loads the list of doctors.
6. Each doctor card displays the doctor's information and available time slots.
7. Each doctor card displays a Delete Doctor button.

**Priority:** High
**Story Points:** 3
**Notes:**
- The system must automatically load the list of doctors.
- This is the view of the admin dashboard; inherited functionalities are implemented separately.

---

### Add Doctor
_As an authenticated admin user, I want to add a doctor, so that patients can schedule appointments with them._

```gherkin
Feature: Add a doctor
Background:
  Given I am an authenticated admin user
  And I am on the admin dashboard page
  And I have selected the "Add Doctor" option
  And I am on the Add Doctor modal

Scenario: Successfully adding a doctor 
  When I enter valid doctor information
  And I submit the Add Doctor form
  Then the new doctor must be stored
  And I should see the newly added doctor in the list of available doctors

Scenario: Attempt to add a doctor with missing required information
  When I leave one or more required fields empty
  And I submit the Add Doctor form
  Then the system should not allow the registration
  And I should remain on the Add Doctor modal
  And I should see the corresponding validation error messages

Scenario: Attempt to add a doctor with an existing email
  When I enter doctor information with an email already registered
  And I submit the Add Doctor form
  Then the system should not allow the registration
  And I should remain on the Add Doctor modal
  And I should see an email already exists error message
```

**Acceptance Criteria:**
1. An admin can add a doctor using valid doctor information.
2. When a doctor is added successfully, the system persists the doctor in the database.
3. The system prevents duplicate doctor email registrations.
4. When validation fails, the doctor is not persisted and validation errors are displayed.
5. Only authenticated admin users can add doctors.

**Priority:** High
**Story Points:** 5
**Notes:**
- The email address must be unique among doctors.
- Only admin users can add doctors.

---

### Delete Doctor
_As an authenticated admin user, I want to delete a doctor, so that patients can no longer schedule appointments with that doctor._

**Feature (Gherkin)**
```gherkin
Feature: Delete a doctor

Background:
  Given I am an authenticated admin user
  And I am on the admin dashboard page
  And I locate a doctor in the list of available doctors
  And I click the "Delete" button on the doctor's card
  And a confirmation prompt is displayed

Scenario: Successfully deleting a doctor
  When I confirm the deletion
  Then the doctor must be deleted
  And I should no longer see the deleted doctor in the list of available doctors

Scenario: Cancel doctor deletion
  When I cancel the deletion
  Then the doctor must not be deleted
  And I should continue seeing the doctor in the list of available doctors
```

**Acceptance Criteria:**
1. Before deleting a doctor, a confirmation prompt should be displayed
2. The delete operation must be executed only if the admin confirms the deletion.
3. After successfully deleting a doctor, the doctor's record and its relationships must be deleted in the database.
4. When I cancel the confirmation, the deletion must not occur

**Priority:** Medium
**Story Points:** 2
**Notes:**
- The delete operation is a hard delete in the database.
- Non-existing doctors are treated as successfully deleted to avoid exposing sensitive system information


---

### Update the information of the doctors
_As an authenticated admin, I want to update a doctor's specialties and contact information, so that patients can access up-to-date information about doctors._

**Feature (Gherkin)**
```gherkin
Feature: Update Doctor Information

Background:
  Given I am an authenticated admin
  And I am on the admin dashboard
  And I have located a doctor
  And I click the "Update" button
  And the "Update Doctor" form opens

Scenario: View update doctor form
  Then the form should be prefilled with the doctor's current information
  And the name field should be disabled
  And the password field should not be displayed

Scenario: Successfully update doctor information
  When I modify the doctor's specialties or contact information
  And I click the "Confirm" button
  Then the doctor's specialties and contact information should be updated
  And I should be redirected to the admin dashboard
  And I should see the selected doctor with the updated information

Scenario: Update doctor information with a duplicate email
  When I enter a duplicate email
  And I click the "Confirm" button
  Then I should remain on the "Update Doctor" form
  And I should see the corresponding validation error message

Scenario: Update doctor information with invalid data
  When I enter invalid information
  And I click the "Confirm" button
  Then I should remain on the "Update Doctor" form
  And I should see the corresponding validation error messages
```

**Acceptance Criteria:**
1. When viewing the Update Doctor form
    - The form should be prefilled with the doctor's current information.
    - The name field should be disabled.
    - The password field should not be displayed.
2. After successfully updating a doctor
    - The changes made must be persisted in the database.
    - The "Update Doctor" form should close automatically
    - The user should be redirected to the admin dashboard.
    - The recently updated doctor should display the new information in its card
3. When trying to update the information of a doctor with a duplicate email
    - The doctor's information in the database must not change.
    - I should remain on the "Update Doctor" form
    - I should see the proper error message
4. When trying to update the information of a doctor with invalid data
    - The doctor's information in the database must not change.
    - I should remain on the "Update Doctor" form
    - I should see the proper error messages

**Priority:** Low
**Story Points:** 3
**Notes:**
- The email address must be unique among doctors.
- Validations must be enforced at the backend

## Doctor User Stories

### Login As Doctor
_As a doctor, I want to log into the system, so that I can access the doctor dashboard._

---

**Feature (Gherkin)**
```gherkin
Feature: Login As Doctor

Background:
  Given I am not an authenticated user
  And I am on the role selection page
  And I have selected the "Doctor" role
  And I am on the doctor login modal

Scenario: Successfully log in as doctor
  When I enter a valid email and password
  And I submit the login form
  Then I should be redirected to the doctor dashboard page

Scenario: Attempt to log in with invalid credentials
  When I enter an invalid email and password
  And I submit the login form
  Then I should remain on the doctor login modal
  And an authentication error message should be displayed
```  

**Acceptance Criteria:**

1. A doctor can authenticate using a registered email and password.
2. After successful authentication, the doctor is redirected to the doctor dashboard page.
3. The system denies access when invalid credentials are provided.
4. When authentication fails, an appropriate error message is displayed.

**Priority**: High
**Story Points**: 3

**Notes:**
- Email must follow a valid email format.
- Password input should hide typed characters.

---

### Log Out As Doctor
_As an authenticated doctor, I want to log out of the system, so that I can securely end my session._

**Feature (Gherkin)**
```gherkin
Feature: Log Out As Doctor

Scenario: Successfully log out as doctor
  Given I am authenticated as a doctor user
  When I click on the "Log Out" button
  Then I should be redirected to the "Select Role" page
  And my authentication token/session must be deleted

Scenario: Access doctor dashboard after logout
  Given I am not authenticated as a doctor user
  When I try to access the doctor dashboard
  Then I should be redirected to the "Select Role" page
```

**Acceptance Criteria:**
1. The authentication token/session must be deleted.
2. The doctor must be redirected to the "Select Role" page.
3. After logging out, the doctor must not be able to access the doctor dashboard.

**Priority:** High
**Story Points:** 1
**Notes:**


---

### Filter appointments as Doctor
_As an authenticated doctor, I want to filter appointments, so that I can be prepared._

**Feature (Gherkin)**
```gherkin
Feature: Filter appointments

Background:
  Given I am an authenticated doctor
  And I am on the protected doctor dashboard


Scenario: Filter appointments by criteria
  Given I have appointments booked
  When I apply filters
    - Patient's name (optional)
    - Date (required)
  Then I should receive a filtered list of appointments matching the criteria
  And each appointment should contain:
    - Appointment Id
    - Doctor Id
    - Patient Id
    - Patient's Name
    - Patient's Phone
    - Patient's Email

Scenario: No appointments match filters
  Given I have appointments booked
  When I apply filters that do not match any of my appointments
  Then I should receive an empty list

Scenario: Missing required date filter
  When I leave the date filter empty
  And I apply filters
  Then I should see a validation error message
```

**Acceptance Criteria:**
1. Only authenticated doctors can apply the filters
2. When filtering appointments
- Patient's name filter is optional.
- Patient name matching must be case-insensitive and use contains logic, e.g. "ah" should match "Sarah Lee".
- Date is required.
- The date filter matches appointments scheduled on the selected date.
3. Each appointment should include:
    - Appointment Id
    - Doctor Id
    - Patient Id
    - Patient's Name
    - Patient's Phone
    - Patient's Email
4. 4. The system must return an empty list when no appointments match the applied filters.
      **Priority:** Low
      **Story Points:** 3
      **Notes:**
- The backend must ensure to return appointments associated to the authenticated doctor

---

### Mark/Unmark available time slots:
_As an authenticated doctor, I want to mark my available time slots, so that patients can be informed about my availability._

**Feature (Gherkin)**
```gherkin
Feature: Mark/Unmark available time slots

Background:
  Given I am an authenticated doctor
  And I am on the doctor dashboard
  And I click on the "Mark Availability" button
  And the "Mark Availability" form opens

Scenario: View mark availability form
  Then I see the list of available time slots predefined by the system
  And my previously saved available time slots are marked automatically
  And I see the "Confirm" button
  And I see the "Cancel" button

Scenario: Successfully updating available time slots
  When I mark or unmark available time slots from the list
  And I click on the "Confirm" button
  Then only the marked available time slots are saved
  And the unmarked available time slots are removed
  And I should see an "Availability updated" message
  And the form should close automatically
  And I should remain on my doctor's dashboard

Scenario: Cancel to update available time slots
  When I click on the "Cancel" button
  Then my available time slots do not change
  And the form should close automatically
  And I should remain on my doctor's dashboard
```

**Acceptance Criteria:**
1. The Mark Availability form should display:
- The list of available time slots predefined by the system
- My previously saved available time slots are marked automatically if any
- Confirm button
- Cancel Button
2. After successfully updating available time slots:
- Only the marked available time slots are saved
- The unmarked available time slots are removed
- An "Availability updated" confirmation message should be displayed
- The form should close automatically
- The user should remain on the doctor's dashboard
3. Cancel to update available time slots:
- My previously saved available time slots remain with no modifications
- The form should close automatically
- The user should remain on the doctor's dashboard

**Priority:** Medium
**Story Points:** 3
**Notes:**
- A doctor may leave all available time slots unmarked, indicating that the doctor is unavailable.


### View Doctor's Dashboard:
_As an authenticated doctor, I want to view my doctor's dashboard, so that I can manage my appointments, view patient details, and add prescriptions to my patients._

**Feature (Gherkin)**
```gherkin
Feature: View Doctor Dashboard

Background:
  Given I am an authenticated doctor
  And I am on my doctor's dashboard

Scenario: Display doctor dashboard
  Then I should see the "Home" button
  And I should see the "Profile" button
  And I should see the "Log Out" button
  And I should see the "Filter Appointments" form
  And I should see a list of appointments with:
    - Appointment ID
    - Patient Name
    - Patient Phone
    - Patient Email
    - "Add Prescription" button

Scenario: Load today's appointments automatically
  Given There are appointments for today
  Then today's appointments should be displayed by default

Scenario: No appointments for today
  Given There are no appointments for today
  Then I should see an empty state message indicating no appointments are available
```

**Acceptance Criteria:**
1. The dashboard should display the "Home" button
2. The dashboard should display the "Profile" button
3. The dashboard should display the "Log Out" button
4. The dashboard should display the "Filter Appointments" form
5. The dashboard should display the list of appointments
6. The list of appointments should be filled with today's appointments automatically if any

Priority: Low
Story Points: 3

**Notes:**
- The list of appointments must only display appointments of the authenticated doctor.
- This is the view of the doctor's dashboard; inherited functionalities are implemented separately.

### Add Prescription to a patient's appointment as doctor:
_As an authenticated doctor, I want to add a prescription for a patient's appointment, so that the patient can follow the recommended indications._

**Feature (Gherkin)**
```gherkin
Feature: Add Prescription

Background:
  Given I am an authenticated doctor
  And I am on my doctor's dashboard
  And I see the list of appointments
  And I have located the appointment
  And I click on the "Add Prescription" button
  And the "Add Prescription" form opens

Scenario: View add prescription form
  Then the form's fields are:
    - Patient's Name
    - Medicine Names
    - Dosage instructions
    - Additional Notes
    - Add Prescription button
    - Cancel button
  And the patient's name should be prefilled and disabled

Scenario: Successfully adding a prescription 
  When I enter valid medicine names
  And I optionally enter dosage instructions or additional notes
  And I click on the "Add Prescription" button
  Then the prescription should be associated with the selected appointment
  And I should see a "Prescription saved" message 
  And the "Add Prescription" form is closed
  And I should remain on my doctor's dashboard

Scenario: Add prescription with missing required data
  When I leave one or more required fields empty
  And I click on the "Add Prescription" button
  Then the prescription should not be saved
  And I should remain on the "Add Prescription" form
  And I should see the proper validation error messages

Scenario: Cancel to add a prescription
  When I click on the "Cancel" button
  Then the "Add Prescription" form should close
  And I should remain on my doctor's dashboard 
```

**Acceptance Criteria:**
1. When viewing the Add Prescription form:
    - Patient's Name prefilled and disabled
    - Medicine Names (required)
    - Dosage Instructions
    - Additional Notes
    - Add Prescription button
    - Cancel button
2. After adding a prescription successfully:
    - The prescription must be persisted in the database.
    - The prescription must be associated with the selected appointment.
    - I should see the "Prescription saved" confirmation message
    - The form should close automatically
    - The user should remain on the doctor's dashboard.
3. When I do not fill in any required field:
    - The prescription should not be saved
    - I should remain on the "Add Prescription" form
    - I should see the proper validation error messages
4. When cancelling the operation:
    - The "Add Prescription" form should close.
    - No prescription should be saved.
    - The user should remain on the doctor's dashboard.

**Priority:** Medium
**Story Points:** 5
**Notes:**
- A doctor must not be able to add prescriptions to another doctor's appointments.

## Patient's User Stories

### Patient Dashboard As Guest
_As a Patient, I want to access a public dashboard where I can view available doctors and choose to log in or sign up, so that I can decide how to proceed._

**Feature (Gherkin)**
```gherkin
Feature: Patient dashboard (guest access)

Scenario: Access the patient dashboard as a guest
  Given I am not authenticated
  And I am on the role selection page
  When I select the "Patient" role
  Then I should be redirected to the public patient dashboard page
  And I should see the "Login" button 
  And I should see the "Sign Up" button
  And I should see the "Filter Doctors" form
  And I should see a list of doctors with available time slots
  And each doctor card should display:
    - Name
    - Specialty
    - Phone
    - Email
    - Available time slots
```

**Acceptance Criteria:**
1. The dashboard displays the Login button.
2. The dashboard displays the Sign Up button.
3. The dashboard displays the Filter Doctors form.
4. The dashboard automatically loads the list of doctors with available time slots
5. Each doctor card displays the doctor's information and available time slots.

**Priority:** High
**Story Points:** 3
**Notes:**
- This is the view of the public patient dashboard; inherited functionalities are implemented separately.
- Uses the "List of Doctors with Available Time Slots" functionality.


---

### View Appointments Dashboard As Patient
_As a patient, I want to view my appointments dashboard, so that I can prepare accordingly._

**Feature (Gherkin)**
```gherkin
Feature: View Appointments Dashboard As Patient

Scenario: View appointments dashboard
  Given I am on the protected patient dashboard
  When I click the "Appointments" button
  Then I should see my appointments dashboard
  And I should see the "Filter Appointments" form
  And the form should include a Doctor Name filter
  And the form should include an Appointment Status filter with:
    - All
    - Past
    - Upcoming
  And I should see a list of my appointments
  And each appointment should display:
    - Doctor's Name
    - Date
    - Time
    - An "Update Appointment" button
```

**Acceptance Criteria:**
1. The dashboard should display the "Filter Appointments" form.
2. The dashboard should display the patient's appointments.
3. Each appointment should display the doctor's name, date, and time.
4. Each appointment should display an "Update Appointment" button.

**Priority:** Medium
**Story Points:** 3
**Notes:**
- This is the view of the appointments dashboard; inherited functionalities are implemented separately.

---

### Sign Up As Patient
_As a guest user, I want to register as a patient, so that I can book appointments at the clinic._

---

**Feature (Gherkin)**
```gherkin
Feature: Sign Up As Patient

Scenario: Successfully signing up as a patient
  Given I am not an authenticated user
  And I am on the patient dashboard page
  When I click the "Sign Up" button
  Then the Sign Up modal should be displayed
  
  When I enter valid patient information
  And I submit the Sign Up form
  Then the new patient must be stored
  And the patient dashboard should refresh automatically
  And I must see the patient dashboard as signed in patient
```  

**Acceptance Criteria:**

1. A guest user can register as a patient using a valid email and password.
2. The system prevents duplicate email registrations.
3. The patient account is persisted in the database.

**Priority**: High
**Story Points**: 3

**Notes:**
- Email addresses must be unique.

---
### Login As Patient
_As a guest user, I want to log into the system, so that I can manage my bookings._

---

**Feature (Gherkin)**
```gherkin
Feature: Login As Patient

Scenario: Successfully log in as patient
  Given I am not authenticated
  And I am on the role selection page
  
  When I select the "Patient" role
  Then I should be redirected to the public patient dashboard page
  
  When I click the "Log In" button
  Then the Log In modal should be displayed

  When I enter a valid email and password
  And I submit the Log In form
  Then I should be redirected to the protected patient dashboard

Scenario: Attempt to log in with invalid credentials
  Given I am not authenticated
  And I am on the role selection page

  When I select the "Patient" role
  Then I should be redirected to the public patient dashboard page
  
  When I click the "Log In" button
  Then the Log In modal should be displayed

  When I enter an invalid email or password
  And I submit the Log In form
  Then I should remain on the current page
  And An authentication error message should be displayed
```  

**Acceptance Criteria:**

1. A guest user can log in using a registered email and password.
2. After successful authentication, the user is redirected to the protected patient dashboard.
3. The system denies access when invalid credentials are provided.
4. When authentication fails, an appropriate error message is displayed.


**Priority**: High
**Story Points**: 3

**Notes:**
- Email must follow a valid email format.
- Password input should hide typed characters.

---

### Log Out As Patient
_As an authenticated patient user, I want to log out of the system, so that I can securely end my session._

**Feature (Gherkin)**
```gherkin
Feature: Log Out As Patient

Scenario: Successfully log out as patient
  Given I am authenticated as a patient user
  When I click on the "Log Out" button
  Then I should be redirected to the "Select Role" page
  And my authentication token/session must be deleted

Scenario: Access protected patient dashboard after logout
  Given I have logged out
  When I try to access the protected patient dashboard
  Then I should be redirected to the "Select Role" page
```

**Acceptance Criteria:**
1. The authentication token/session must be deleted.
2. The user must be redirected to the "Select Role" page.
3. After logging out, the user must not be able to access the protected patient dashboard.

**Priority:** High
**Story Points:** 1
**Notes:**


---

### Patient Dashboard As Authenticated Patient
_As an authenticated patient, I want to access a protected patient dashboard where I can view doctors with available time slots, and access my appointments, so that I can manage my healthcare activities._

**Feature (Gherkin)**
```gherkin
Feature: Patient dashboard as authenticated patient

Scenario: Successfully view the protected patient dashboard
  Given I am an authenticated patient
  When I am on the protected patient dashboard
  Then I should see the "Home" button
  And I should see the "Appointments" button
  And I should see the "Log Out" button
  And I should see the "Filter Doctors" form
  And I should see the list of doctors with available time slots:
  And each doctor card should display:
    - Name
    - Specialty
    - Phone
    - Email
    - Available time slots
    - The "Book Appointment" button

Scenario: Attempt to access the protected patient dashboard without patient privileges
  Given I am not an authenticated patient
  When I attempt to access the protected patient dashboard
  Then I should be redirected to the "Select Role" page
```

**Acceptance Criteria:**
1. Only authenticated patients can access the protected patient dashboard.
2. The dashboard displays the Home button.
3. The dashboard displays the Appointments button
4. The dashboard displays the Log Out button
5. The dashboard displays the Filter Doctors form
6. The dashboard must load the list of doctors with available time slots automatically.
7. Each doctor card displays the "Book Appointment" button.
8. When the user is not authenticated as a patient, it should be redirected to the "Select Role" page
   **Priority:** High
   **Story Points:** 3
   **Notes:**
- This is the view of the protected patient dashboard; inherited functionalities are implemented separately.
- Uses the "List of Doctors with Available Time Slots" functionality.

---

### Book Appointment
_As an authenticated patient, I want to book an hour-long appointment with a doctor, so that I can consult with a doctor._

**Feature (Gherkin)**
```gherkin
Feature: Booking An Appointment

Background:
  Given I am an authenticated patient
  And I am on the protected patient dashboard
  And I locate a doctor in the list of doctors with available time slots
  And I click the "Book Appointment" button on the doctor's card
  And the "Book Appointment" overlay should be displayed
  And I should see "Book Appointment" form

Scenario: View book appointment form
  Then the form should display:
     - Patient's Name (disabled)
     - Doctor's Name (disabled)
     - Doctor's Specialty (disabled)
     - Doctor's Email (disabled)
     - Date field
     - Time field
     - Confirm Booking button
  And only the doctor's available time slots should be displayed in the time field

Scenario: Successfully booking an appointment
  When I have selected a valid date and time
  And I click the "Confirm Booking" button
  Then the appointment should be stored
  And I should see an "Appointment booked successfully" message
  And the overlay should close automatically
  And I should remain on the patient dashboard

Scenario: Failing to book an appointment on missing required fields
  When I leave the date or time field empty
  And I click the "Confirm Booking" button
  Then the appointment should not be stored
  And I should see the proper validation error messages
  And I should remain on the "Book Appointment" overlay

Scenario: Past date and time validation
  When I have selected a past date and time
  And I click the "Confirm Booking" button
  Then the appointment should not be stored
  And I should see an "Appointment cannot be in the past" message
  And I should remain on the "Book Appointment" overlay

Scenario: Doctor's schedule conflict
  Given another appointment already exists for the doctor at the selected date and time
  When I have selected a date and time
  And I click the "Confirm Booking" button
  Then the appointment should not be stored
  And I should see a "Doctor unavailable" message
  And I should remain on the "Book Appointment" overlay

Scenario: Patient's schedule conflict
  Given another appointment already exists for the patient at the selected date and time
  When I have selected a date and time
  And I click the "Confirm Booking" button
  Then the appointment should not be stored	
  And I should see a "Patient already has an appointment at this time" message
  And I should remain on the "Book Appointment" overlay
```

**Acceptance Criteria:**
1. When viewing the Book Appointment form:
- The patient's name should be prefilled
- The doctor's information should be prefilled
- Only the doctor's available time slots should be displayed in the time field
2. After successfully booking an appointment:
- The appointment must be persisted in the database
- A confirmation message should be displayed
- The Book Appointment overlay should be closed automatically
3. Failing to book an appointment due to missing required fields
- The appointment should not be persisted
- The proper error messages should be displayed
- I should remain on the Book Appointment overlay
4. Failing to book an appointment due to selecting a past date and time
- The appointment should not be persisted
- I should see an "Appointment cannot be in the past" message
- I should remain on the Book Appointment overlay
5. Failing to book an appointment due to a conflict with the doctor's schedule
- The appointment should not be persisted
- I should see a "Doctor unavailable" message
- I should remain on the Book Appointment overlay
6. Failing to book an appointment due to a conflict with the patient's schedule
- The appointment should not be persisted
- I should see a "Patient already has an appointment at this time" message
- I should remain on the Book Appointment overlay

**Priority:** High
**Story Points:** 8
**Notes:**
- The time field must only display the doctor's available time slots.
- Doctor's schedule conflicts must be validated at the backend.
- Patient's schedule conflicts must be validated at the backend.
- Appointments cannot be booked in the past.
- All appointments have a fixed duration of one hour.

---

### Update Appointment As Patient
_As a patient, I want to update an appointment, so that I can attend at my convenience._

**Feature (Gherkin)**
```gherkin
Feature: Update Appointment

Background:
  Given I am on the patient appointments dashboard
  And I have selected an appointment to update
  And I have opened the "Update Appointment" form

Scenario: View update appointment form
  Then the appointment information should be prefilled
  And the patient and doctor fields should be disabled
  And I should only see the doctor's available time slots

Scenario: Successfully update an appointment
  When I change the date and time
  And I click the "Confirm" button
  Then I should see a confirmation message
  And I should be redirected to the patient appointments dashboard

Scenario: Doctor schedule conflict
  Given another appointment already exists for the doctor at the selected date and time
  When I change the date and time
  And I click the "Confirm" button
  Then I should remain on the "Update Appointment" form
  And I should see a "Doctor unavailable" message

Scenario: Patient schedule conflict
  Given another appointment already exists for the patient at the selected date and time
  When I change the date and time
  And I click the "Confirm" button
  Then I should remain on the "Update Appointment" form
  And I should see a "Patient already has an appointment at this time" message

Scenario: Past date and time validation
  Given I have selected a past date and time
  When I click the "Confirm" button
  Then I should remain on the "Update Appointment" form
  And I should see an "Appointment cannot be in the past" message

Scenario: Cancel updating an appointment
  When I click the "Cancel" button
  Then I should be redirected to the patient appointments dashboard
```

**Acceptance Criteria:**
1. When viewing the Update Appointment form
    - Appointment information should be prefilled
    - Patient and doctor fields should be disabled
    -  Only the doctor's available time slots should be displayed in the time field
2. After successfully updating an appointment
    - A "confirmation" message should be displayed
    - The user should be redirected to the patient appointments dashboard
3. Failing to update an appointment due to a conflict with the doctor's schedule
    - I should remain on the "Update Appointment" form
    - I should see a "Doctor unavailable" message
4. Failing to update an appointment due to a conflict with the patient's schedule
    - I should remain on the "Update Appointment" form
    - I should see a "Patient already has an appointment at this time" message
5. Failing to update an appointment, because the selected date and time is in the past
    - I should remain on the "Update Appointment" form
    - I should see an "Appointment cannot be in the past" message
6. When the user cancels updating the appointment, they should be redirected to the patient appointments dashboard

**Priority:** Medium
**Story Points:** 5
**Notes:**
- The time field must only show the doctor's available time slots.
- Doctor schedule conflicts must be validated at the backend.
- Patient schedule conflicts must be validated at the backend.
- Appointments cannot be updated to a past date and time; validation must be enforced at the backend.

---

### Shared Admin and Patients

### List of Doctors with Available Time Slots
_As a system consumer, I want to retrieve the list of doctors with available time slots, so that I can display them in different dashboards._

**Feature (Gherkin)**
```gherkin
Feature: List Available Doctors

Scenario: Successfully getting the list of doctors with available time slots
  Given the system is running
  When I request the list of doctors with available time slots
  Then I should receive the list of doctors.
  And each doctor should contain:
    - Name
    - Specialty
    - Email
    - Phone
    - Available time slots

Scenario: No doctors available
  Given the system is running
  When I request the list of doctors with available time slots
  And there are no doctors with available time slots
  Then I should receive an empty doctor list
```

**Acceptance Criteria:**
1. The system must return a list of doctors with available time slots when the endpoint is called
2. Each doctor must have the following:
- Name
- Specialty
- Phone
- Email
- Available time slots
3. The response must not contain duplicate doctor entries
4. Available time slots must be displayed in a readable format (e.g. "10:00-11:00", not raw DB format)
5. The system should return an empty doctor list when there are no available doctors.

**Priority:** Low
**Story Points:** 2
**Notes:**
- The endpoint is public and reusable by multiple dashboards (admin, patient, guest).

---

### Filter Doctors with Available Time Slots
_As a system consumer, I want to filter the list of doctors with available time slots, so that I can find doctors that match my criteria._

**Feature (Gherkin)**
```gherkin
Feature: Filter List of Doctors with Available Time Slots

Scenario: Filtering doctors by criteria
  Given a list of doctors with available time slots exists
  When I apply filters (name, specialty, time period)
  Then I should receive a filtered list of doctors matching all criteria

Scenario: No doctors match filters
  Given a list of doctors with available time slots exists
  When I apply filters (name, specialty, time period)
  Then I should receive an empty list
```

**Acceptance Criteria:**
1. The system must return a list of doctors matching all applied filters.
2. The Name Filter must be case-insensitive, e.g.: "le" should match "Sarah Lee", "Lee Martin".
3. The Time Filter is based on doctor's available slots
- AM: 00:00 ≤ time < 12:00
- PM: 12:00 ≤ time ≤ 23:59
- All: no filtering
4. The Specialty filter: exact match from predefined specialties; case insensitive.
5. All filters must apply the AND logic
6. The system must return an empty list when no doctors match the selected criteria.
   **Priority:** Low
   **Story Points:** 3
   **Notes:**
- The endpoint is public and reusable by multiple consumers (admin, patient, guest dashboards).

## Public

### Select Role (Index Page)
_As a user, I want to view and select the available system roles (Admin, Patient, Doctor),
so that I can access the appropriate section of the system._

**Feature (Gherkin)**
```gherkin
Feature: Select system role
Scenario: View available roles on the index page
  Given I have access to the system
  When the index page loads
  Then I should see the available system roles
  And I should see a clickable "Admin" role option
  And I should see a clickable "Patient" role option
  And I should see a clickable "Doctor" role option
```

**Acceptance Criteria:**
1. The index page must be publicly accessible
2. The index page must load without authentication
3. The index page must be the default application entry point
4. I should see the "Admin" role option.
5. I should see the "Patient" role option.
6. I should see the "Doctor" role option.
7. Each role option must be presented as a clickable button or link.

**Priority:** High
**Story Points:** 2
**Notes:**
- The index page acts as the public entry point of the system.
- Each role must include a clickable button or link.
