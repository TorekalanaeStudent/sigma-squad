# Computify (Working Title)

## Group Members

* Canillo
* Mercado
* Saan
* Caneda

---

# Project Overview

## Application Name

**Computify** *(Working Title)*

## Target Audience

* Students
* Library Staff / Librarians

## Goal

The goal of the system is to reduce the manual work of recording library computer usage by digitizing the process. The system will also allow students to check computer availability remotely without needing to physically visit the library.

## Problem Statement

The current process requires students to manually write their:

* Name
* Student ID
* Time In
* Time Out

Additionally, students must physically check the library to determine whether computers are available or occupied.

## Proposed Solution

Computify will provide:

* A digital reservation system for library computers
* Real-time computer availability monitoring
* Automated reservation expiration
* Separate dashboards for students and librarians
* User authentication using legitimate NU email addresses

## Competitive Advantage

The proposed system is unique because similar systems are not commonly implemented in regular institutional library environments.

---

# System Features

## 1. User Registration and Authentication

### Student Registration

The system will allow users to register using their NU email address.

The backend will validate whether the email is a legitimate NU email domain.

### Validation Logic

If the email is valid:

* The user will be added to the database
* The account will be registered as a student account

If the email is invalid:

* Registration will be denied
* The user will receive an error message

### Librarian Accounts

Librarian accounts will not be publicly registered.

Instead:

* Librarian accounts will be manually added to the database by administrators.

---

# Data to be Stored

The system will store the following information:

## User Information

* Student Name
* Student ID
* Email Address
* User Role

## Session Information

* Computer Assigned
* Time In
* Expected Time Out
* Reservation Status

---

# Reservation System

## Features

Students will be able to:

* View available computers
* Reserve a computer
* Cancel reservations
* Monitor reservation timers

## Reservation Expiration

Reservations will automatically expire after **5 minutes** if the student does not arrive.

When a reservation expires:

* The computer becomes available again
* The reservation token is returned to the student

## Reservation Restrictions

To prevent spam or abuse:

* Each student can only have one active reservation at a time
* Students cannot reserve another computer until their current reservation ends

---

# Dashboards

## Student Dashboard

The student dashboard will display:

* Available computers
* Occupied computers
* Reserved computers
* Reservation timer/status

## Librarian Dashboard

The librarian dashboard will display:

* Active users
* Computer availability
* Reservation queue
* Remaining session time

Librarians can also:

* Assign reserved students to computers
* End active sessions manually

---

# User Roles

The system will have two types of users:

## Student

Can:

* Reserve computers
* View availability
* Manage reservations

## Librarian

Can:

* Manage computer sessions
* Assign computers
* Monitor active users
* Access administrative dashboard

---

# Technology Stack

## Frontend

* React JS
* Tailwind CSS

## Backend

* Java Spring Boot

## Database

* PostgreSQL

## Authentication

* JWT (JSON Web Tokens)

---

# Proposed Database Tables

## Users

Stores:

* Name
* Student ID
* Email
* Password
* Role

## Computers

Stores:

* Computer Number
* Computer Status
* Assigned User

## Reservations

Stores:

* Reserved Computer
* Reservation Time
* Expiration Time
* Reservation Status

## Sessions

Stores:

* Time In
* Time Out
* User Session Data

---

# System Workflow

## Student Workflow

1. Student logs in
2. Student checks available computers
3. Student reserves a computer
4. Reservation timer begins
5. Librarian confirms assignment
6. Session starts

## Librarian Workflow

1. Librarian views reservations
2. Librarian assigns computers
3. Librarian monitors active sessions
4. Librarian ends sessions when finished

---

# Future Improvements

Possible future features include:

* Real-time notifications
* Email verification
* QR Code login
* Analytics dashboard
* Mobile application support
* Automated session monitoring

---

# Conclusion

Computify aims to modernize the library computer reservation process by replacing manual logging with a digital management system. The project improves convenience for both students and librarians by providing a centralized platform for reservations, monitoring, and computer availability management.
