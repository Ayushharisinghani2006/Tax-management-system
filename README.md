# Tax Management System (Java - System Software)

## Overview

This project is a **console-based system software** written in Java that simulates a **role-based tax management system**.

It focuses on:

* Core Java concepts
* System design logic
* Data handling using collections
* Basic security (password hashing)

---

## Objectives

* Implement role-based access control (RBAC)
* Simulate tax calculation workflow
* Practice file handling and logging
* Demonstrate modular class design

---

## System Roles

### 1. Admin

* View all registered users
* Delete users

### 2. Taxpayer

* Add income records
* Add expense records
* View tax summary
* Generate tax report (file export supported)
* Set and view reminders

### 3. Auditor

* View taxpayer financial data
* Flag discrepancies
* View audit logs

---

## Core Functionalities

### Authentication

* User registration and login
* Password hashing using SHA-256

### Tax Management

* Income and expense tracking
* Taxable income calculation
* Progressive tax calculation using predefined brackets

### Reminder System

* Users can set reminders with date-time
* System checks and displays due/upcoming reminders

### Audit Logging

* Tracks user actions such as:

  * Registration
  * Login
  * Admin/Auditor operations

### File Handling

* Tax reports can be saved as `.txt` files

---

## Technologies Used

* Java (Core)
* Java Collections Framework
* File I/O (BufferedWriter, FileWriter)
* Date-Time API (LocalDateTime)
* Security (MessageDigest - SHA-256)

---

## Project Structure

```
TaxManagementSystem.java
│
├── User
├── Notification
├── AuditLog
├── TaxManagementSystem (Main class)
├── TaxpayerData
├── TaxCalculator
└── SecurityUtils
```

---

## How to Run

### Compile

```
javac TaxManagementSystem.java
```

### Execute

```
java TaxManagementSystem
```

---

## Design Highlights

* Role-based control using conditional logic
* Separation of concerns using multiple classes
* Use of HashMap for user and data storage
* Modular methods for scalability

---

## Limitations

* No database (in-memory storage only)
* No GUI (CLI-based)
* No persistent sessions
* Minimal input validation

---

## Possible Extensions

* Database integration (MySQL/MongoDB)
* REST API using Spring Boot
* Frontend interface (React)
* JWT-based authentication
* Advanced tax rules

---

## Author

Ayush
