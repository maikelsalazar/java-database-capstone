# Log Out As Admin

**Priority:** High  
**Story Points:** 3

---

## User Story
_As an authenticated admin user, I want to log out of the system, so that I can securely end my session._

---

## Feature (Gherkin)
```gherkin
Feature: Log Out As Admin

Scenario: Successfully log out as admin
  Given I am authenticated as an admin user
  When I click on the "Logout" button
  Then I should be redirected to the index page
  And my authentication token/session must be deleted
```

## Acceptance Criteria
### 1. Log Out 
- The authentication token/session must be deleted
- The user must be redirected to the index page

### 2. Prevent Access After Logout
- After logging out, the user must not be able to access the admin dashboard

## Edge Cases
```gherkin
Scenario: Access admin dashboard after logout
  Given I have logged out
  When I try to access the admin dashboard URL
  Then I should be redirected to the index page
```
