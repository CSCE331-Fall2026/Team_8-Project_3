# Team 8 — Panda Express POS Web App

Team 8's website project for Panda Express targeted towards the public. Using our previous service, we will be making a website for costumers to purchase items on their own.

## Live Links
- **Deployed app:** https://team-8-project-3-1.onrender.com/customer
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
- **Database:** PostgreSQL on GCP CloudSQL
- **Hosting service:** Render (backend Web Service + frontend Static Site)
- **Authentication:** Firebase


# User Guide

## Frontend: Compile and run

``cd src/frontend``

``npm install``

``npm run dev``

The frontend calls the backend at `http://localhost:8080` unless `VITE_API_URL` is set when building.


## Back-End: Compile and Run
The back end lives in `src/backend` (Java 25, Maven, Javalin on port 8080).

### Setup
1. Install JDK 25.
2. Copy `src/backend/db.properties.example` to `src/backend/db.properties` and fill in the credentials. The URL must be a JDBC URL (`jdbc:postgresql://<host>:5432/<db>`). `db.properties` is gitignored, so do not commit it.
3. Create `src/backend/.env` (also gitignored) with the local Firebase settings:
   FIREBASE_AUTH_EMULATOR_HOST=127.0.0.1:9099
   FIREBASE_PROJECT_ID=team8project3-2f2ac

Instead of `db.properties`, you can set the `DB_URL`, `DB_USER` and `DB_PASSWORD` environment variables, which take priority. The backend also reads `PORT` (default `8080`) and `CORS_ORIGINS`, a comma-separated list of frontend origins allowed in addition to `http://localhost:5173`.

### Running the Firebase emulator
**Terminal 1: Firebase Auth emulator.** Leave it running.
```bash
firebase emulators:start --project team8project3-2f2ac
```
(Install the CLI with npm install -g firebase-tools)

**The emulator keeps accounts in memory only. Restarting it deletes every test account.
This also causes it to desync from the database. Use the school database instead of CloudSQL database for testing.

### Run from the terminal
**Terminal 2: Backend App
Requires JDK 25 and Maven. Then from `src/backend`:
```bash
mvn compile
mvn exec:java "-Dexec.mainClass=App"
```
Install Maven with:
- **macOS:** `brew install maven`
- **Windows:** `winget install Apache.Maven` (or `choco install maven` / `scoop install maven`), then open a new terminal so `mvn` is on your `PATH`
- **Any OS:** download from https://maven.apache.org/download.cgi and add its `bin` folder to `PATH`

On Windows, check your setup with `java -version` and `mvn -version`. If `mvn` is not recognized, `JAVA_HOME` must point to the JDK 25 folder and Maven's `bin` folder must be on `PATH`.

A successful start prints Javalin's startup output. If the database connection fails, the app prints `Error connecting to database: ...` and exits.

## Deploying to Render
**Backend:** New > Web Service, Language **Docker**, Root Directory `src/backend` (it contains the `Dockerfile`). In Environment, set `DB_URL` (add `?sslmode=require` if the database requires SSL), `DB_USER`, `DB_PASSWORD`, and `CORS_ORIGINS` set to the frontend's `https://….onrender.com` URL. Render supplies `PORT`.

**Frontend:** New > Static Site, Root Directory `src/frontend`, Build Command `npm install && npm run build`, Publish Directory `dist`. Set the environment variable `VITE_API_URL` to the backend's `https://….onrender.com` URL. Under Redirects/Rewrites, add a rewrite from `/*` to `/index.html` so routes like `/cashier` work on refresh.

## Testing the Back End with Postman

Base URL: http://localhost:8080. For requests with a body, choose Body > raw > JSON.

1. Get a token (emulator)

POST http://127.0.0.1:9099/identitytoolkit.googleapis.com/v1/accounts:signUp?key=fake
```json
{ 
    "email": "testuser@example.com", 
    "password": "password123", 
    "returnSecureToken": true 
}
```

Copy idToken from the response. To sign in again later, use accounts:signInWithPassword with the same body. Tokens expire after one hour.

For every backend request below, open the Authorization tab, choose Bearer Token and paste the token.

2. Create the profile

POST /users (token required):
```json
{
  "firstName": "Test", 
  "lastName": "Customer", 
  "phoneNumber": "9795551234", 
  "userRole": "customer"
}
```

The email and Firebase id come from the token, not the body. Expected: 201. Repeating it returns 409. GET /users/me returns the signed-in user's profile, or 404 if no profile exists yet.

For testing, create one account per role (customer, cashier, manager, kitchen). Each needs its own email and its own token. The role in the body is currently trusted, to make testing easier.

3. Place an order

POST /orders (token required, role: cashier, manager or customer):
```json
{
  "paymentMethod": "credit_card",
  "lines": [
    { "comboId": 1, "quantity": 1, "menuItemIds": [13, 1] }
  ]
}
```

This orders a Bowl with Chow Mein and Orange Chicken (IDs from the seed data). Who is ordering comes from the token, not the body: a customer token creates an `online` order for that customer, and a cashier or manager token creates an `in_store` order with that user as the cashier. Any `cashierId` or `channel` in the body is ignored. A cashier or manager may also add `"customerId": 3` to attach the customer being served, or leave it out for a guest order.

Bad order (a Plate with only one entree), expected 400 `Plate requires 2 entree(s)`:
```json
{
  "paymentMethod": "cash",
  "lines": [
    { "comboId": 2, "menuItemIds": [13, 1] }
  ]
}
```

4. Get the order

GET /orders/{orderId}, using the `orderId` from the response above:
- With the same token (or any cashier, manager or kitchen token): 200 with the order.
- With a different customer's token: 404, so customers cannot see other people's orders.
- With no token: 401.

GET /orders?n=5 returns the five most recent orders. Expected: 200 with a cashier, manager or kitchen token, and 403 with a customer token.

## Current Route RBAC Scheme:
| Area | Allowed |
|------|---------|
| `GET /menu` | everyone, no token |
| Menu `POST`, `PUT`, `DELETE` | manager |
| Ingredients, read | manager, cashier, kitchen |
| Ingredients, write | manager |
| `POST /orders` | manager, cashier, customer |
| `GET /orders?n=10` | manager, cashier, kitchen |
| `GET /orders/{id}` | any role (a customer only their own) |
| Order `PUT`, `DELETE` | manager |
| `GET`, `PUT`, `DELETE /users/{id}` | any role on yourself, a manager on anyone |