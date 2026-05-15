# Booking An Appointment With A Doctor

**Priority:** High  
**Story Points:** 8

---

## User Story
_As an authenticated patient user, I want to book an **hour-long** appointment with a doctor, 
so that I can consult with a doctor._

---

## Feature (Gherkin)
```gherkin
Feature: Booking An Appointment With A Doctor

Scenario: Successfully booking an appointment with a doctor
  Given I am an authenticated patient user
  And I am on the patient dashboard page
  When I locate a doctor in the list of available doctors
  And I click the "Book" button on the doctor's card
  Then the "Book Appointment" modal should be displayed
  And I should see "Book Appointment" form    
  
  When I enter valid appointment booking information
  And I click the "Confirm Booking" button
  Then the appointment must be stored
  And the modal must close
```  

## Acceptance Criteria
### 1. Booking an appointment successfully
- The system must store the new appointment in the database
- The system must display a success message
- The modal must close after successful submission
- The booked appointments list must refresh automatically

### 2. Failing to book an appointment
- The appointment must not be stored in the database
- The Book Appointment form should display proper validation error messages

### 3. Book Appointment Modal
- The Book Appointment form must be displayed inside a modal
- The modal must contain:
    - Patient's Name input _disabled_
    - Doctor's Name input _disabled_
    - Doctor's Specialty input _disabled_
    - Doctor's Email input _disabled_
    - Date input 
    - Doctor's available times select
    - Confirm Booking button

## Data Constraints
- A doctor must not have overlapping appointments
- Each appointment must have a duration of one hour

## Security Constraints
- Only authenticated patients may create appointments
- Patients must not be able to create appointments for other patients
- Appointment creation requests must require a valid authentication token/session

## Functional Behavior
- The available times select must display only time slots marked as available by the doctor
- The booking form should use a date picker input for easier date selection

## Edge cases
```gherkin
Scenario: Race condition on booking appointment
  When two or more users book an appointment at the same date and time simultaneously
  Then the system must allow only one booking to succeed
  And the remaining users should receive an error message indicating the time slot is no longer available
```

```gherkin
Scenario: Invalid data submitted
  When the user enters invalid data
  And I submit the "Book Appointment" form
  Then the system should not allow the booking
  And the user must see the proper validation error messages
```

```gherkin
Scenario: Close Book Appointment modal
  When I click the close button
  Then the modal should be hidden
```

## API Contract (Backend) (Optional)

> Note: The authentication token is sent in the URL path because it is required by the exercise specification.


**Request**
```http
POST /api/appointments/{token}

{
  "patient": {
    "id": 1
  },
  "doctor": {
    "id": 2
  },
  "appointmentTime": "{date}T{time}:00",
  "status": 0
}

```

**Success Response**
```http
Status Code: 201
{
  "success": true,
  "message": "Appointment booked successfully"
}
```

**Fail Response**
```http
Status Code: 400
{
  "success": false,
  "messages": "Failed to book appointment"
}
```
