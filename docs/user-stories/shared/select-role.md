# Select Role - Index Page

**Priority:** High  
**Story Points:** 3

---

## User Story
_As a user, I want to view and select the available system roles (Admin, Patient, Doctor), 
so that I can access the appropriate section of the system._

---

## Feature (Gherkin)

```gherkin
Feature: Select system role

Scenario: View available roles on the index page
  Given I access the system
  When the index page loads
  Then I should see the available system roles
  And I should see the "Admin" role option
  And I should see the "Patient" role option
  And I should see the "Doctor" role option
```  

## Acceptance Criteria
### 1. Index Page Access
- The index page must be publicly accessible
- The index page must load without authentication
- The index page must be the default application entry point

### 2. Available Roles
- The system must display the following roles:
  - Admin
  - Patient
  - Doctor
- Each role must be visually distinguishable
- Each role must include a selectable button or link

### 3. Role Selection
- Admin
    - Opens the admin login modal
- Patient
    - Redirects to the patient dashboard
- Doctor
    - Opens the doctor login modal

### 4. Navigation Consistency
- Users must be able to return to the index page from other public pages
- The system logo or name should redirect to the index page

## Functional Behavior
- Role options should be displayed immediately after the page loads
- The system name/logo should always remain visible

## Edge cases
```gherkin
Scenario: Invalid role route
  When a user accesses an invalid role path
  Then the system should display a "Page not found" message
```

## Additional Notes
- The index page acts as the public entry point of the system
- Authentication behavior is handled separately by each role

