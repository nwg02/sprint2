# GISU Sprint 2 Prototype

This project is a Java Spring Boot application that uses MySQL to store user accounts and insurance assessment results. Users can register, log in, complete an insurance assessment, and view available insurance plans.

## Requirements

* Java 17 or newer
* Maven
* MySQL 8 or newer

An internet connection is needed the first time Maven downloads the project's dependencies.

## 1. Set Up the Database

Run `database/schema.sql` in MySQL. This creates the `gisu` database, the required tables, and sample insurance plans.

## 2. Configure MySQL

Open `src/main/resources/application.properties` and set the database username and password for your local MySQL installation.

The default database name is `gisu`. Make sure the credentials match your MySQL setup.

## 3. Run the Application

Open a terminal in the project folder and run:

```bash
mvn spring-boot:run
```

Once the application starts, open http://localhost:8080 in your browser.

## Features

* User registration and login/logout
* Session-based login state
* Dashboard
* Insurance assessment results saved to MySQL
* Insurance plans loaded from MySQL

## Notes

* This is a prototype for a class project.
* Insurance recommendations use a basic budget and coverage filter, not AI.
* The database includes a `payments` table, but payment processing is not implemented.
* Passwords are hashed before being stored.
* Use fictional or test information when trying the application.
