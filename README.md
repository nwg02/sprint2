# GISU Sprint 2 Java Prototype

This is a minimal Java + Spring Boot + MySQL web prototype for the Sprint 2 assignment. It includes registration, login/logout, session-based login state, a basic insurance assessment saved to MySQL, and insurance plans loaded from MySQL.

## Requirements
- Java 17 or newer
- Maven
- MySQL 8 or newer

These tools and Maven dependencies require an internet connection for the initial setup. Wait for Wi-Fi if you are conserving hotspot data.

## 1. Create the database
In MySQL, run `database/schema.sql`. This creates the `gisu` database, its four tables, and sample insurance plans.

## 2. Configure database access
Open `src/main/resources/application.properties` and update the MySQL username/password to match your local MySQL installation.

Default local settings:
- database: `gisu`
- username: `root`
- password: empty

## 3. Run the application
Open a terminal in this project folder and run:

```bash
mvn spring-boot:run
```

Then visit `http://localhost:8080`.

## Included pages
- Home
- Register
- Login / Logout
- Dashboard
- Insurance assessment (saved to database)
- Insurance plans (read from database)

## Notes
- This is a classroom prototype, not a production insurance or payment system.
- The assessment recommendations use a simple budget/coverage filter, not AI.
- A `payments` table is included for database-design coverage; no real payment processing is implemented.
- Passwords are hashed before being stored. Use only fictional/test data.
