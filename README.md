# Team_8-Project_3
Team 8's website project for Panda Express targeted towards the public. Using our previous service, we will be making a website for costumers to purchase items on their own.

### Roles

#### Project Manager
- Vish

#### Back-end
- Tyler
- Arnav

#### Front-end
- Will
- Brandon

### Novel Feature

A rewards system - Rewards points to costumers for items. The more they buy, the more points they get to later get discounts on items.

### Our Techstack

#### Front-end
- UI Software: React + Typescript
- UI Design: Figma, Claude Design

#### Back-end
- Database: PostgreSQL
- Programming: Java + Javalin(HTTP)
- Authentication: Firebase

### Back End Structure:
    - Top level: App
        - Registers routes, connects database, and starts server
    - Controllers
        - Handles HTTP logic and unpacks request payload for use in services and returns status codes
    - Services
        - Main business logic that deals only with java and our own custom types and calls repository functions
    - Repository
        - Direct database interaction that handles SQL queries
    - Models
        - Our custom data types that structure what data is collected for use in the database or for any other logic
