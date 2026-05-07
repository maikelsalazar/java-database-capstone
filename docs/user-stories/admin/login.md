# Login As Admin

**Priority:** High  
**Story Points:** 3

---

## User Story
_As an Admin user, I want to log into the system, so that I can access the admin dashboard._

---

## Feature (Gherkin)
```gherkin
Feature: Log in as Admin

Scenario: Successfully log in as admin
  Given I am not authenticated
  And I am on the select role page
  When I click on the "Admin" role button
  Then the admin login modal should be displayed

  When I enter a valid username and password
  And I submit the login form
  Then I should be redirected to the admin dashboard page
```  

## Acceptance Criteria
### 1. Successful Login With Valid Credentials
- The system must authenticate the admin user
- The user must be redirected to the admin dashboard page
- The authentication token/session must be stored after successful login

### 2. Unsuccessful Login With Invalid Credentials
- Authentication must fail
- The user must remain on the current page
- The system must display an "Invalid credentials" message

### 3. Admin Login Modal
- The login form must be displayed inside a modal
- The modal must contain:
    - Username input
    - Password input
    - Login button
    - Close button

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
  When I enter an invalid username or password
  And I submit the login form
  Then I should see an "Invalid credentials" message
  And I should remain on the current page
```
