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
  Then I should be redirected to the public patient dashboard page
  And I should see a list of doctors with available time slots
  And each doctor card should display:
    - Name
    - Specialty
    - Phone
    - Email
    - Available time slots
  And I also should see "Login" and "Sign Up" buttons
```

## Acceptance Criteria

### 1. Navigation
- Selecting "Patient" redirects to the public patient dashboard page
- The page must be publicly accessible **without authentication**
- Direct URL access must not redirect to login or create a redirect loop

### 2. Filter Doctors Form
- The page must display the "Filter Doctors" form
- It must display the following fields:
    - Name Filter (input)
    - Time Filter (select)
    - Specialty Filter (select)

### 3. List of Doctors With Available Time Slots
- The system must automatically load the list of doctors with available time slots
- Each doctor card must include:
  - Name
  - Specialty
  - Phone number
  - Email
  - Available time slots

**Constraints**
- Each doctor must appear only once in the list
- Available time slots must be displayed in a readable format (e.g. "10:00-11:00", not raw DB format)

### 4. Login Button
- Clicking the "Log In" button must open the "Patient Login Modal" within a modal

### 5. Sign Up Button
- Clicking the "Sign Up" button must open the "Patient Sign Up Modal" within a modal

## Functional behavior

### 1. Loading State
```gherkin
Scenario: Show loading state
  When the public patient dashboard is loading
  Then I should see a loading indicator
  And the list of doctors with available time slots should not be visible until loading completes
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

## API Contract (Backend)
__Request:__
```http
GET /api/doctors/list
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
**Clarification:**
- This endpoint returns unfiltered available doctors for guest users.

## Additional Notes
- This user story only includes what the public patient dashboard page must have.
- Every functionality in the public patient dashboard page is specified in its own user story.
- The public patient dashboard page is accessible at: `/pages/patientDashboard.html
