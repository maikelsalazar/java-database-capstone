# Search Available Doctors

**Priority:** Low.
**Story Points:** 5

---

## User Story
_As a Patient, I want to filter doctors by name, time availability, and specialty,
so that I can find the most suitable doctor_

---

## Feature (Gherkin)

```gherkin
Feature: Search available doctors

Scenario: Filter doctors by name, specialty, and time
  Given I am on the "Available Doctors" page
  And I see a list of all available doctors
  And I have filters for name, specialty, and available time (AM/PM)
  When I apply any combination of filters
  Then the list should update dynamically
  And I should only see doctors matching all selected criteria
  And the results should be sorted by doctor name in ascending order
```

## Acceptance Criteria
### 1. Name filter:
- Partial match (e.g. "le" -> "Sarah Lee", "Lee Martin").
- Case insensitive.
- Ignores leading/trailing spaces.
- Matches against full doctor name.

### 2. Time filter:
- Options: AM or PM
- Based on doctor's available time slots:
    - AM: 00:00 ≤ time < 12:00
    - PM: 12:00 ≤ time ≤ 23:59
- A doctor is included if _at least one available time slot_ matches the selected time filter

### 3. Specialty Filter:
- Exact match from predefined specialties
- Case insensitive

### 4. Combination:
- All filters must be applied together using AND logic.
- If a filter is not provided, it must be ignored.

### 5. Data Constraints
- Each doctor appears only once in the results.

## Functional behavior
### 1. Sorting
- Results must be sorted by doctor name ascending
### 2. Reset Filters
- When all filters are cleared, the page must display all available doctors
### 3. Loading State (frontend UX)
- Show a loading indicator while results are being updated

## Edge cases

### 1. No Results
- Show a "No doctors found" message

## API Contract
__Request:__
```http
GET /api/doctor/filter/{name}/{time}/{specialty}
```

__Response:__
```json
{
  "doctors": [
    {
      "id": 1,
      "name": "Dr. Emily Adams",
      "specialty": "Cardiologist",
      "email": "dr.adams@example.com",
      "phone": "555-101-2020",
      "availableTimes": [
        "09:00-10:00",
        "10:00-11:00",
        "11:00-12:00",
        "14:00-15:00"
      ]
    },
    {
      "id": 2,
      "name": "Dr. Mark Johnson",
      "specialty": "Neurologist",
      "email": "dr.johnson@example.com",
      "phone": "555-202-3030",
      "availableTimes": [
        "10:00-11:00",
        "11:00-12:00",
        "14:00-15:00",
        "15:00-16:00"
      ]
    }
  ]
}
```

__Consideration__
- All filters are mandatory.
- Use "*" when a filter is not provided

__Examples__:

Using all filters:
```http
GET /api/doctor/filter/john/am/cardio

```
Using name and specialty filters:
```http
GET /api/doctor/filter/john/*/cardio
```
