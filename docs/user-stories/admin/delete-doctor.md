# Delete Doctor

**Priority:** Medium  
**Story Points:** 3

---

## User Story
_As an authenticated admin user, I want to delete a doctor,
so that patients can no longer schedule appointments with that doctor._

---

## Feature (Gherkin)
```gherkin
Feature: Delete a doctor

Scenario: Successfully deleting a doctor
  Given I am an authenticated admin user
  And I am on the admin dashboard page
  When I locate a doctor in the list of available doctors
  And I click the "Delete" button on the doctor's card
  Then a confirmation prompt should be displayed

  When I confirm the deletion
  Then the doctor must be deleted
  And I should no longer see the deleted doctor in the list of available doctors
```  

## Acceptance Criteria
### 1. Deleting a doctor successfully
- The system must **soft** delete the doctor
- The system must display a confirmation message that the doctor was deleted
- The doctor list must refresh automatically.

### 2. Failing to delete a doctor
- The system must display an error message.

### 3. Deleted Doctor Visibility
- The doctor deleted must not appear in the list of available doctors anymore.
- The doctor deleted must not appear in the results of any search of available doctors anymore.

### 4. Unauthorized deletion
- The system must deny deletion requests from unauthenticated users
- The system must return HTTP 401 Unauthorized

## Data Constraints
- If the doctor to delete has appointments scheduled they must be canceled automatically
- Appointments cannot be scheduled with a deleted doctor

## Functional Behavior
### Appointments canceled automatically
- Affected patients must be notified that their appointment has been canceled via email

## Edge cases
```gherkin
Scenario: Deleting a non-existing doctor
  When I try to delete a non-existing doctor
  Then the system should return a successful deletion response  
```
```gherkin
Scenario: Cancel doctor deletion
  When the confirmation prompt is displayed
  And I cancel the deletion
  Then the doctor must not be deleted
```

## API Contract (Backend)

> Note: The authentication token is included in the URL path because it is a requirement of the exercise specification.


**Request**
```http
DELETE /api/doctors/{id}/{token}
```

**Success or non-existing doctor Response**
```http
Status Code: 200
{
  "success": true,
  "message": "Doctor deleted successfully"
}
```

**Fail response**
Unauthorized
```http
Status Code: 401
{
  "success": false,
  "message": "Error message"
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

### Clarification:
- Soft-deleted doctors must remain persisted in the database with an inactive/deleted status
- Soft-deleted doctors must be excluded from public and admin search results

---

## Additional Notes
### Non-existing doctor handling:
- Non-existing doctors are treated as successfully deleted to avoid exposing sensitive system information
