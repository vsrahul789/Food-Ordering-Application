# 🍔 Online Food Ordering System

A full-stack backend API for an Online Food Ordering System built using **Spring Boot**, **Spring Security**, **JWT Authentication**, **MySQL**, and **RESTful APIs**.

## 🚀 Features

- **User Registration & Login** (JWT based)
- **Role-Based Access Control**:
  - `ADMIN`: Full CRUD on restaurants
  - `RESTAURANT_OWNER`: Can manage own restaurant's menu items
  - `CUSTOMER`: Can browse restaurants, menu items, add to cart, place orders, and leave feedback
- **Restaurant Management**
- **Menu Item Management**
- **Cart Functionality**
- **Order Placement & Tracking**
- **Feedback System**
- **Logging to File**

## 🏛️ Project Structure
```
OnlineFoodOrderingSystem/
├── src/main/java/com/foodorderingsystem/FoodOrderingApplication/
│   ├── config/                 # Security Configs
│   ├── controller/             # REST Controllers
│   ├── dto/                    # DTOs for data transfer
│   ├── entities/               # JPA Entities
│   ├── repository/             # Spring Data JPA Repositories
│   ├── security/               # JwtProvider and Filters
│   ├── service/                # Interfaces
│   ├── service/impl/           # Service Implementations
│   └── exception/              # Custom Exceptions
├── src/main/resources/
│   ├── application.properties  # Configuration
├── pom.xml                     # Maven Dependencies
```

## 🚩 Endpoints Overview

### ✨ Auth Routes (`/api/auth`)
| Method | Endpoint        | Description                     |
|--------|------------------|---------------------------------|
| POST   | `/register`      | Register new user               |
| POST   | `/login`         | Login and receive JWT           |

### 🏨 Restaurant Routes (`/api/restaurant`)
| Method | Endpoint               | Access   | Description                 |
|--------|-------------------------|----------|-----------------------------|
| POST   | `/`                     | ADMIN    | Add a new restaurant        |
| GET    | `/`                     | ALL      | Get all restaurants         |
| DELETE | `/{restaurantId}`       | ADMIN    | Delete a restaurant         |

### 🍽️ Menu Routes (`/api/restaurant`)
| Method | Endpoint         | Access            | Description                 |
|--------|------------------|-------------------|-----------------------------|
| POST   | `/`              | RESTAURANT_OWNER  | Add menu item to own restaurant |
| GET    | `/`              | ALL               | View all menu items         |
| DELETE | `/{id}`          | RESTAURANT_OWNER  | Delete own menu item        |

### 🛒 Cart Routes (`/api/cart`)
| Method | Endpoint          | Description                 |
|--------|-------------------|-----------------------------|
| POST   | `/`   | Add item to cart            |
| GET    | `/`           | View cart contents          |
| DELETE | `/remove/{itemId}`| Remove item from cart       |
| DELETE | `/`          | Clear entire cart           |

### 📦 Order Routes (`/api/orders`)
| Method | Endpoint       | Description                 |
|--------|----------------|-----------------------------|
| POST   | `/place`       | Place a new order           |
| GET    | `/`   | View customer order |
| PUT    | `/{id}` | Cancel order                |
| PUT    | `/{id}/status` | Update order status (Admin) |

### 🖊️ Feedback Routes (`/api/feedback`)
| Method | Endpoint       | Description                 |
|--------|----------------|-----------------------------|
| POST   | `/`      | Submit feedback             |
| GET    | `/` | View feedback by users       |

## ⚖️ Roles & Access
- `ADMIN`: Manage users, restaurants, orders
- `RESTAURANT_OWNER`: Manage own restaurant & menu
- `CUSTOMER`: Browse, order, and submit feedback

## 🔒 Authentication
- Uses JWT for stateless authentication
- Add `Authorization: Bearer <token>` header to secure endpoints

## ⚙️ Configuration (`application.properties`)
```properties
spring.application.name=FoodOrderingApplication

spring.datasource.url=jdbc:mysql://localhost:3306/food_ordering_system
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

logging.level.org.springframework.security=DEBUG

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

server.port=8082

jwt.secret=_GIVE_YOUR_OWN_SECRET_KEY_(GENERATE A 64 BIT KEY FROM INTERNET)
jwt.expiration=86400000 #1DAY

logging.file.name=logs/app.log
logging.level.root=INFO

logging.file.max-size=10MB
logging.file.max-history=10
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n
```

## 🎨 Logging
- Logging is enabled with `Slf4j`
- Logs saved in `logs/app.log`
- Logs include user registration, login, restaurant actions, orders, and feedback

## 🚚 Before Running the Project
```bash
mvn clean
mvn install
```
---

Built with ❤️ by 
### Venkata Sai Rahul
