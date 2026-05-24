Idempotency Gateway (The "Pay-Once" Protocol)

Business Context Problem: Double charging happens when payment requests are retried.
Solution: An API layer checks the Idempotency-Key to ensure each request is processed only once.

Architecture Diagram Flow: Client → API Gateway → Idempotency Layer → Payment Processor → Cache/DB → Client.
Paths:

First Request → process + store response.

Duplicate Request → return cached response.

Different Request → error (409/422).

In-flight Request → block & return same result.

Architecture Diagram
Idempotency Gateway Architecture

Setup Instructions Fork Repository into your GitHub account.

Install Java JDK (version 21 or 25).

Install Maven.

Run locally:

bash mvn spring-boot:run Verify API: POST /process-payment.

API Documentation Endpoint: POST /process-payment
Headers:

Idempotency-Key:

Request Body:

json { "amount": 100, "currency": "RWF" } Responses:

First request → 201 Created, "Charged 100 RWF".

Duplicate → cached response + X-Cache-Hit:true.

Different body → 409 Conflict / 422 Unprocessable Entity.

In-flight → waits, returns same result.

User Stories & Acceptance Criteria: 

User Story 1: Happy path.

User Story 2: Duplicate attempt.

User Story 3: Same key, different body.

Bonus User Story: In-flight duplicate.

Design Decisions Storage Choice: 

Redis/SQLite/Map for caching responses.

Hashing: Compare payloads for fraud check.

Locking: Prevent race conditions.

Developer’s Choice Feature Audit Logging: Log duplicate attempts (timestamp, key, client IP).
Purpose: Detect retry patterns, prevent abuse, improve monitoring.

Pre-Submission Checklist:
-Repo is Public.

-Clean unnecessary files.

-Server runs without crash.

-README replaced with documentation.

-Diagram included.

-Multiple commits with clear messages.

**API Documentation**

 POST /process-payment
- **Description**: Processes a payment request and ensures idempotency using the `Idempotency-Key` header.

- **Headers**:
  - `Idempotency-Key: <unique-string>` (required)

- **Request Body**:
  {
    "amount": 1000,
    "currency": "RWF",
    "method": "MOMO"
  }
Response (First Request):

{
  "status": "success",
  "transactionId": "abc123",
  "message": "Charged 1000 RWF"
}
Response (Duplicate Request):


{
  "status": "success",
  "transactionId": "abc123",
  "message": "Charged 1000 RWF"
}
Header: X-Cache-Hit: true

Error Case (Fraud/Error Check):

{
  "error": "Idempotency-Key reuse detected with different payload."
}
Status: 400 Bad Request. 

## Design Decisions

- **Framework Choice (Spring Boot)**: I chose Spring Boot because it provides fast setup, dependency injection, and built-in REST API support. This makes building an idempotency gateway quick and reliable.

- **Idempotency Implementation (Redis)**: For the prototype, I used a HashMap to store transaction states in memory. In a production environment, Redis or a database would be used to ensure scalability and persistence.

- **Error Handling**: I implemented `@ControllerAdvice` and `ResponseEntity` to standardize error responses. This ensures that developers receive clear and consistent messages.

- **Security Considerations**: The system detects fraud by rejecting requests that reuse the same Idempotency-Key with different payloads. This prevents replay attacks and ensures transaction integrity.

- **Developer Experience**: The README includes API documentation, setup instructions, and architecture diagrams so that other developers can quickly understand and use the project.
