# Feature: Admin Dashboard (EPIC)

**Priority:** High
**Story Points:** 13

---

## User Story
_As an authenticated admin user, I want to access the admin dashboard where I can view the "Log Out" button,
the "Add Doctor" button, a "Filter Doctors" form, and the list of doctors with available time slots where each doctor card displays
the doctor's information and a "Delete Doctor" button, so that I can manage doctors and system operations._
---

## Feature (Gherkin)

```gherkin
Feature: Admin dashboard

Scenario: Successfully view the admin dashboard
  Given I am an authenticated admin user
  When I access the admin dashboard
  Then I should see:
    - The "Add Doctor" button
    - The "Log Out" button
    - The "Filter Doctors" form
    - The list of doctors with available time slots, where each doctor card should display:
        - Name
        - Specialty
        - Phone
        - Email
        - Available time slots
        - The "Delete" button     
```

## Acceptance Criteria
### 1. The Add Doctor Button
- It must be clickable to perform the "Add Doctor" functionality

### 2. The Log Out Button
- It must be clickable to perform the logout functionality for an authenticated admin user

### 3. Filter Doctors Form
- It must display the following fields:
  - Name Filter (input)
  - Time Filter (select)
  - Specialty Filter (select)

### 4. List of Doctors With Available Time Slots
- The system must automatically load the list of doctors with available time slots
- Each doctor must include:
    - Name
    - Specialty
    - Phone number
    - Email
    - Available time slots
    - The "Delete" button
- The "Delete" button must be clickable to delete the doctor 

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

### 2. Error Handling
```gherkin
Scenario: API error when loading doctors
  When the system fails to load doctors
  Then I should see an error message "Unable to load doctors"
  And I should have an option to retry
```

### 3. Access Admin Dashboard without authentication
```gherkin
Scenario: Access admin dashboard without authentication
  Given I am not authenticated
  When I try to access the admin dashboard URL
  Then I should be redirected to the index page
```

### 4. Access Admin Dashboard without proper authorization
```gherkin
Scenario: Access admin dashboard without proper authorization
  Given I am authenticated
  And I do not have the admin role
  When I try to access the admin dashboard URL
  Then I should be redirected to the index page
```

## Security
- Only authenticated admin users must be able to access the admin dashboard
- After logging out, the admin dashboard must no longer be accessible

## API Contract
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

## Additional Notes
- After logging in as an admin user, the system must automatically redirect the user to the admin dashboard page.
- This user story only includes what the admin dashboard must have.
- Every functionality in the admin dashboard is specified in its own user story.
