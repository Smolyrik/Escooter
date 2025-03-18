# E-Scooter Rental System

## Description
The **E-Scooter Rental System** is a Java Spring Boot application designed to manage the rental of electric scooters. It includes user registration, rental management, pricing models, and administrative tools. The system follows best practices in software development, including MVC architecture, strong cohesion, loose coupling, and modular design.

## Features

### User Management
- User registration (Managers and Regular Users)
- Editing personal information
- Role-based access control (Manager, User)

### Rental System
- Hierarchical rental locations with geographic mapping
- Add, edit, and remove scooters and rental locations
- View detailed rental point information (availability, models, condition)
- Rental pricing (hourly, subscription-based, discounts)
- User scooter rental by hourly rate or subscription
- Rental history for both users and administrators (who, when, distance traveled)

### Technical Specifications
- **Java 21**, **Spring Boot**, **Spring Security**
- **PostgreSQL** as the database (normalized to 3NF)
- **Liquibase** for database migrations
- **JWT-based authentication**
- **Docker & Docker Compose** for containerized deployment
- **MapStruct** for DTO-to-Entity mapping
- **JUnit 5 & Mockito** for unit testing
- **Testcontainers** for integration testing
- **Swagger/OpenAPI** for API documentation

## Installation & Setup

### Prerequisites
- Java 21
- Docker & Docker Compose
- PostgreSQL (if running locally)
- Maven


### 
### Run with Docker Compose
```sh
docker-compose up --build
