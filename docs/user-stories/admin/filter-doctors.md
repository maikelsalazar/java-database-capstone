# Feature: Filter Doctors

**Priority:** Low
**Story Points:** 3

---

## User Story
_As an authenticated admin user, I want to filter doctors from the list of doctors with available time slots, 
so that I can find the doctor I want to manage._

---

## Feature (Gherkin)

```gherkin
Feature: Filter doctors with available time slots

Scenario: Filter doctors by name, specialty, and time
  Given I am an authenticated admin user
  And I am on the admin dashboard page
  And I see a list of all available doctors
  And I have filters for name, specialty, and available time period (AM/PM or all)
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
- If a filter is not applied, the frontend must send "*" as the value
  - "*" for name means no name filtering (include all doctors)
  - "*" for time means no time filtering (include all time periods)
  - "*" for specialty means no specialty filtering (include any specialty)

**Considerations**
- Each doctor appears only once in the results.

## Functional Behavior

### 1. Sorting
- Results must be sorted by doctor name in ascending order
- Sorting must be handled by the backend response

### 2. Reset Filters
- When all filters are cleared, the page must display all available doctors

### 3. Loading State (Frontend UX)
- Show a loading indicator while results are being updated

### 4. No Results
- Show a "No doctors found matching the selected criteria" message

### 5. Name Filtering
- The system should avoid unnecessary filtering requests while the user is typing.
  
### 6. Filters handling
- Filters are optional and default to "*".
- Only non-"*" values are applied.
- All applied filters must be combined using AND logic.

## Edge Cases
```gherkin
Scenario: No matching doctors found
  When I apply filter criteria
  And no doctors match the selected criteria
  Then I should see a "No doctors found matching the selected criteria" message
  And the list should be empty
```

```gherkin
Scenario: All filters are wildcard
  Given I apply "*" for name, time, and specialty
  When I request filtered doctors
  Then I should see all available doctors
```

## API Contract (Backend)

__Request:__
```http
GET /api/doctors/filter/{name}/{time}/{specialty}
```

__Successful Response (with matches):__
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
    }
  ]
}
```

__Successful Response (no matches):__
```json
{
  "doctors": []
}
```

**Considerations**
- Backend must treat "*" as null-equivalent (no filtering condition)
- Path parameters are required in order: /name/time/specialty
- Use "*" as wildcard for any filter

## Additional Notes
- This feature belongs to the admin dashboard EPIC.
- If an error occurs while filtering doctors, it must be logged.
