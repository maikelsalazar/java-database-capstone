# Add Doctor

**Priority:** High  
**Story Points:** 5

---

## User Story
_As an authenticated admin user, I want to add a doctor, so that patients can schedule appointments with them._

---

## Feature (Gherkin)
```gherkin
Feature: Add a doctor

Scenario: Successfully adding a doctor
  Given I am an authenticated admin user
  And I am on the admin dashboard page
  When I click the "Add Doctor" button
  Then the Add Doctor modal should be displayed
  
  When I enter valid doctor information
  And I submit the Add Doctor form
  Then the new doctor must be stored
  And I should see the newly added doctor in the list of available doctors
```  

## Acceptance Criteria
### 1. Adding a new doctor successfully
- The system must store the new doctor in the database
- The system must display a success message
- The modal must close after successful submission
- The doctor list must refresh automatically

### 2. Failing to add a new doctor
- The doctor must not be stored in the database
- The Add Doctor form should display proper validation error messages

### 3. Add Doctor Modal
- The Add Doctor form must be displayed inside a modal
- The modal must contain:
  - Name input
  - Email input
  - Phone input
  - Password input
  - Specialty input
  - Available times text area

## Data Constraints
- The email address must be unique among doctors
- All fields are required and mandatory
- Phone must be 10 digits
- Available times should be in the form:
  ```
   09:00-10:00
   11:00-12:00
   13:00-14:00
   15:00-16:00
  ```

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
  When the user enters invalid data
  And I submit the Add Doctor form
  Then the system should not allow the registration
  And the user must see the proper validation error messages
```

```gherkin
Scenario: Close Add Doctor modal
  When I click the close button
  Then the modal should be hidden
```

## API Contract (Backend)
**Request**
```http
POST /api/doctors

{
  "name": "John doe",
  "email": "john.doe@email.com",
  "phone": "5551232222",
  "password": "doctor@1234",
  "specialty": "Cardiologist",
  "availableTimes": [
      "09:00-10:00",
      "11:00-12:00",
      "13:00-14:00",
      "15:00-16:00"
  ]
}

```

**Success Response**
```http
Status Code: 201
{
  "success": true,
  "message": "Doctor added successfully"
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
