# Log Out As Doctor

**Priority:** High  
**Story Points:** 3

---

## User Story
_As an authenticated doctor user, I want to log out of the system, so that I can securely end my session._

---

## Feature (Gherkin)
```gherkin
Feature: Log Out As Doctor

Scenario: Successfully log out as doctor
  Given I am authenticated as a doctor user
  When I click on the "Logout" button
  Then I should be redirected to the index page
  And my authentication token/session must be deleted
```

## Acceptance Criteria
### 1. Log Out
- The authentication token/session must be deleted
- The user must be redirected to the index page

### 2. Prevent Access After Logout
- After logging out, the user must not be able to access protected doctor pages

## Edge Cases
```gherkin
Scenario: Access protected doctor pages after logout
  Given I have logged out
  When I try to access a protected doctor page via URL
  Then I should be redirected to the index page
```
