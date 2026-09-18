# [Intelligent-College-Admission-System](https://github.com/KethineniVenkataAvinash/Intelligent-College-Admission-System)

<p align="center">
  <strong>Intelligent College Admission & Seat Allocation System</strong>
</p>

<p align="center">
  A modular, database-driven Java command-line application that integrates student admissions, merit ranking, counselling, seat allocation, finance, hostel, transport, and examination workflows.
</p>

<p align="center">
  <a href="https://github.com/KethineniVenkataAvinash/Intelligent-College-Admission-System">
    <img src="https://img.shields.io/badge/Repository-GitHub-181717?style=for-the-badge&logo=github&logoColor=white" alt="GitHub Repository">
  </a>
  <img src="https://img.shields.io/badge/Java-21-007396?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21">
  <img src="https://img.shields.io/badge/Maven-Build%20Tool-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven">
  <img src="https://img.shields.io/badge/JPA-Jakarta%20Persistence-59666C?style=for-the-badge" alt="Jakarta Persistence">
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Hibernate-ORM-59666C?style=for-the-badge&logo=hibernate&logoColor=white" alt="Hibernate ORM">
  <img src="https://img.shields.io/badge/MySQL-Database-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL">
  <img src="https://img.shields.io/badge/JUnit%205-Testing-25A162?style=for-the-badge&logo=junit5&logoColor=white" alt="JUnit 5">
  <img src="https://img.shields.io/badge/JDBC-Connectivity-007396?style=for-the-badge" alt="JDBC">
  <img src="https://img.shields.io/badge/CLI-Terminal%20Application-333333?style=for-the-badge" alt="Command Line Interface">
</p>

<p align="center">
  <em>Academic Project • Java • Maven • JPA • Hibernate • MySQL • JUnit 5</em>
</p>

---

## Table of Contents

- [Overview](#overview)
- [Problem Statement](#problem-statement)
- [Objectives](#objectives)
- [Core Capabilities](#core-capabilities)
- [Functional Modules](#functional-modules)
- [Complete Admission Lifecycle](#complete-admission-lifecycle)
- [System Architecture](#system-architecture)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Domain Model](#domain-model)
- [Database and Persistence](#database-and-persistence)
- [Business Rules](#business-rules)
- [Student Workflow](#student-workflow)
- [Administrator Workflow](#administrator-workflow)
- [Counselling and Seat Allocation](#counselling-and-seat-allocation)
- [Waitlist and Seat Release](#waitlist-and-seat-release)
- [Scholarship and Finance](#scholarship-and-finance)
- [Hostel Management](#hostel-management)
- [Transport Management](#transport-management)
- [Examination Management](#examination-management)
- [Security and Validation](#security-and-validation)
- [Testing Strategy](#testing-strategy)
- [Prerequisites](#prerequisites)
- [Database Setup](#database-setup)
- [Configuration](#configuration)
- [Installation](#installation)
- [Build](#build)
- [Run](#run)
- [Test](#test)
- [Command-Line Execution](#command-line-execution)
- [Important Classes and Services](#important-classes-and-services)
- [Design Principles](#design-principles)
- [Error Handling](#error-handling)
- [Project Documentation](#project-documentation)
- [Troubleshooting](#troubleshooting)
- [Project Limitations](#project-limitations)
- [Future Enhancements](#future-enhancements)
- [Academic Scope](#academic-scope)
- [Project Status](#project-status)
- [Author](#author)
- [License](#license)

---

## Overview

The **Intelligent College Admission & Seat Allocation System** is a Java-based command-line application that brings together the major stages of a college admission process into one integrated workflow.

Instead of treating registration, ranking, counselling, allocation, finance, hostel, transport, and examinations as independent activities, the system connects them through a common student and admission lifecycle.

The application uses a layered design with:

- Domain models
- Business services
- Repository and persistence components
- JPA and Hibernate ORM
- MySQL database storage
- Security and validation components
- Utility and exception-handling components
- Automated tests

The project is designed to run from a terminal without requiring a graphical user interface.

---

## Project Highlights

| Area | Implementation |
|---|---|
| Application Type | Command-Line / CLI |
| Programming Language | Java |
| Java Version | Java 21 |
| Build System | Apache Maven |
| Persistence API | Jakarta Persistence API (JPA) |
| ORM | Hibernate |
| Database | MySQL |
| Connectivity | JDBC |
| Testing | JUnit 5 |
| Architecture | Layered / Service-oriented structure |
| Source Control | Git |
| Repository | GitHub |

### Core Functional Coverage

```text
Student Registration
        |
        v
Application Management
        |
        v
Verification & Eligibility
        |
        v
Merit Ranking
        |
        v
Course Preferences
        |
        v
Counselling
        |
        v
Seat Allocation
        |
        +-----------------------+
        |                       |
        v                       v
   Seat Allocated           Waitlisted
        |                       |
        v                       |
   Scholarship                  |
        |                       |
        v                       |
      Finance                  |
        |                       |
        +-----------+-----------+
                    |
                    v
           Hostel / Transport
                    |
                    v
           Examination Process
                    |
                    v
             Hall Ticket
```

---

## Problem Statement

College admission is a multi-stage process involving several interconnected operations:

- Student registration
- Application submission
- Application verification
- Eligibility checking
- Merit ranking
- Course preference processing
- Category-based seat allocation
- Counselling
- Waitlist management
- Scholarship calculation
- Fee and payment management
- Hostel allocation
- Transport allocation
- Examination registration
- Examination centre allocation
- Examination room and seat allocation
- Hall-ticket generation

When these activities are managed manually or through disconnected systems, maintaining consistent admission records and coordinating the complete lifecycle becomes more difficult.

This project provides an integrated solution that connects these operations through a structured Java application and persistent database layer.

---

## Objectives

The project is designed to:

1. Automate the major stages of the college admission lifecycle.
2. Separate student and administrator workflows.
3. Maintain persistent admission data using MySQL.
4. Generate merit-based student ranks.
5. Process course preferences according to priority.
6. Support category-based seat allocation.
7. Provide counselling and waitlist management.
8. Process released seats and eligible waitlisted students.
9. Calculate scholarships using configured rank-based rules.
10. Manage tuition fees and payment information.
11. Allocate hostel rooms according to availability and configured rules.
12. Allocate students to transport routes and buses.
13. Manage examination registration and allocation.
14. Generate hall-ticket information.
15. Apply validation and structured exception handling.
16. Test important persistence, admission, allocation, waitlist, and concurrency workflows.
17. Maintain a modular and extensible Java codebase.

---

## Core Capabilities

### Student and Account Management

- Student registration
- Unique application number generation
- Student authentication
- Student profile management
- Academic information management
- Category and gender information
- Application status tracking

### Admission Processing

- Application creation
- Application submission
- Application verification
- Eligibility validation
- Course preference entry
- Merit rank generation
- Admission status management

### Allocation

- Course-wise seat management
- Category-wise seat matrix
- Merit-based allocation
- Preference-based allocation
- Seat availability checking
- Seat release
- Waitlist creation
- Waitlist promotion

### Counselling

- Counselling registration
- Counselling status tracking
- Seat allotment
- Freeze
- Float
- Slide
- Waitlist
- Completion status

### Scholarship

- Rank-based scholarship eligibility
- Scholarship amount calculation
- Scholarship persistence
- Fee integration

### Finance

- Tuition fee calculation
- Scholarship adjustment
- Net payable calculation
- Payment processing
- Transaction ID tracking
- Payment status
- Payment receipt generation

### Campus Services

- Hostel allocation
- Room allocation
- Occupancy tracking
- Capacity validation
- Bus route management
- Bus allocation
- Transport capacity tracking

### Examination

- Exam creation
- Exam schedule management
- Exam registration
- Examination centre allocation
- Room allocation
- Seat allocation
- Hall-ticket generation

### Administration

- Application verification
- Eligibility management
- Rank generation
- Seat allocation
- Seat matrix monitoring
- Counselling management
- Waitlist management
- Examination management
- Hostel monitoring
- Transport monitoring
- Notifications
- Reports and summaries

---

## Functional Modules

| Module | Main Responsibility |
|---|---|
| Student Management | Registration, authentication, profile and student data |
| Application Management | Application creation, submission, verification and eligibility |
| Course Management | Course information and student preferences |
| Ranking Management | Merit rank generation |
| Counselling Management | Counselling registration and status tracking |
| Seat Allocation | Merit, category, preference and capacity-based allocation |
| Waitlist Management | Waitlist creation, seat release and promotion |
| Scholarship Management | Rank-based scholarship calculation |
| Payment Management | Fee processing and payment records |
| Hostel Management | Hostel, room, capacity and allocation management |
| Transport Management | Routes, buses, capacity and student assignment |
| Examination Management | Exams, schedules, registration and allocation |
| Hall Ticket Management | Hall-ticket information generation |
| Reporting | Admission and operational summaries |
| Security | Authentication and authorization |
| Validation | Input and business-rule validation |

---

## Complete Admission Lifecycle

```text
+---------------------------+
|    Student Registration   |
+-------------+-------------+
              |
              v
+---------------------------+
|   Application Creation    |
+-------------+-------------+
              |
              v
+---------------------------+
|   Course Preferences      |
+-------------+-------------+
              |
              v
+---------------------------+
| Application Verification  |
+-------------+-------------+
              |
              v
+---------------------------+
|   Eligibility Checking    |
+-------------+-------------+
              |
              v
+---------------------------+
|     Merit Rank Generation |
+-------------+-------------+
              |
              v
+---------------------------+
|       Counselling         |
+-------------+-------------+
              |
              v
+---------------------------+
|      Seat Allocation      |
+-------------+-------------+
              |
        +-----+-----+
        |           |
        v           v
+---------------+ +---------------+
| Seat Allocated| |   Waitlisted  |
+-------+-------+ +-------+-------+
        |                 |
        v                 |
+---------------+         |
|  Scholarship  |         |
+-------+-------+         |
        |                 |
        v                 |
+---------------+         |
| Fee Calculation|        |
+-------+-------+         |
        |                 |
        v                 |
+---------------+         |
|    Payment    |         |
+-------+-------+         |
        |                 |
        +--------+--------+
                 |
                 v
+---------------------------+
|    Hostel / Transport    |
+-------------+-------------+
              |
              v
+---------------------------+
|   Examination Registration|
+-------------+-------------+
              |
              v
+---------------------------+
| Exam Centre / Room / Seat|
+-------------+-------------+
              |
              v
+---------------------------+
|       Hall Ticket        |
+---------------------------+

Seat Release
      |
      v
Waitlist Promotion
      |
      v
Available Student
      |
      v
Seat Allocation Update
```

---

## System Architecture

The application follows a layered architecture to separate the user interface, business rules, persistence operations, and database responsibilities.

```text
+----------------------------------------------------------------+
|                     PRESENTATION LAYER                         |
|                                                                |
|                 Console Interface / Main.java                  |
+-------------------------------+--------------------------------+
                                |
                                v
+----------------------------------------------------------------+
|                       SERVICE LAYER                            |
|                                                                |
| Student | Application | Ranking | Counselling | Allocation     |
| Scholarship | Payment | Hostel | Transport | Examination       |
| Reports | Notifications | Verification                         |
+-------------------------------+--------------------------------+
                                |
                                v
+----------------------------------------------------------------+
|                     REPOSITORY LAYER                            |
|                                                                |
|              JPA Repository / EntityManager                     |
+-------------------------------+--------------------------------+
                                |
                                v
+----------------------------------------------------------------+
|                    PERSISTENCE LAYER                            |
|                                                                |
|                Jakarta Persistence + Hibernate                  |
+-------------------------------+--------------------------------+
                                |
                                v
+----------------------------------------------------------------+
|                       DATABASE LAYER                            |
|                                                                |
|                            MySQL                                |
+----------------------------------------------------------------+
```

### Layer Responsibilities

#### Presentation Layer

`Main.java` provides the command-line interface and coordinates student and administrator menus.

#### Service Layer

The service layer contains business logic and coordinates the individual admission modules.

Examples:

```text
ApplicationService
ApplicationVerificationService
RankingService
CounsellingService
AllocationService
ScholarshipService
PaymentService
HostelService
TransportService
ExamService
```

#### Repository Layer

The repository layer performs persistence operations through JPA components and the `EntityManager`.

#### Persistence Layer

Jakarta Persistence API and Hibernate ORM map Java entities to relational database tables.

#### Database Layer

MySQL provides persistent storage for application data.

---

## Technology Stack

| Technology | Role |
|---|---|
| Java 21 | Application development |
| Apache Maven | Build and dependency management |
| Jakarta Persistence API | Persistence abstraction |
| Hibernate ORM | Object-relational mapping |
| MySQL | Relational database |
| MySQL Connector/J | MySQL JDBC driver |
| JDBC | Database connectivity |
| JUnit 5 | Automated testing |
| Git | Version control |
| GitHub | Repository hosting |

---

## Project Structure

```text
Intelligent-College-Admission-System/
|
+-- pom.xml
+-- README.md
+-- statement.md
|
+-- src/
    |
    +-- main/
    |   |
    |   +-- java/
    |   |   |
    |   |   +-- com/
    |   |       |
    |   |       +-- college/
    |   |           |
    |   |           +-- admission/
    |   |               |
    |   |               +-- Main.java
    |   |               +-- annotation/
    |   |               +-- enums/
    |   |               +-- exception/
    |   |               +-- jdbc/
    |   |               +-- jpa/
    |   |               +-- model/
    |   |               +-- security/
    |   |               +-- service/
    |   |               +-- util/
    |   |
    |   +-- resources/
    |       |
    |       +-- META-INF/
    |           |
    |           +-- persistence.xml
    |
    +-- test/
        |
        +-- java/
```

### Package Responsibilities

| Package | Responsibility |
|---|---|
| `model` | Domain entities and application data models |
| `service` | Business logic and application workflows |
| `jpa` | JPA persistence and database operations |
| `jdbc` | JDBC-related database functionality |
| `security` | Authentication, authorization and password utilities |
| `enums` | Application status, category, gender and related enumerations |
| `exception` | Custom application exceptions |
| `annotation` | Custom authorization or application annotations |
| `util` | Reusable helper and utility functionality |

---

## Domain Model

The application models the major entities involved in the admission lifecycle.

### Major Entities

```text
Student
Application
Course
CoursePreference
Counselling
Seat
SeatAllocation
Waitlist
Scholarship
Payment
PaymentReceipt
Hostel
HostelRoom
HostelAllocation
Bus
BusRoute
TransportAllocation
Exam
ExamSchedule
ExamRegistration
ExamAllocation
HallTicket
Notification
Admin
```

### Domain Relationship Overview

```text
                         +-------------+
                         |   Student   |
                         +------+------+
                                |
              +-----------------+------------------+
              |                 |                  |
              v                 v                  v
       +-------------+   +-------------+    +-------------+
       | Application |   | Scholarship |    |   Payment   |
       +------+------+   +-------------+    +-------------+
              |
      +-------+--------+
      |                |
      v                v
+-------------+  +-------------+
| Preferences |  | Counselling |
+-------------+  +------+------+
                        |
                        v
                +---------------+
                | SeatAllocation|
                +-------+-------+
                        |
                  +-----+-----+
                  |           |
                  v           v
             +---------+  +---------+
             | Hostel  |  |Transport|
             +---------+  +---------+
                        |
                        v
                +---------------+
                |   Examination |
                +-------+-------+
                        |
                        v
                 +-------------+
                 | Hall Ticket |
                 +-------------+
```

---

## Database and Persistence

The application uses **MySQL** for persistent storage.

The persistence layer uses:

- Jakarta Persistence API
- Hibernate ORM
- JPA entities
- JPA annotations
- `EntityManager`
- Repository components
- MySQL Connector/J

### Persistence Unit

```text
collegeAdmissionPU
```

### Persistence Configuration

```text
src/main/resources/META-INF/persistence.xml
```

The persistence layer separates database operations from the application's business logic.

### Persistence Responsibilities

```text
Java Entity
     |
     v
JPA Mapping
     |
     v
Hibernate ORM
     |
     v
JDBC Driver
     |
     v
MySQL
```

---

## Business Rules

The application contains configurable business rules for ranking, scholarships, allocation, capacity, and admission processing.

### Merit Ranking

The ranking workflow uses academic percentage to generate merit order.

```text
Higher Academic Percentage
            |
            v
Higher Merit Priority
            |
            v
Lower Rank Number
```

Application number may be used as a tie-breaking value where required.

### Scholarship Rules

The configured rank-based scholarship rules are:

| Rank Range | Scholarship |
|---|---:|
| 1 - 500 | 100% |
| 501 - 2,000 | 50% |
| 2,001 - 5,000 | 25% |
| Above 5,000 | 0% |

### Category Configuration

The configured categories are:

```text
GENERAL
OBC
SC
ST
EWS
```

### Counselling States

```text
NOT_STARTED
REGISTERED
ALLOTTED
FREEZE
FLOAT
SLIDE
WAITLISTED
COMPLETED
```

---

## Student Workflow

### 1. Registration

The student provides required information such as:

- Name
- Date of birth
- Gender
- Address
- Email
- Phone number
- Academic information
- Category
- Password

A unique application number is generated.

### 2. Application

The student creates an admission application.

### 3. Course Preferences

The student enters preferred courses in priority order.

### 4. Verification

The application is reviewed by the administrator.

### 5. Eligibility

The system validates the application before ranking and counselling.

### 6. Ranking

Eligible students receive a merit rank.

### 7. Counselling

The student registers for counselling and participates in the allocation workflow.

### 8. Allocation

The system considers rank, category, preferences, eligibility, and available seats.

### 9. Scholarship and Finance

The system calculates applicable scholarship and fee information.

### 10. Campus Services

The student can be processed for hostel and transport services.

### 11. Examination

The student can register for an examination and receive centre, room, seat, and hall-ticket information.

---

## Administrator Workflow

The administrator workflow includes:

```text
Administrator Login
        |
        v
Application Verification
        |
        v
Eligibility Management
        |
        v
Rank Generation
        |
        v
Course / Seat Monitoring
        |
        v
Counselling Management
        |
        v
Seat Allocation
        |
        v
Waitlist / Seat Release
        |
        v
Scholarship / Finance
        |
        v
Hostel Monitoring
        |
        v
Transport Monitoring
        |
        v
Examination Management
        |
        v
Reports / Notifications
```

---

## Counselling and Seat Allocation

`CounsellingService` manages counselling registration and counselling states.

`AllocationService` coordinates seat allocation.

The allocation process considers:

1. Student eligibility
2. Counselling participation
3. Course preferences
4. Merit rank
5. Student category
6. Course capacity
7. Available seats
8. Category-wise seat matrix

### Allocation Decision

```text
Student
  |
  +--> Eligibility
  |
  +--> Rank
  |
  +--> Category
  |
  +--> Course Preference
  |
  +--> Available Seat
          |
          v
   Allocation Decision
```

### Seat Matrix

`SeatMatrixService` maintains seat availability information for configured courses and categories.

---

## Waitlist and Seat Release

The system supports waitlist processing when a student cannot receive a seat during an allocation cycle.

The waitlist subsystem supports:

- Waitlist position tracking
- Seat release
- Eligibility verification
- Promotion of eligible students
- Allocation updates

Important services include:

```text
SeatReleaseService
WaitlistService
WaitlistPromotionService
```

### Waitlist Flow

```text
Seat Unavailable
      |
      v
Student Waitlisted
      |
      v
Seat Released
      |
      v
Waitlist Evaluation
      |
      v
Eligible Student
      |
      v
Seat Promotion
      |
      v
Allocation Updated
```

---

## Scholarship and Finance

### Scholarship

`ScholarshipService` calculates scholarship eligibility using the configured rank-based rules.

The scholarship amount is integrated with fee calculation.

### Finance

The finance subsystem supports:

- Tuition fee calculation
- Scholarship deduction
- Net payable calculation
- Amount paid
- Transaction ID
- Payment status
- Payment records
- Receipt generation

Main services:

```text
PaymentService
PaymentReceiptService
```

### Finance Flow

```text
Course Fee
    |
    v
Scholarship Calculation
    |
    v
Scholarship Deduction
    |
    v
Net Payable
    |
    v
Payment
    |
    v
Transaction Record
    |
    v
Receipt
```

---

## Hostel Management

The hostel subsystem manages hostel and room allocation.

Configured hostel examples include:

```text
BH01 - Boys Hostel A
BH02 - Boys Hostel B
GH01 - Girls Hostel A
GH02 - Girls Hostel B
```

Supported room types:

```text
SINGLE
DOUBLE
TRIPLE
```

The hostel subsystem tracks:

- Hostel capacity
- Room capacity
- Occupancy
- Student allocation
- Availability
- Admission rank

Main service:

```text
HostelService
```

### Hostel Allocation Flow

```text
Student
   |
   v
Hostel Eligibility
   |
   v
Available Hostel
   |
   v
Available Room
   |
   v
Capacity Validation
   |
   v
Room Allocation
   |
   v
Occupancy Update
```

---

## Transport Management

The transport subsystem manages:

- Bus routes
- Multiple buses
- Route capacity
- Bus capacity
- Occupancy tracking
- Student transport allocation
- Bus and route assignment

Main service:

```text
TransportService
```

### Transport Flow

```text
Student
   |
   v
Transport Request
   |
   v
Available Route
   |
   v
Available Bus
   |
   v
Capacity Validation
   |
   v
Transport Allocation
   |
   v
Occupancy Update
```

---

## Examination Management

The examination module manages the examination lifecycle.

### Supported Operations

- Exam creation
- Exam schedules
- Exam sessions
- Student registration
- Examination-centre allocation
- Room allocation
- Seat allocation
- Hall-ticket generation

Main services:

```text
ExamService
ExamRegistrationService
HallTicketService
```

### Examination Flow

```text
Exam Creation
      |
      v
Exam Schedule
      |
      v
Student Registration
      |
      v
Exam Centre Allocation
      |
      v
Room Allocation
      |
      v
Seat Allocation
      |
      v
Hall Ticket Generation
```

### Hall Ticket

`HallTicketService` generates hall-ticket information using:

- Student details
- Admission details
- Examination details
- Examination centre
- Room number
- Seat number
- Exam date
- Exam session

Generated hall-ticket files are stored in the configured hall-ticket output directory.

---

## Security and Validation

The application provides separate student and administrator workflows.

Security-related components include:

- Authentication services
- Role management
- Password utilities
- Authorization annotations
- Access-control logic
- Input validation
- Custom exceptions

Typical roles are:

```text
STUDENT
ADMIN
```

### Security Flow

```text
Credentials
    |
    v
Authentication
    |
    v
Role Identification
    |
    v
Authorization
    |
    +-------------------+
    |                   |
    v                   v
 STUDENT              ADMIN
 Workflow             Workflow
```

Application credentials and database passwords should never be committed to the repository.

---

## Testing Strategy

The project includes tests for important persistence, allocation, admission, waitlist, and concurrency-related workflows.

### Test Areas

- Student JPA operations
- Course persistence
- Seat persistence
- Seat allocation
- Transport allocation
- Admission workflow
- Waitlist promotion
- Seat release
- Hostel concurrency
- Transport concurrency

### Representative Test Classes

```text
StudentJpaTest
CourseJpaTest
SeatJpaTest
SeatAllocationJpaTest
TransportAllocationJpaTest
AdmissionFlowIntegrationTest
WaitlistSeatReleaseVerificationTest
HostelConcurrencyVerificationTest
TransportConcurrencyVerificationTest
```

### Run All Tests

```bash
mvn test
```

### Expected Maven Result

```text
BUILD SUCCESS
```

---

## Prerequisites

Before running the application, install and configure the following.

### Java

Java 21 or a compatible JDK.

Verify:

```bash
java -version
```

### Maven

Maven 3.x.

Verify:

```bash
mvn -version
```

### MySQL

MySQL Server must be installed and running.

Verify:

```bash
mysql --version
```

---

## Database Setup

Create the application database:

```sql
CREATE DATABASE college_admission;
```

Verify the database:

```sql
SHOW DATABASES;
```

The application expects MySQL to be accessible through the configured JDBC connection.

---

## Configuration

The application requires database connection settings.

### Required Configuration

```text
DB_URL
DB_USERNAME
DB_PASSWORD
HIBERNATE_DDL_AUTO
```

### Example Configuration

```text
DB_URL=jdbc:mysql://localhost:3306/college_admission?useSSL=false&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=your_password
HIBERNATE_DDL_AUTO=update
```

Replace `your_password` with the password configured for your local MySQL account.

> Never commit real credentials, passwords, API keys, or other secrets to GitHub.

### PowerShell Configuration

For a temporary Windows PowerShell session:

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/college_admission?useSSL=false&serverTimezone=UTC"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_password"
$env:HIBERNATE_DDL_AUTO="update"
```

Verify:

```powershell
echo $env:DB_URL
echo $env:DB_USERNAME
```

---

## Installation

### Step 1: Clone the Repository

```bash
git clone https://github.com/KethineniVenkataAvinash/Intelligent-College-Admission-System.git
```

### Step 2: Enter the Project Directory

```bash
cd Intelligent-College-Admission-System
```

### Step 3: Start MySQL

Make sure the MySQL server is running.

### Step 4: Create the Database

```sql
CREATE DATABASE college_admission;
```

### Step 5: Configure Database Credentials

Set the environment variables described in the [Configuration](#configuration) section.

### Step 6: Compile the Project

```bash
mvn clean compile
```

A successful compilation should end with:

```text
BUILD SUCCESS
```

---

## Build

To create the Maven package:

```bash
mvn clean package
```

Build artifacts are generated under:

```text
target/
```

For a clean rebuild:

```bash
mvn clean
mvn compile
```

---

## Run

The main application entry point is:

```text
com.college.admission.Main
```

Run the application through Maven:

```bash
mvn exec:java -Dexec.mainClass=com.college.admission.Main
```

The application starts the command-line interface and provides the available student and administrator workflows.

---

## Command-Line Execution

The project is designed for terminal-based execution.

Typical execution sequence:

```text
1. Start MySQL
2. Create college_admission database
3. Configure database credentials
4. Open terminal in project root
5. Compile project
6. Start application
7. Select Student or Admin workflow
8. Perform the required operation
```

### Quick Start

```bash
git clone https://github.com/KethineniVenkataAvinash/Intelligent-College-Admission-System.git
cd Intelligent-College-Admission-System
mvn clean compile
mvn exec:java -Dexec.mainClass=com.college.admission.Main
```

The database must be configured before the application is started.

---

## Important Classes and Services

### Application Entry Point

```text
Main.java
```

Provides the main command-line application workflow.

### Student and Authentication

```text
StudentRegistrationService
StudentAuthenticationService
```

Responsible for registration and student authentication.

### Application Processing

```text
ApplicationService
ApplicationVerificationService
```

Responsible for application creation, submission, verification, and eligibility-related processing.

### Courses

```text
CourseService
```

Responsible for course-related operations and course data.

### Ranking

```text
RankingService
```

Responsible for merit rank generation.

### Counselling

```text
CounsellingService
```

Responsible for counselling registration and status management.

### Seat Allocation

```text
AllocationService
SeatMatrixService
SeatReleaseService
WaitlistService
WaitlistPromotionService
```

Responsible for seat availability, allocation, seat release, waitlist management, and promotion.

### Scholarship

```text
ScholarshipService
```

Responsible for scholarship calculation and persistence.

### Finance

```text
PaymentService
PaymentReceiptService
```

Responsible for payment processing and receipt generation.

### Hostel

```text
HostelService
```

Responsible for hostel and room allocation.

### Transport

```text
TransportService
```

Responsible for buses, routes, capacities, and student transport allocation.

### Examination

```text
ExamService
ExamRegistrationService
HallTicketService
```

Responsible for examination schedules, registrations, centre allocation, room allocation, seat allocation, and hall tickets.

### Reporting

```text
AdmissionReportService
```

Responsible for admission-related reporting and summaries.

---

## Repository and Persistence Layer

The project uses JPA repositories and persistence components for database operations.

Major persisted domain areas include:

```text
Student
Course
Application
Seat
SeatAllocation
Exam
ExamSchedule
ExamRegistration
ExamAllocation
HallTicket
Scholarship
Payment
Hostel
Transport
Notification
```

The persistence layer keeps database access separate from the business service layer.

---

## Design Principles

### Separation of Concerns

```text
Model       -> Domain Data
Service     -> Business Logic
Repository  -> Persistence
Security    -> Authentication / Authorization
Utility     -> Common Operations
Exception   -> Error Handling
```

### Modularity

Each major admission capability is implemented through dedicated services or components.

### Maintainability

The package structure separates responsibilities and allows individual modules to evolve independently.

### Reusability

Common persistence and utility functionality is centralized where appropriate.

### Persistence Abstraction

JPA and Hibernate provide an abstraction between Java objects and relational database storage.

### Validation

Input and business-rule validation is performed before important operations.

### Extensibility

The service-oriented structure provides a foundation for adding APIs, web interfaces, notification systems, analytics, and deployment infrastructure in future versions.

---

## Error Handling

The application uses structured exception handling for different categories of failures.

Examples include:

- Authentication errors
- Validation errors
- Admission errors
- Allocation errors
- Database errors

Custom exceptions and validation mechanisms help prevent invalid operations and provide controlled handling of application failures.

### General Error Flow

```text
User Input
    |
    v
Validation
    |
    +------ Invalid ------> Exception Handling
    |
    v
Business Rule Validation
    |
    +------ Invalid ------> Exception Handling
    |
    v
Service Operation
    |
    v
Persistence Operation
    |
    +------ Failure ------> Database / Application Exception
    |
    v
Successful Result
```

---

## Project Documentation

The project documentation covers:

- Problem Statement
- Objectives
- Functional Requirements
- Non-Functional Requirements
- System Architecture
- Workflow
- Use Case Design
- Class Design
- Sequence Design
- Entity Relationship Design
- Database Schema
- Implementation Details
- Testing
- Results
- Challenges
- Learnings
- Future Enhancements

The repository also contains:

```text
statement.md
```

for the project statement and supporting documentation.

---

## Troubleshooting

### `mysql` is not recognized

Verify that MySQL Server and its command-line tools are installed and that the MySQL `bin` directory is available in the system `PATH`.

Then reopen the terminal and run:

```bash
mysql --version
```

### Database Connection Failure

Check:

1. MySQL Server is running.
2. Database `college_admission` exists.
3. Username is correct.
4. Password is correct.
5. JDBC URL points to the correct host and port.
6. The MySQL Connector/J dependency is available through Maven.

### Maven Compilation Failure

Run:

```bash
mvn clean compile
```

If dependency resolution is required, run the command again after confirming network connectivity and Maven configuration.

### Application Does Not Start

Confirm that:

```text
com.college.admission.Main
```

is the correct application entry point and that the project has compiled successfully.

Run:

```bash
mvn exec:java -Dexec.mainClass=com.college.admission.Main
```

### Tests Fail Because of Database Configuration

Confirm that the test environment can access the required MySQL database and that the persistence configuration is correct.

---

## End-to-End Scenario

A complete student journey can be summarized as:

```text
                    STUDENT
                       |
                       v
                 Registration
                       |
                       v
                  Application
                       |
                       v
              Course Preferences
                       |
                       v
                  Verification
                       |
                       v
                  Eligibility
                       |
                       v
                  Merit Rank
                       |
                       v
                 Counselling
                       |
                       v
                Seat Allocation
                  /         \
                 /           \
                v             v
           Allocated       Waitlisted
                |             |
                v             |
           Scholarship        |
                |             |
                v             |
         Fee Calculation      |
                |             |
                v             |
             Payment          |
                |             |
                +------+------+
                       |
                       v
              Hostel / Transport
                       |
                       v
              Exam Registration
                       |
                       v
              Exam Allocation
                       |
                       v
                 Hall Ticket

                Seat Release
                     |
                     v
              Waitlist Review
                     |
                     v
             Student Promotion
                     |
                     v
              Updated Allocation
```

---

## Project Limitations

The current implementation has the following limitations:

- The application is command-line based.
- No web frontend is included.
- No REST API is included.
- The application requires a locally configured MySQL environment.
- Course, hostel, and transport configurations are system-defined.
- The project is primarily intended for academic demonstration and evaluation.
- Production deployment would require additional infrastructure, monitoring, security hardening, backup strategy, logging, and operational configuration.

---

## Future Enhancements

The modular structure provides a foundation for future improvements.

### Web Application

Add a browser-based student portal and administrator dashboard.

### REST API

Expose admission operations through RESTful APIs.

### Advanced Authentication

Introduce token-based authentication such as JWT or OAuth for API-based access.

### Notifications

Integrate:

- Email notifications
- SMS notifications
- Application status alerts
- Seat allocation notifications

### Document Management

Add:

- Online document uploads
- Document verification
- Digital admission documents

### Advanced Counselling

Add more configurable counselling rounds, allocation policies, and preference-processing strategies.

### Analytics

Add:

- Admission statistics
- Course demand analysis
- Seat utilization analytics
- Category-wise reports
- Counselling analytics

### Deployment

Potential deployment improvements include:

- Docker
- Cloud database
- Cloud application hosting
- CI/CD pipelines

### Reporting

Add automated:

- PDF reports
- Excel reports
- Admission letters
- Fee receipts
- Hall tickets

---

## Academic Scope

This project demonstrates practical application of:

- Object-Oriented Programming
- Java programming
- Collections
- Data processing
- Exception handling
- File handling
- JDBC
- JPA
- Hibernate ORM
- MySQL
- CRUD operations
- Database persistence
- Layered architecture
- Authentication
- Authorization
- Business-rule implementation
- Input validation
- Automated testing
- Integration testing
- Concurrency-related testing
- Modular software design
- Maven
- Git
- GitHub

---

## Project Status

| Property | Details |
|---|---|
| Project Type | Academic / Educational Project |
| Application Type | Command-Line Application |
| Language | Java |
| Java Version | Java 21 |
| Build Tool | Apache Maven |
| Persistence | Jakarta Persistence API |
| ORM | Hibernate |
| Database | MySQL |
| Connectivity | JDBC |
| Testing | JUnit 5 |
| Version Control | Git |
| Repository Hosting | GitHub |
| Repository | [Intelligent-College-Admission-System](https://github.com/KethineniVenkataAvinash/Intelligent-College-Admission-System) |

---

## Author

**Venkata Avinash**

Project:

[**Intelligent-College-Admission-System**](https://github.com/KethineniVenkataAvinash/Intelligent-College-Admission-System)

---

## License

This project is intended for academic and educational purposes.
