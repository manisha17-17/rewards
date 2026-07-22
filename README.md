# Retail Rewards Program API

A Spring Boot REST API that calculates customer reward points based on purchase transactions over a specified date range.

The application uses **Spring Boot**, **Spring Data JPA**, and an **H2 in-memory database** to manage transactions and calculate monthly and total reward points.

---

# Features

- Calculate reward points for a customer within a date range
- Calculate reward points for all customers
- Add new transactions
- Retrieve customer transactions
- Retrieve all transactions
- Uses H2 in-memory database
- Uses BigDecimal for monetary calculations
- Bean Validation for request validation
- Global exception handling with structured JSON responses
- Unit and Controller test cases using JUnit, Mockito, and MockMvc

---

# Reward Rules

The retailer awards reward points according to the following rules:

- No points for purchases of **$50 or less**
- 1 point for every whole dollar spent over **$50**
- 2 points for every whole dollar spent over **$100**

Example:

Transaction Amount = **$120**

```
First $50       -> 0 points
$51-$100        -> 50 points
$101-$120       -> 20 × 2 = 40 points

Total = 90 points
```

---

# Technology Stack

- Java 17
- Spring Boot
- Spring MVC
- Spring Data JPA
- H2 Database
- Maven
- Lombok
- JUnit 5
- Mockito
- MockMvc

---

# Project Structure

```
src
 ├── main
 │   ├── java
 │   │     └── com.retail.rewards
 │   │           ├── controller
 │   │           ├── dao
 │   │           ├── model
 │   │           ├── service
 │   │           ├── util
 │   │           ├── exception
 │   │           └── RewardsApiApplication
 │   │
 │   └── resources
 │         ├── application.properties
 │         ├── schema.sql
 │         └── data.sql
 │
 └── test
       ├── controller
       ├── service
       ├── util
       └── exception
```

---

# Architecture

```
                Client
                   │
             REST Controllers
                   │
            Service Layer
                   │
        Reward Calculation Logic
                   │
      Spring Data JPA Repository
                   │
          H2 In-Memory Database
```

---

# Setup Instructions

## Prerequisites

- Java 17
- Maven 3.8+
- Git

---

## Clone the Project

```bash
git clone https://github.com/manisha17-17/rewards.git

cd rewards-api
```

---

## Build

```bash
mvn clean install
```

---

## Run

```bash
mvn spring-boot:run
```

The application starts on

```
http://localhost:8080
```

---

# H2 Database

The application uses an **H2 In-Memory Database**.

Open the H2 Console:

```
http://localhost:8080/h2-console
```

Connection Details

```
JDBC URL

jdbc:h2:mem:rewardsdb

Username

sa

Password

(empty)
```

---

# Sample Data

The application automatically loads sample transactions during startup using **data.sql**.

No manual data insertion is required before testing the APIs.

---

# API Endpoints

## Rewards APIs

| Method | Endpoint | Description |
|----------|----------------------------|-------------------------------|
| GET | /api/rewards/{customerId} | Rewards for one customer |
| GET | /api/rewards | Rewards for all customers |

---

## Transaction APIs

| Method | Endpoint | Description |
|----------|--------------------------|-------------------------|
| POST | /api/transactions | Add transaction |
| GET | /api/transactions | Get all transactions |
| GET | /api/transactions/{customerId} | Customer transactions |

---


# API Documentation

---

## 1. Calculate Rewards for a Customer

Returns monthly reward points and total reward points for a customer within the specified date range.

### Endpoint

```
GET /api/rewards/{customerId}
```

### Query Parameters

| Parameter | Type | Required | Format |
|----------|------|----------|--------|
| startDate | LocalDate | Yes | yyyy-MM-dd |
| endDate | LocalDate | Yes | yyyy-MM-dd |

---

### Example Request

```
GET http://localhost:8080/api/rewards/CUST1?startDate=2026-01-01&endDate=2026-03-31
```

---

### Example Response

```json
{
  "customerId": "CUST1",
  "monthlyPoints": {
    "Jan-2026": 90,
    "Feb-2026": 25,
    "Mar-2026": 250
  },
  "totalPoints": 365,
  "transactions": [
    {
      "id": 1,
      "customerId": "CUST1",
      "amount": 120.00,
      "date": "2026-01-15"
    },
    {
      "id": 2,
      "customerId": "CUST1",
      "amount": 75.00,
      "date": "2026-02-10"
    },
    {
      "id": 3,
      "customerId": "CUST1",
      "amount": 200.00,
      "date": "2026-03-05"
    }
  ]
}
```

---

## 2. Calculate Rewards for All Customers

Returns reward summaries for every customer within the specified date range.

### Endpoint

```
GET /api/rewards
```

---

### Example Request

```
GET http://localhost:8080/api/rewards?startDate=2026-01-01&endDate=2026-03-31
```

---

### Example Response

```json
[
  {
    "customerId": "CUST1",
    "monthlyPoints": {
      "Jan-2026": 90,
      "Feb-2026": 25,
      "Mar-2026": 250
    },
    "totalPoints": 365
  },
  {
    "customerId": "CUST2",
    "monthlyPoints": {
      "Jan-2026": 45,
      "Feb-2026": 110
    },
    "totalPoints": 155
  }
]
```

---

## 3. Add Transaction

Adds a new customer transaction.

### Endpoint

```
POST /api/transactions
```

---

### Request Body

```json
{
  "customerId": "CUST3",
  "amount": 125.50,
  "date": "2026-03-20"
}
```

---

### Success Response

**Status**

```
201 Created
```

**Location Header**

```
Location:
/api/transactions/6
```

---

### Response Body

```json
{
  "id": 6,
  "customerId": "CUST3",
  "amount": 125.50,
  "date": "2026-03-20"
}
```

---

## 4. Get Transactions for a Customer

Returns all transactions belonging to a customer.

### Endpoint

```
GET /api/transactions/{customerId}
```

---

### Example

```
GET http://localhost:8080/api/transactions/CUST1
```

---

### Response

```json
[
  {
    "id": 1,
    "customerId": "CUST1",
    "amount": 120.00,
    "date": "2026-01-15"
  },
  {
    "id": 2,
    "customerId": "CUST1",
    "amount": 75.00,
    "date": "2026-02-10"
  }
]
```

---

## 5. Get All Transactions

Returns every transaction stored in the H2 database.

### Endpoint

```
GET /api/transactions
```

---

### Example Request

```
GET http://localhost:8080/api/transactions
```

---

### Example Response

```json
[
  {
    "id": 1,
    "customerId": "CUST1",
    "amount": 120.00,
    "date": "2026-01-15"
  },
  {
    "id": 2,
    "customerId": "CUST1",
    "amount": 75.00,
    "date": "2026-02-10"
  },
  {
    "id": 3,
    "customerId": "CUST2",
    "amount": 130.00,
    "date": "2026-02-25"
  }
]
```

---

## Notes

- Reward points are calculated for every transaction individually.
- Monthly rewards are grouped using **Year-Month** (for example, `Jan-2026`) to avoid combining transactions from different years.
- Monetary values use **BigDecimal** to preserve decimal precision.
- Transactions are persisted in an **H2 in-memory database** using Spring Data JPA.

---


# Error Handling

The application uses a global exception handler (`@RestControllerAdvice`) to return structured JSON error responses.

---

## Invalid Date Format

### Request

```
GET /api/rewards/CUST1?startDate=2026/01/01&endDate=2026-03-31
```

### Response

**400 Bad Request**

```json
{
  "timestamp": "2026-07-20T20:15:10.123",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid date format supplied. Please use YYYY-MM-DD.",
  "details": {
    "parsedValue": "2026/01/01",
    "expectedFormat": "ISO-8601 (YYYY-MM-DD)"
  }
}
```

---

## Start Date Greater Than End Date

### Request

```
GET /api/rewards/CUST1?startDate=2026-04-01&endDate=2026-03-01
```

### Response

**400 Bad Request**

```json
{
  "timestamp": "2026-07-20T20:20:10.111",
  "status": 400,
  "error": "Bad Request",
  "message": "Start date must not be after end date."
}
```

---

## Missing Required Parameter

### Request

```
GET /api/rewards/CUST1?startDate=2026-01-01
```

### Response

**400 Bad Request**

```json
{
  "timestamp": "2026-07-20T20:21:15.001",
  "status": 400,
  "error": "Bad Request",
  "message": "Required query parameter is missing."
}
```

---

## Validation Failure

### Request

```json
POST /api/transactions

{
    "customerId":"",
    "amount":0,
    "date":null
}
```

### Response

**400 Bad Request**

```json
{
  "status":400,
  "error":"Bad Request",
  "message":"Validation failed"
}
```

---

# Reward Calculation Examples

| Transaction Amount | Reward Points |
|-------------------:|--------------:|
| $40 | 0 |
| $50 | 0 |
| $75 | 25 |
| $100 | 50 |
| $120 | 90 |
| $150 | 150 |
| $200 | 250 |

---

# Testing

The project contains unit and controller tests using:

- JUnit 5
- Mockito
- Spring Boot Test
- MockMvc

### Covered Scenarios

- Reward calculation
- Transaction CRUD operations
- Controller endpoints
- Request validation
- Invalid date format
- Invalid date range
- Missing request parameters
- Exception handling

---

# Assumptions

- Reward points are calculated independently for each transaction.
- Amounts are stored using **BigDecimal** to avoid floating-point precision issues.
- Reward calculations round down to the nearest whole dollar before calculating points.
- Monthly rewards are grouped by **Year-Month** (for example, `Jan-2026`) to ensure transactions from different years are not merged.
- Transaction IDs are generated automatically by the database.
- Sample data is loaded automatically into the H2 database at application startup using SQL scripts.

---

# Technologies Used

- Java 17
- Spring Boot
- Spring MVC
- Spring Data JPA
- Hibernate
- H2 Database
- Maven
- Lombok
- Bean Validation
- JUnit 5
- Mockito
- MockMvc

---

# Future Improvements

- Add pagination for transaction APIs.
- Integrate Swagger/OpenAPI documentation.
- Add authentication and authorization.
- Replace H2 with MySQL or PostgreSQL for production deployments.
- Add caching for frequently requested reward summaries.
- Containerize the application using Docker.
- Add CI/CD pipeline using GitHub Actions or Jenkins.

---

# Author

**Manisha Dangi**

Retail Rewards Program API

Built using Spring Boot, Spring Data JPA, H2 Database, and Java 17.
