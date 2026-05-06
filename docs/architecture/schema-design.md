# Schema Design

## MySQL Database Design

### Table admin
#### Basic definition
- id: Integer, Primary Key (@Id), Auto Increment
- username: String, Unique, Not Null
- password: String, Not Null
- created_at: LocalDateTime, Not Null, Auto generated, Not Updatable

#### MySQL details
| Column       | Datatype       | Primary Key | Not Null | Auto Increment |
|--------------|---------------|-------------|-----------|----------------|
| id           | INT           |  YES        | YES       | YES            |
| username     | VARCHAR(100)  |  NO         | YES       | NO             |
| password     | VARCHAR(100)  |  NO         | YES       | NO             |

**Indexes**
| Columns    | Type   | Additional |
|------------|--------|------------|
| username   | UNIQUE |            |

### Table doctors

#### Basic definition
- id: Integer, Primary Key (@Id), Auto Increment.
- name: String, Not Null.
- specialty: String, Not Null.
- email: String, Unique, Not Null.
- password: String, Not Null.
- phone: String, Not Null, Only Digits.


#### MySQL details
| Column       | Datatype       | Primary Key | Not Null | Auto Increment | 
|--------------|---------------|-------------|----------|-----------------|
| id           | INT           |  YES        | YES      | YES             |
| name         | VARCHAR(100)  |  NO         | YES      | NO              |
| specialty    | VARCHAR(50)   |  NO         | YES      | NO              |
| email        | VARCHAR(100)  |  NO         | YES      | NO              |
| password     | VARCHAR(100)  |  NO         | YES      | NO              |
| phone        | VARCHAR(10)   |  NO         | YES      | NO              |

**Indexes**
| Columns | Type   | Additional |
|---------|--------|------------|
| email   | UNIQUE |            |


### Table: doctor_available_times
| column         | datatype    | 
| doctor_id      | INT         |
| available_time | VARCHAR(20) |


### Table patients
#### Basic definition
- id: Integer, Primary Key (@Id), Auto Increment
- name: String, Not Null
- email: String, Unique, Not Null
- password: String, Not Null
- phone: String, Not Null, Only Digits
- address: String, Not Null

#### MySQL details
| Column        | Datatype                        | Primary Key | Not Null | Auto Incrememt | 
|---------------|---------------------------------|-------------|----------|----------------|
| id            | INT                             |  YES        | YES      | YES            |
| name          | VARCHAR(100)                    |  NO         | YES      | NO             |
| email         | VARCHAR(100)                    |  NO         | YES      | NO             |
| password      | VARCHAR(100)                    |  NO         | YES      | NO             |
| phone         | VARCHAR(10)                     |  NO         | YES      | NO             |
| address       | VARCHAR(255)                    |  NO         | YES      | NO             |

**Indexes**
| Columns | Type   | Additional |
|---------|--------|------------|
| email   | UNIQUE |            |

### Table appointments

#### Basic definition
- id: Integer, Primary Key (@Id), Auto Increment
- doctor_id: integer, ManyToOne (doctor), Not Null
- patient_id: integer, ManyToOne (patient), Not Null
- appointment_time: LocalDateTime, Not NUll
- Status: integer. (0=scheduled, 1=Completed, 2=cancelled)

#### MySQL details
| Column                 | Datatype | Primary Key | Not Null | Unique   | Auto Incrememt  |
|------------------------|----------|-------------|----------|----------|-----------------|
| id                    | INT       |  YES        | YES      | YES      |     Y           |
| doctor_id             | INT       |  NO         | YES      | NO       |                 |
| patient_id            | INT       |  NO         | YES      | NO       |                 |
| appointment_time      | DATETIME  |  NO         | YES      | NO       |                 |
| status                | INT       |  NO         | YES      | NO       |                 |


**Indexes**
| Columns                      | Type               | Additional                                                         |
|------------------------------|--------------------|--------------------------------------------------------------------|
| doctor_id, appointment_time  | UNIQUE             |                                                                    |
| doctor_id                    | FOREIGN KEY        | Table doctors, REFERENCES doctors.id = apppointments.doctor_id     |
| patient_id                   | FOREIGN KEY        | Table patients, REFERENCES patients.id = apppointments.patient_id  |


## MongoDB Design

### Collection prescriptions
```json
{
  "_id": ObjectId("6807dd712725f013281e7201"),
  "patientName": "John Smith",
  "appointmentId": 51,
  "medication": "Paracetamol",
  "dosage": "500mg",
  "doctorNotes": "Take 1 tablet every 6 hours."
}
```

### Restrictions:
- id: MongoDB Id, auto-generated
- appointmentId: appointment's entity id, valid long
- patientName: required, 3-100 characters
- medication: required, 3-100 characters.
- dosage: required, 3-20 characters.
- doctorNotes: not required, max 200 characters.
