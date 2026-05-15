# Patient Dashboard (Authenticated Patient) (EPIC)

**Priority:** Medium.
**Story Points:** 3.

---

## User Story
_As an authenticated patient, I want to access a protected patient dashboard where I can view available doctors,
and appointments, so that I can manage my healthcare activities._

---

## Feature (Gherkin)

```gherkin
Feature: Patient dashboard (authenticated)

Scenario: Successfully view the protected patient dashboard
  Given I am an authenticated patient
  When I am on the protected patient dashboard
  Then I should see:
    - Home button
    - Appointments button
    - Log Out button
    - The "Filter Doctors" form
    - The List of Doctors with available time slots  
```

## Acceptance Criteria

### 1. Home Button
- Clicking the "Home" button must redirect to the protected patient dashboard

### 2. Appointments Button
- Clicking the "Appointments" button must redirect to the patient's appointments page

### 3. Log Out Button
- It must be clickable to perform the logout functionality for an authenticated patient user

### 4. Filter Doctors Form
- It must display the following fields:
    - Name Filter (input)
    - Time Filter (select)
    - Specialty Filter (select)

### 5. List of Doctors With Available Time Slots
- The system must automatically load the list of doctors with available time slots
- Each doctor must include:
    - Name
    - Specialty
    - Phone number
    - Email
    - Available time slots
    - The "Book" button
- The "Book" button must be clickable to book an appointment

**Constraints**
- Each doctor must appear only once in the list
- Available times must be displayed in a readable format (e.g. "10:00-11:00", not raw DB format)

## Functional behavior
- When the system is fetching the list of doctors, a loading indicator must be visible until loading completes. 


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
- The protected patient dashboard must use the same endpoint used by the guest dashboard 
to retrieve unfiltered available doctors.

## Additional Notes
- This user story only includes what the protected patient dashboard page must have.
- Every functionality in the protected patient dashboard page is specified in its own user story.
