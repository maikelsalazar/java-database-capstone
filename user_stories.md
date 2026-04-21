# User Story Template

**Title:**
_As a [user role], I want [feature/goal], so that [reason]._

**Acceptance Criteria:**
1. [Criteria 1]
2. [Criteria 2]
3. [Criteria 3]

**Priority:** [High/Medium/Low]
**Story Points:** [Estimated Effort in Points]
**Notes:**
- [Additional information or edge cases]

## Admin User Stories

### Log In As an Admin

```gherkin
Scenario: Successful login with valid credentials
  Given I am on the login page
  When I enter a valid username and password
  And I click the login button
  Then I should be redirected to "/dashboard"
  And I should see my username displayed on the screen
```

```gherkin
Scenario: Unsuccessful login with invalid credentials
  Given I am on the login page
  When I enter an invalid username or password
  And I click the login button
  Then I should remain on "/login"
  And I should see an error message "Invalid credentials"
```

```gherkin
Scenario: Logout from the system
  Given I am logged into the system
  When I click the logout button
  Then I should be redirected to "/login"
  And I should not see my username on the page
```

### Managing doctors

```gherkin
Scenario: Register a new doctor successfully
  Given I am logged in as an admin
  And I am on the doctors management page
  When I click the "Add Doctor" button
  And I fill in the form with valid doctor data
  And I click the "Register" button
  Then I should be redirected to the doctors list
  And I should see the doctor with the entered name in the list
```

```gherkin
Scenario: Registration fails with missing required fields
  Given I am logged in as an admin
  When I try to register a doctor without required fields
  Then I should see validation error messages
```

```gherkin
Scenario: Delete a doctor's profile successfully
  Given I am logged in as an admin
  And I am on the doctors management page
  And a doctor named "Dr. John Doe" exists in the list
  When I click the "Remove Doctor" button for "Dr. John Doe"
  And I confirm the deletion
  Then I should remain on the doctors management page
  And I should not see "Dr. John Doe" in the list
```
```gherkin
Scenario: Cancel doctor deletion
  Given I am logged in as an admin
  And I am on the doctors management page
  When I attempt to delete a doctor
  And I cancel the confirmation dialog
  Then the doctor should still be visible in the list
```

### Reports

```gherkin
Scenario: View monthly appointments from CLI
  Given the following appointments exist:
    | date       | patient       |
    | 2026-03-10 | John Doe      |
    | 2026-03-15 | Jane Smith    |
    | 2026-04-01 | Bob Brown     |
  When I run the "appointments-report" script
  Then I should see the output:
    """
    March 2026
    - John Doe
    - Jane Smith

    April 2026
    - Bob Brown
    """ 
```

### Patient's user stories
```gherkin
Scenario: View the list of available doctors
  Given I am not logged in
  And I am on the home page
  When I navigate to the doctors page
  Then I should see a list of doctors
  And each doctor should display name and specialty
```

### Registration and login

```gherkin
Scenario: Successful patient registration
  Given I am on the home page
  When I navigate to the patient registration form
  And I enter valid patient information
  And I click the "Register" button
  Then I should be redirected to "/login"
  And I should see a confirmation message "Registration successful"
```

```gherkin
Scenario: Login after successful registration
  Given I have registered as a patient
  When I log in with my email and password
  Then I should be redirected to the patient dashboard
```

```gherkin
Scenario: Logout from the system as a patient
  Given I am logged in as a patiend
  When I click the logout button
  Then I should be redirected to "/login"
  And I should see the login form
  And I should not see my username on the page
```

### Managing appointments

```gherkin
Scenario: Create a new appointment successfully
  Given I am logged in as a patient
  And I am on the booking page
  When I click the "Add Appointment" button
  And I enter appointment details with date "2026-04-20"
  And I click the "Save" button
  Then I should see an appointment scheduled for "2026-04-20"
```

```gherkin
Scenario: Update an existing appointment
  Given I am logged in as a patient
  And I am on the booking page
  And I have an appointment on "2026-04-20"
  When I edit the appointment and change the date to "2026-04-21"
  And I save the changes
  Then I should see the appointment scheduled for "2026-04-21"
  And I should not see an appointment on "2026-04-20"
```

```gherkin
Scenario: Update an existing appointment
  Given I am logged in as a patient
  And I am on the booking page
  And I see an appointment scheduled for "2026-04-20"
  When I click the "Edit Appointment" button for that appointment
  And I change the appointment date to "2026-04-21"
  And I click the "Save" button
  Then I should see an appointment scheduled for "2026-04-21"
  And I should not see an appointment on "2026-04-20
```

```gherkin
Scenario: Delete an existing appointment
  Given I am logged in as a patient
  And I am on the booking page
  And I see an appointment scheduled for "2026-04-20"
  When I click the "Cancel Appointment" button for that appointment
  And I confirm the deletion
  Then I should see a confirmation message "Appointment cancelled successfully"
  And I should no longer see an appointment scheduled for "2026-04-20"
```

```gherkin
Scenario: View my upcoming appointments
  Given I am logged in as a patient
  And I have upcoming appointments scheduled
  When I navigate to the patient dashboard
  Then I should see a section for upcoming appointments
  And I should see my upcoming appointments listed
```

## Doctor's user stories


### Login

```gherkin
Scenario: Log in as a doctor
  Given I am on the login page
  When I enter a valid username and password
  And I click the login button
  Then I should be redirected to the "Doctor Dashboard" page
  And I should see my username displayed
```

```gherkin
Scenario: Logout from the system as a doctor
  Given I am logged in as a doctor
  When I click the logout button
  Then I should be redirected to "/login"
  And I should see the login form
  And I should not see my username on the page
```

### Managing appointments

```gherkin
Scenario: View upcoming appointments as a doctor
  Given I am logged in as a doctor
  And I have upcoming appointments scheduled
  When I navigate to the doctor dashboard
  Then I should see a section for upcoming appointments
  And I should see my upcoming appointments listed
```

```gherkin
Scenario: View my appointment calendar to stay organized
  Given I am logged in as a doctor
  And I have past and upcoming appointments
  When I open the calendar view
  Then I should see appointments displayed by date
  And past appointments should be distinguishable from upcoming ones
```

```gherkin
Scenario: View patient details from an appointment
  Given I am logged in as a doctor
  And I have an appointment with "John Doe"
  When I open the appointment details
  And I click the "View Profile" button
  Then I should see the patient profile for "John Doe"
```

```gherkin
Scenario: Mark unavailability affecting existing appointments
  Given I am logged in as a doctor
  And I have appointments scheduled between "2026-04-20" and "2026-04-25"
  When I mark my absence from "2026-04-20" to "2026-04-25"
  Then the appointments in that period should be flagged as affected
  And the patients should be notified
```

```gherkin
Scenario: Mark unavailability as a doctor
  Given I am logged in as a doctor
  When I navigate to the doctor's dashboard
  And I click the "Mark Absence" button
  And I enter a start date and an end date
  And I confirm my absence
  Then my unavailability should be saved
```

### Managing profile

```gherkin
Scenario: Update my doctor's profile
  Given I am logged in as a doctor
  When I navigate to the doctor's dashboard
  And I click the "Profile" button
  And I update my profile information
  And I click the "Save" button
  Then I should see my updated profile information
```

```gherkin
Scenario: Patients see updated doctor profile
  Given a doctor has updated their profile information
  When a patient views the doctor's profile
  Then the updated information should be visible
```


