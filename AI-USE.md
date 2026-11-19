# AI Use Log

## Session log 10/29

### 1. Code review of the user's files (several rounds)
- **After pulling `main`:** Compared the merge against the earlier commit. Found `OrderRepository` took a `Connection` while `App` passed a `DataSource`. Noted `Database`, `pom.xml` (Jackson and Hikari added), a new `Order` record, and the new CRUD controller, service and repository. Listed follow-ups: client-supplied `orderTotal`, no exception mapper, and the untouched old models. Dropped the CORS suggestion when told there is no frontend yet.
- **`OrderRepository`:** Found it never closed pooled connections, which would exhaust the pool after about 10 requests. Proposed try-with-resources, then a smaller change that keeps the existing structure (wrap each method body in `try (Connection conn = pool.getConnection())`). Verified the user's applied version.
- **Controller and `App` refactor:** Proposed moving route registration into `OrderController.registerRoutes`. Reviewed the user's version and found `delete("/orders{orderID}")` missing a slash and an unused import.
- **`pool == null` check:** Explained it can never be true because `Database` creates the pool in a static initializer, so a failure surfaces as `ExceptionInInitializerError`. Offered two fixes. The user applied the catch-based one. A second review found the message would print `null` (fixed by using `getCause()`) and the dead null check, which the user removed. Final `App` was verified.

### 2. Documentation
- **Prompt:** Add compile instructions and example Postman requests to the README, and record the assistant's output in `AI-USE.md`.
- **Output:** Appended "Back-End: Compile and Run" and "Example Postman Requests" sections to `README.md`, and wrote this file.

## Session log 11/12 – 11/19

### 1. Order flow redesign
- **Design:** Kept `Order` as the database row and response type. Added request types (`PlaceOrderRequest`, `OrderLine`) and the `PaymentType` and `OrderChannel` enums. Reduced `ComboType` to validation rules only.
- **`placeOrder`:** One transaction that inserts the order, details, combo details, total and ingredient stock.
- **Validation (service):** Payment method, non-empty lines, combo exists, item counts per combo, items active, a la carte rule, and customer, cashier and channel rules. Invalid input returns 400 through `IllegalArgumentException`.
- **Customer, cashier and channel:** Added `customer_id`, `cashier_id` and `order_channel` to `orders`.

### 2. Firebase authentication
- **Implemented (as code the user added):** `FirebaseSetup` (emulator or real credentials), `FirebaseAuthHelper` (token verification), `AccessControl` (a `beforeMatched` handler: public, token-only, profile lookup, role check, deny by default), `Access` and `UserRole` as route roles, role lists on every route, ownership rules (user routes and order reads), and `placeOrder` deriving customer, cashier and channel from the signed-in user.
- **Registration and sign-in flow:** The client signs up with Firebase, then `POST /users` creates the profile with `firebase_id` and `email` taken from the verified token. Sign-in is Firebase only, followed by `GET /users/me`.
