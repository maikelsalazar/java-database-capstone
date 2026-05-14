# Login As Patient

**Priority:** High  
**Story Points:** 3

---

## User Story
_As a guest user, I want to log into the system, so that I can manage my bookings._

---

## Feature (Gherkin)
```gherkin
Feature: Login As Patient

Scenario: Successfully log in as patient
  Given I am not authenticated
  And I am on the role selection page
  
  When I select the "Patient" role
  Then I should be redirected to the public patient dashboard page
  
  When I click the "Log In" button
  Then the Log In modal should be displayed

  When I enter a valid email and password
  And I submit the Log In form
  Then I should be redirected to the protected patient dashboard
```  

## Acceptance Criteria
### 1. Successful Login With Valid Credentials
- The system must authenticate the patient user
- The user must be redirected to the protected patient dashboard
- The authenticated session must persist until the user logs out or the session expires

### 2. Unsuccessful Login With Invalid Credentials
- Authentication must fail
- The user must remain on the current page
- The system must display an "Invalid credentials" message

### 3. Patient Login Modal
- The login form must be displayed inside a modal
- The modal must contain:
    - Email input
    - Password input
    - Login button
    - Close button

## Data Constraints
- Email must follow a valid email format
- Password is required

## Security Constraints
- Passwords must never be returned in API responses
- Authentication tokens must only be generated for valid credentials
- Protected patient pages must require a valid authenticated session
- The system must prevent unauthorized access to protected patient pages

## Functional Behavior
- The password input should hide typed characters
- The login button should remain disabled until all required fields are filled
- The close button should hide the login modal

## Edge Cases
```gherkin
Scenario: Submit empty login form
  When I submit the login form without credentials
  Then I should see validation messages
```

```gherkin
Scenario: Close login modal
  When I click the close button
  Then the modal should be hidden
```

```gherkin
Scenario: Submit invalid credentials
  When I enter an invalid email or password
  And I submit the login form
  Then I should see an "Invalid credentials" message
  And I should remain on the current page
```

## API Contract (Backend)

**Request**
```http
POST /api/patients/login

{
"email": "john.smith@example.com",
"password": "patient@1234"
}
```

**Success Response**
```http
Status Code: 200

{
  "success": true,
  "token": "jwt-token",
  "message": "Login successful"
}
```

**Fail response**
```
Status Code: 401

{
  "success": false,
  "message": "Invalid credentials"
}
```
