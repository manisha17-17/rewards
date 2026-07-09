# Retail Rewards Program API

A Spring Boot RESTful API that calculates reward points for a retail customer loyalty program based on transaction purchase amounts over a three-month period.

## 📌 Problem Statement & Rules
A retailer awards loyalty points to customers based on individual transactions:
* **2 points** for every dollar spent **over $100** in a single transaction.
* **1 point** for every dollar spent **between $50 and $100** in a single transaction.
* *Example:* A $120 purchase earns $90$ points: `(2 * $20) + (1 * $50) = 40 + 50 = 90 points`.

Given a multi-month transaction history, the API aggregates data to output **monthly points breakdown** and **total points accrued** per customer.

---

## 🏗️ Architecture Overview

The system uses a decoupled, three-tier architecture. It completely isolates data storage, business logic rules, and client communication interfaces. 
Instead of relying on an external SQL database, it leverages an optimized In-Memory Thread-Safe Data Layer to ensure high throughput and low latency.

┌────────────────────────────────────────────────────────────────────────┐
│                        CLIENT / POSTMAN LAYER                          │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ HTTP Requests
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        CONTROLLER / REST LAYER                         │
│  - TransactionController (Data Ingestion & Event Logs)                 │
│  - RewardsController     (Analytical Computations & Reporting)         │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Internal DTOs / Models
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                         SERVICE / BUSINESS LAYER                       │
│  - RewardsServiceImpl   (Points Strategy Engine & Dynamic Aggregation) │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Stream API Operations
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                       DATA ACCESS LAYER (IN-MEMORY)                    │
│     │
│  - ConcurrentHashMap     (Thread-safe primary persistence storage)      │
│   │
└────────────────────────────────────────────────────────────────────────┘




### Key Technical Choices
* **Spring Boot 3.x / Java 17+**: Provides a robust framework with modern Java features.
* **Controller Segregation**: Separates data ingestion endpoints (`/api/transactions`) from analytical computation business endpoints (`/api/rewards`) to adhere to Single Responsibility Principle (SRP).
* **Automated Data Seeding**: Generates a rich, structured 3-month dataset automatically at startup to immediately demonstrate features.

---

## 🚀 Setup & Installation Instructions

### Prerequisites
* **Java SDK 17** or higher installed.
* **Maven 3.6+** installed (or use the included `./mvnw` wrapper).

### Step 1: Clone the Repository
```bash
git clone <your-github-repo-url>
cd retail-rewards-program
```

### Step 2: Build the Application
```bash
./mvnw clean package
```

### Step 3: Run the Application
```bash
./mvnw spring-boot:run
```
The server will boot up locally at `http://localhost:8080`.

---

## 📖 User Guidelines & API Documentation

At application startup, a pre-populated dataset spanning 3 months is automatically loaded into memory to simulate real production history.

### 1. Calculate Customer Rewards
Retrieves the points earned per month and the overall total within a specific date range.

* **Endpoint**: `GET /api/rewards/{customerId}`
* **Query Parameters**:
  * `startDate` (Format: `YYYY-MM-DD`)
  * `endDate` (Format: `YYYY-MM-DD`)

#### Example Request
```http
GET http://localhost:8080/api/rewards/CUST2?startDate=2026-01-01&endDate=2026-03-31
```

#### Example Response
```json
{
    "customerId": "CUST2",
    "monthlyPoints": {
        "FEBRUARY": 110,
        "JANUARY": 45
    },
    "totalPoints": 155,
    "transactions": [
        {
            "id": 4,
            "customerId": "CUST2",
            "amount": 95.0,
            "date": "2026-01-20"
        },
        {
            "id": 5,
            "customerId": "CUST2",
            "amount": 130.0,
            "date": "2026-02-25"
        }
    ]
}
```

### 2. Add New Transaction
Allows manual record entry to dynamically verify calculation rules.

* **Endpoint**: `POST /api/transactions`

#### Example Request Body
```json
{
  "id": 5
  "customerId": "CUST-001",
  "amount": 120.00,
  "transactionDate": "2026-03-15"
}
```

#### Example Response (`200 OK`)
```json
{
  "id": 105,
  "customerId": "CUST-001",
  "amount": 120.00,
  "transactionDate": "2026-03-15"
}
```

### 3. Retrieve All History
* **Endpoint**: `GET /api/transactions`
* **Description**: Returns the full un-aggregated array of all data elements seeded or added to the application.

---

## 🧪 Reward Point Rule Breakdown
To verify math accuracy, the internal business formula executes as follows:
* **Transactions $\le$ $50**: `0 points`
* **Transactions between $51 and $100**: `(Amount - 50) * 1`
* **Transactions > $100**: `((Amount - 100) * 2) + 50`
