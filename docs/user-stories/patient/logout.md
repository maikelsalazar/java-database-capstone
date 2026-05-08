# Log Out As Patient

**Priority:** High  
**Story Points:** 3

---

## User Story
_As an authenticated patient user, I want to log out of the system, so that I can securely end my session._

---

## Feature (Gherkin)
```gherkin
Feature: Log Out As Patient

Scenario: Successfully log out as patient
  Given I am authenticated as a patient user
  When I click on the "Log Out" button
  Then I should be redirected to the index page
  And my authentication token/session must be deleted
```

## Acceptance Criteria
### 1. Log Out
- The authentication token/session must be deleted
- The user must be redirected to the index page

### 2. Prevent Access After Logout
- After logging out, the user must not be able to access protected patient pages

## Security Constraints
- Authentication tokens/sessions must be invalidated after logout
- Protected patient pages must require a valid authenticated session
- Cached authenticated data must not remain accessible after logout

## Edge Cases
```gherkin
Scenario: Access protected patient pages after logout
  Given I have logged out
  When I try to access a protected patient page URL
  Then I should be redirected to the index page
```

```gherkin
Scenario: Access protected patient page after session expiration
  Given my authenticated session has expired
  When I try to access a protected patient page
  Then I should be redirected to the index page
```
