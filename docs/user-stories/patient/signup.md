# Sign Up As Patient (Guest Access)

**Priority:** High  
**Story Points:** 5

---

## User Story
_As a guest user, I want to register as a patient, so that I can book appointments at the clinic._

---

## Feature (Gherkin)
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

## Acceptance Criteria
### 1. Signing up as patient successfully
- The system must store the new patient in the database
- The system must display a success message
- The modal must close after successful submission
- The patient dashboard must reload automatically

### 2. Failing to sign up as patient
- The patient must not be stored in the database
- The Sign Up form should display proper validation error messages

### 3. Sign Up Modal
- The Sign Up form must be displayed inside a modal
- The modal must contain:
    - Name input
    - Email input
    - Password input
    - Phone input
    - Address input

### 4. Logging in after successful registration
- - The patient must be able to log in using their registered email and password.

## Data Constraints
- The email address must be unique among patients
- All fields are required and mandatory
- Phone must be 10 digits

## Security Constraints
- Passwords must be stored encrypted/hashed
- Passwords must never be returned in API responses

## Functional Behavior
- The password input should hide typed characters
- The submit button should remain disabled until all required fields are valid

## Edge cases
```gherkin
Scenario: Email duplicated
  When the user enters an already registered email
  Then the system should not allow the registration
  And the user must see an error message
```

```gherkin
Scenario: Invalid data submitted
  When I enter invalid data
  And I submit the Sign Up form
  Then the system should not allow the registration
  And the user must see the proper validation error messages
```

```gherkin
Scenario: Close Sign Up modal
  When I click the close button
  Then the modal should be hidden
```

## API Contract (Backend) (Optional)
**Request**
```http
POST /api/patients

{
  "name": "John Smith",
  "email": "john.smith@example.com",
  "password": "patient@1234",
  "phone": "8887777777",
  "address": "101 Oak St, Cityville"
}

```

**Success Response**
```http
Status Code: 201
{
  "success": true,
  "message": "Patient registered successfully"
}
```

**Fail Response**
```http
Status Code: 400
{
  "success": false,
  "messages": {
    "email": "Email already exists",
    "phone": "Phone must contain 10 digits"
  }
}
```
