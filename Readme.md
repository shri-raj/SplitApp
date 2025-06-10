# SplitApp - Expense Splitting Application

## Overview
SplitApp is a Spring Boot application designed to help users track and split expenses among friends, roommates, or any group. It provides a simple and efficient way to manage shared expenses, calculate balances, and determine optimal settlements.

## Features
- **Expense Management**: Create, update, and delete expenses
- **Multiple Split Types**: Support for equal, percentage-based, and exact amount splits
- **Balance Tracking**: Automatically calculate who owes whom
- **Settlement Suggestions**: Get optimal settlement plans to minimize the number of transactions
- **RESTful API**: Easy integration with frontend applications

## Technology Stack
- **Backend**: Java 17, Spring Boot 3.5.0
- **Database**: PostgreSQL
- **Build Tool**: Maven
- **Dependencies**: Spring Data JPA, Spring Web, Lombok, Spring Validation

## API Endpoints

### Expenses
- `GET /expenses` - Get all expenses
- `GET /expenses/{id}` - Get expense by ID
- `POST /expenses` - Create a new expense
- `PUT /expenses/{id}` - Update an existing expense
- `DELETE /expenses/{id}` - Delete an expense
- `GET /expenses/people` - Get all people involved in expenses

### Person
- `GET /people` - Get all people and their balances
- `GET /settlements` - Get optimal settlement plan

## How to Use

### Creating an Expense with Equal Split
```json
{
  "amount": 1000,
  "description": "Dinner",
  "paidBy": "John",
  "splitType": "EQUAL"
}
```

### Creating an Expense with Exact Split
```json
{
  "amount": 1000,
  "description": "Dinner",
  "paidBy": "John",
  "splitType": "EXACT",
  "splits": [
    {
      "personName": "John",
      "amount": 400
    },
    {
      "personName": "Alice",
      "amount": 300
    },
    {
      "personName": "Bob",
      "amount": 300
    }
  ]
}
```

### Creating an Expense with Percentage Split
```json
{
  "amount": 1000,
  "description": "Dinner",
  "paidBy": "John",
  "splitType": "PERCENTAGE",
  "splits": [
    {
      "personName": "John",
      "amount": 400,
      "percentage": 40
    },
    {
      "personName": "Alice",
      "amount": 300,
      "percentage": 30
    },
    {
      "personName": "Bob",
      "amount": 300,
      "percentage": 30
    }
  ]
}
```

## Setup and Installation

### Prerequisites
- Java 17 or higher
- Maven
- PostgreSQL database

### Configuration
Update the `application.properties` file with your database connection details:

```properties
spring.datasource.url=jdbc:postgresql://your-database-url/your-database-name
spring.datasource.username=your-username
spring.datasource.password=your-password
```

### Building and Running

```bash
# Clone the repository
git clone https://github.com/yourusername/SplitApp.git
cd SplitApp

# Build the application
./mvnw clean package

# Run the application
java -jar target/SplitApp-0.0.1-SNAPSHOT.jar
```

## Deployment

This application can be deployed to Render or any other cloud platform that supports Java applications.

### Deploying to Render

1. Create a `system.properties` file in the project root with:
   ```
   java.runtime.version=17
   ```

2. Create a new Web Service on Render
3. Connect your GitHub repository
4. Set the build command: `./mvnw clean package -DskipTests`
5. Set the start command: `java -jar target/SplitApp-0.0.1-SNAPSHOT.jar`
6. Configure environment variables for database credentials
        
