# List of Doctors With Available Time Slots (Guest Access)

**Priority:** Low
**Story Points:** 3

---

## User Story
_As a non-authenticated patient user, I want to see the list of doctors with available time slots,
so that I can choose an available doctor and book an appointment.._

---

## Feature (Gherkin)
```gherkin
Feature: Feature: List of doctors with available time slots for guest patients

Scenario: Successfully view the list of doctors with available time slots
  Given I am a non-authenticated patient user
  When I am on the public patient dashboard page
  Then I should see a list of doctors with available time slots
```

---

## Acceptance Criteria
### 1. The List of Doctors With Available Time Slots
- The system must automatically load the list of doctors with available time slots
- Each doctor must have the following:
    - Name
    - Specialty
    - Phone
    - Email
    - Available time slots
    - The "Book" button

### Book Button
- Clicking the "Book" button must open the login modal

**Constraints**
- Each doctor must appear only once in the list
- Available times must be displayed in a readable format (e.g. "10:00-11:00", not raw DB format)


## Functional Behavior

### 1. Loading State
```gherkin
Scenario: Show loading state
  When the system is loading the list of doctors with available time slots
  Then I should see a loading indicator
  And the list of doctors with available time slots should not be visible until loading completes
```

## Edge Cases

### 1. Empty State (No Doctors)
```gherkin
Scenario: No available doctors
  When there are no available doctors
  Then I should see a message "No doctors available at the moment"
```

### 2. API Error
```gherkin
Scenario: API error when loading doctors
  Given the doctors API is unavailable
  When the system attempts to load the doctors list
  Then I should see the message "Unable to load doctors"
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

**Clarification**
- The endpoint must be accessible to non-authenticated users

## Additional Notes
- If an error occurred while fetching the list of doctors, it must be logged.
