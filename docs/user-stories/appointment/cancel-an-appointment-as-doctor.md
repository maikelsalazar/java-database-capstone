# Cancel Appointment As Doctor

**Priority:** Medium
**Story Points:** 3

---

## User Story
_As an authenticated doctor user, I want to cancel a patient's appointment, so that I can have the time slot free._

---

## Feature (Gherkin)
```gherkin
Feature: Cancel a patient's appointment

Scenario: Cancel an appointment
  Given I am an authenticated doctor user
  And I see the list of my upcoming appointments in my calendar
  When I click the "Cancel" button on a patient's appointment
  Then a confirmation prompt should be displayed

  When I confirm the cancellation
  Then the patient's appointment must be cancelled
  And I should see the appointment as cancelled in the list of my upcoming appointments
```

## Acceptance Criteria
### 1. Cancel patient's appointment
- The system must update the status of the appointment to "cancelled"
- The system must display a confirmation message indicating that the appointment was cancelled
- The list of upcoming appointments must refresh automatically to reflect the updated status

### 2. Failing to cancel patient's appointment
- The patient's appointment must not change its status
- The system must display an error message

## Data Constraints
- Only upcoming appointments can be cancelled
- Past or already cancelled appointments cannot be cancelled again

## Security
- A doctor cannot cancel another doctor's appointment

## Functional Behavior
### Patient notification
- The patient must receive an email notification after the appointment is cancelled

## Edge cases
```gherkin
Scenario: Cancelling a non-existing patient's appointment
  When I try to cancel a non-existing patient's appointment
  Then the system should return a successful cancellation response  
```

```gherkin
Scenario: Do not confirm appointment cancellation 
  When the confirmation prompt is displayed
  And I dismiss the confirmation prompt
  Then the patient's appointment must not be cancelled
```

## API Contract (Backend)

**Request**
```http
PATCH /api/appointments/{id}/cancel/{token}
```

**Success or non-existing patient's appointment Response**
```http
Status Code: 200
{
  "status": "cancelled"
}
```

**Fail response**
Unauthorized
```http
Status Code: 401
{
  "success": false,
  "message": "Unauthorized"
}
```

Server error
```http
Status Code: 500
{
  "success": false,
  "message": "Error message"
}
```

## Additional Notes
### Non-existing patient's appointment handling:
- Non-existing patient appointments are treated as successful cancellations to avoid 
exposing sensitive system information
