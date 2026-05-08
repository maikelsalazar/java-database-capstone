# Get Doctor With Most Patients By Year

**Priority:** Low  
**Story Points:** 1

---

## User Story
_As a database admin, I want to identify the doctor with the most patients in a given year,
so that I can share this information with managers to help them generate annual performance summaries._

---

## Feature (Gherkin)
```gherkin
Feature: Get Doctor With Most Patients By Year Report

Scenario: Getting Doctor With Most Patients By Year Report from MySQL CLI
  Given the following appointments exist:
    | doctor_id | appointment_time     | patient_id |
    | 1         | 2025-05-01 09:00:00  |  1         |     
    | 1         | 2025-05-01 14:00:00  |  2         |
    | 2         | 2025-05-02 10:00:00  |  4         |
    | 2         | 2025-05-03 14:00:00  |  5         |
    | 1         | 2025-05-03 14:00:00  |  3         |
    
  And I am a database administrator
  When I connect to the MySQL CLI
  And I run the following statement "CALL `cms`.`GetDoctorWithMostPatientsByYear`(2025);"
  Then I should see the output:
    | doctor_id | patients_seen |
    | 1         | 3             |
```

## Acceptance Criteria
### 1. Generate Doctor With Most Patients Report Successfully
- The system must return the doctor who attended the highest number of patients for the provided year
- The report must include:
    - doctor_id
    - patients_seen
- The report must only include appointments that match the requested year
- The report must be ordered by patients_seen descending

### 2. Multiple Doctors with the same highest patient count
- If multiple doctors have the same highest patient count, the system must return the first result ordered by doctor_id ascending

### 3. Empty Report Result
- If no appointments exist for the provided year, the system must return an empty result set
- The stored procedure execution must still complete successfully

### 4. Stored Procedure Execution
- The report must be generated through the MySQL stored procedure:
```sql
CALL cms.GetDoctorWithMostPatientsByYear(<year>);
```
- The stored procedure must accept an integer parameter

### 5. Data Integrity
- The report must only include valid appointments stored in the database
- The report must include appointments of all statuses unless business rules say otherwise.

### 6. Access Control
- Only authorized database users should be able to execute the stored procedure

## Data Constraints
- The year parameter must be a positive integer

## Edge Cases

```gherkin
Scenario: No appointments found for the given year
  Given there are no appointments scheduled for the year 1990
  When I run the following statement "CALL cms.GetDoctorWithMostPatientsByYear(1990);"
  Then I should see an empty result set
```

```gherkin
Scenario: Invalid input
  When I run the following statement "CALL cms.GetDoctorWithMostPatientsByYear('invalid');"
  Then the system should return a database error
```

```gherkin
Scenario: Negative year parameter
  When I run the following statement "CALL cms.GetDoctorWithMostPatientsByYear(-2025);"
  Then I should see an empty result set
```

```gherkin
Scenario: Null parameter
  When I run the following statement "CALL cms.GetDoctorWithMostPatientsByYear(NULL);"
  Then I should see an empty result set because no records match the filter
```
> MySQL does not throw error on passing NULL value as int

## SQL Contract

### Stored Procedure
```sql
CALL cms.GetDoctorWithMostPatientsByYear(2025);
```
### Expected Columns
```
doctor_id
patients_seen
```

## Additional Notes
- The report is intended for reporting and analytical purposes
- The stored procedure should optimize query performance for large appointment datasets
- The report output should be readable directly from the MySQL CLI environment
