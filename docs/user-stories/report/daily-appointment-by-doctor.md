# Get Daily Appointment By Doctor Report

**Priority:** Low  
**Story Points:** 2

---

## User Story
_As a database admin, I want to get the Daily Appointment By Doctor Report, 
so that I can share it with stakeholders for data analysis._

---

## Feature (Gherkin)
```gherkin
Feature: Daily Appointment By Doctor Report

Scenario: View Daily Appointment By Doctor Report from MySQL CLI
  Given the following appointments exist:
    | doctor_name            | appointment_time       | status | patient_name   | patient_phone |
    | Dr. Emily Adams        | 2025-05-01 09:00:00    |  1     | Jane Doe       | 8881111111    |
    | Dr. Emily Adams        | 2025-05-01 14:00:00    |  2     | Michael Jordan | 8884444444    |
    | Dr. Mark Johnson       | 2025-05-02 10:00:00    |  1     | John Smith     | 8882222222    |
    | Dr. Tom Wilson         | 2025-05-03 14:00:00    |  1     | Emily Rose     | 8883333333    |
    
  And I am a database administrator
  When I connect to the MySQL CLI
  And I run the following statement "CALL `cms`.`GetDailyAppointmentReportByDoctor`('2025-05-01');"
  Then I should see the output:
    | doctor_name            | appointment_time       | status | patient_name   | patient_phone |
    | Dr. Emily Adams        | 2025-05-01 09:00:00    |  1     | Jane Doe       | 8881111111    |
    | Dr. Emily Adams        | 2025-05-01 14:00:00    |  2     | Michael Jordan | 8884444444    | 
```
**Appointment Status**
| status | meaning   |
|--------|-----------|
| 0      | scheduled |
| 1      | completed |
| 2      | cancelled |

## Acceptance Criteria
### 1. Generate Daily Appointment Report Successfully
- The system must return all appointments associated with the provided date
- The report must include:
  - Doctor name
  - Appointment time
  - Appointment status
  - Patient name
  - Patient phone number
- The report must only include appointments that match the requested date
- The report must be ordered by appointment time ascending

### 2. Empty Report Result
- If no appointments exist for the provided date, the system must return an empty result set
- The stored procedure execution must still complete successfully

### 3. Stored Procedure Execution
- The report must be generated through the MySQL stored procedure:
```sql
CALL cms.GetDailyAppointmentReportByDoctor(<date>);
```
- The stored procedure must accept a single date parameter

### 4. Data Integrity
- The report must only include valid appointments stored in the database
- The report must include appointments of all statuses

### 5. Access Control
- Only authorized database users should be able to execute the stored procedure

## Data Constraints
- The input parameter must be a valid date
- The report date format must follow YYYY-MM-DD, for example: 2025-03-01
- Appointment records must contain valid doctor and patient references

## Edge Cases
```gherkin
Scenario: No appointments found for the selected date
  Given there are no appointments scheduled for "2025-05-10"
  When I run the following statement "CALL cms.GetDailyAppointmentReportByDoctor('2025-05-10');"
  Then I should see an empty result set
```

```gherkin
Scenario: Invalid date format
  When I run the following statement "CALL cms.GetDailyAppointmentReportByDoctor('05-01-2025');"
  Then the system should return a database error
```

```gherkin
Scenario: Null date parameter
  When I run the following statement "CALL cms.GetDailyAppointmentReportByDoctor(NULL);"
  Then the system should return a database error
```

## SQL Contract

### Stored Procedure
```sql
CALL cms.GetDailyAppointmentReportByDoctor('2025-05-01');
```
### Expected Columns
```
doctor_name
appointment_time
status
patient_name
patient_phone
```

## Additional Notes 
- The report is intended for reporting and analytical purposes
- The stored procedure should optimize query performance for large appointment datasets
- The report output should be readable directly from the MySQL CLI environment
