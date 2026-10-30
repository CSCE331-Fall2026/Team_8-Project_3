# Team 8 — Panda Express POS Web App

Team 8's website project for Panda Express targeted towards the public. Using our previous service, we will be making a website for costumers to purchase items on their own.

## Live Links
- **Deployed app:** TBD
- **Documentation:** TBD
- **Project board (backlog + burn-up):** [GitHub Projects URL](https://github.com/orgs/CSCE331-Fall2026/projects/14)

## Roles
| Name | Role |
|------|------|
| Vish | Project Manager & Full-stack |
| Brandon | Front-end Lead |
| Will | Front-end Lead |
| Tyler | Back-end Lead |
| Arnav | Back-end Lead |

## Novel Feature
**What it is:** Our novel feature will be a rewards system for the user that allows them to get rewards based on their purchases. It will be somewhere along the lines of every $10 spent, the system will give them $1 back.

**Motivation:** It serves both the user and the manager because it allows users who buy from our store more often to get what they need

## Tech Stack
- **Front-end:** Software used for implementation:  React + Typescript, software used for design: Figma, Claude Design
- **Back-end:** Java/JDBC, Javalin
- **Database:** PostgreSQL on AWS
- **Hosting service:** people.tamu.edu
- **Authentication:** Firebase


## Back-End: Compile and Run
The back end lives in `src/backend` (Java 25, Maven, Javalin on port 8080).

### Setup
1. Install JDK 25.
2. Copy `src/backend/database/dbSetup.java.example` to `src/backend/database/dbSetup.java` and fill in the credentials. The URL must be a JDBC URL (`jdbc:postgresql://<host>:5432/<db>`), and each line must end with a semicolon. `dbSetup.java` is gitignored, so do not commit it.

### Run from the terminal
Requires JDK 25 and Maven. From `src/backend`:
```bash
mvn compile
mvn exec:java -Dexec.mainClass=App
```
Install Maven with:
- **macOS:** `brew install maven`
- **Windows:** `winget install Apache.Maven` (or `choco install maven` / `scoop install maven`), then open a new terminal so `mvn` is on your `PATH`
- **Any OS:** download from https://maven.apache.org/download.cgi and add its `bin` folder to `PATH`

On Windows, check your setup with `java -version` and `mvn -version`. If `mvn` is not recognized, `JAVA_HOME` must point to the JDK 25 folder and Maven's `bin` folder must be on `PATH`.

A successful start prints Javalin's startup output. If the database connection fails, the app prints `Error connecting to database: ...` and exits.

## Example Postman Requests
Base URL: `http://localhost:8080`. For requests with a body, choose **Body > raw > JSON**.

Valid enum values:
- `orderStatus`: `paid`, `canceled`, `refunded`
- `paymentMethod`: `credit_card`, `debit_card`, `cash`, `gift_card`, `meal_pass`

| Action | Method | URL | Body |
|--------|--------|-----|------|
| Create an order | POST | `/orders` | see below |
| Get one order | GET | `/orders/1` | none |
| Get recent orders | GET | `/orders?n=10` | none |
| Update an order | PUT | `/orders/1` | only the fields to change |
| Delete an order | DELETE | `/orders/1` | none |

**Create an order** (`POST /orders`):
```json
{
  "orderStatus": "paid",
  "paymentMethod": "credit_card",
  "orderTotal": 12.5
}
```
Expected response: `201 Created` with the new order, including the database-generated `orderId` and `createdAt`. If `orderStatus` is omitted it defaults to `paid`, and if `orderTotal` is omitted it defaults to `0`.

**Update an order** (`PUT /orders/1`):
```json
{
  "orderStatus": "refunded"
}
```
Fields left out keep their current value. Expected response: `200` with `Order updated`, or `404` if the order does not exist.

**Delete an order** (`DELETE /orders/1`): returns `204` on success, or `404` if the order does not exist. Deleting an order also deletes its `order_details` and `combo_details` rows.
