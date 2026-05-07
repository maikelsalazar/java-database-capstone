# Feature: Patient Dashboard (Guest Access)

**Priority:** Medium.
**Story Points:** 3.

---

## User Story
_As a Patient, I want to access a public dashboard where I can view available doctors and choose to log in or sign up,
so that I can decide how to proceed._

---

## Feature (Gherkin)

```gherkin
Feature: Patient dashboard (guest access)

Scenario: Access patient dashboard as a guest
  Given I am not authenticated
  And I am on the role selection page
  When I select the "Patient" role
  Then I should be redirected to "/pages/patientDashboard.html"
  And I should see a list of available doctors
  And each doctor should display name, specialty, available times, phone, and email
  And I should see "Login" and "Sign Up" buttons
```

## Acceptance Criteria
### 1. Navigation
    - Selecting "Patient" redirects to:
      ```
      /pages/patientDashboard.html
      ```
    - Page must be accessible **without authentication**.
    - Direct URL access should also work (no redirect loop)
### 2. Doctor List
- Displays all doctors marked as **available**
- Each doctor must include:
  - Name
  - Specialty
  - Available times
  - Phone number
  - Email

**Constraints**
- Each doctor must appear only once in the list
- Available times must be displayed in a readable format (e.g. "10:00-11:00", not raw DB format)

### 3. Login Button
- Open login modal
```gherkin
  Scenario: Open login modal
    When I click the "Login" button
    Then I should see the patient login form
```
### 4. Sign Up Button
- Open registration modal
  ```gherkin
    Scenario: Open registration modal
      When I click the "Sign Up" button
      Then I should see the patient registration form
  ```

## Functional behavior

### 1. Loading State
```gherkin
Scenario: Show loading state
  When the dashboard is loading
  Then I should see a loading indicator
  And the doctor list should not be visible until loading completes
```

### 2. Navigation Consistency
```gherkin
Scenario: Navigate back to role selection
  When I click on the system name/logo
  Then I should be redirected to the role selection page
```

## Edge cases
### 1. Empty State (No Doctors)
```gherkin
Scenario: No available doctors
  When there are no available doctors
  Then I should see a message "No doctors available at the moment"
```

### 2. Error Handling
```gherkin
Scenario: API error when loading doctors
  When the system fails to load doctors
  Then I should see an error message "Unable to load doctors"
  And I should have an option to retry
```

## API Contract
__Request:__
```http
GET /api/doctor/list
```
__Response:__
```json
{
  "doctors": [
    {
      "id": 1,
      "name": "Dr. Emily Adams",
      "specialty": "Cardiologist",
      "email": "dr.adams@example.com",
      "phone": "555-101-2020",
      "availableTimes": [
        "09:00-10:00",
        "10:00-11:00",
        "11:00-12:00",
        "14:00-15:00"
      ]
    },
    {
      "id": 2,
      "name": "Dr. Mark Johnson",
      "specialty": "Neurologist",
      "email": "dr.johnson@example.com",
      "phone": "555-202-3030",
      "availableTimes": [
        "10:00-11:00",
        "11:00-12:00",
        "14:00-15:00",
        "15:00-16:00"
      ]
    }
  ]
}
```
