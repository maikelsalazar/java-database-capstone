# View Appointments

**Priority:** High  
**Story Points:** 5

---

## User Story
_As an authenticated doctor user, I want to view my appointment calendar, so that I can prepare accordingly._

---

## Feature (Gherkin)
```gherkin
Feature: View Appointments

Scenario: View appointment calendar
  Given I am authenticated as a doctor user
  When I am on the doctor dashboard
  Then I should see a list of my appointments organized in a calendar
```

## Acceptance Criteria
### 1. Appointment List
- The system must display all past and upcoming appointments
- Each appointment must include:
  - Patient's name
  - Appointment date and time

**Constraints**
- Each appointment must appear only once in th list

## Functional behavior
- Past and upcoming appointments must be visually distinguishable
- The patient's name must be clickable in order to view the patient's details
