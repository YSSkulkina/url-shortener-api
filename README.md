# 🔗 URL Shortener API

REST API for URL shortening built with **Java 21**, **Spring Boot 3**, **JWT**, **PostgreSQL**, **Redis**, **Flyway**, **Docker** and **GitHub Actions**.

The project demonstrates a production-style backend service with authentication, user-specific links, caching, database migrations, API documentation and CI pipeline.

---

## 🚀 Features

* User registration and login
* JWT authentication
* Create short URLs
* Redirect by short code
* View current user's links
* Delete own links
* URL expiration
* Click counter
* Redis cache for redirects
* PostgreSQL persistence
* Flyway database migrations
* Swagger/OpenAPI documentation
* Docker Compose setup
* GitHub Actions CI

---

## 🛠 Tech Stack

| Category    | Technologies                           |
| ----------- | -------------------------------------- |
| Language    | Java 21                                |
| Framework   | Spring Boot 3                          |
| Security    | Spring Security, JWT                   |
| Database    | PostgreSQL                             |
| Cache       | Redis                                  |
| Persistence | Spring Data JPA, Hibernate             |
| Migrations  | Flyway                                 |
| Mapping     | MapStruct                              |
| API Docs    | Swagger / OpenAPI                      |
| Build       | Maven                                  |
| Tests       | JUnit 5, Mockito                       |
| DevOps      | Docker, Docker Compose, GitHub Actions |

---

## 🏗 Architecture

```text
Client
  |
  v
Spring Security + JWT
  |
  v
REST Controllers
  |
  v
Service Layer
  |
  +--> PostgreSQL
  |
  +--> Redis Cache
```

---

## 🔐 Authentication Flow

```text
Register / Login
      |
      v
JWT Token
      |
      v
Authorization: Bearer <token>
      |
      v
JwtAuthenticationFilter
      |
      v
SecurityContext
      |
      v
Protected API
```

---

## ⚡ Redis Cache Flow

```text
GET /{shortCode}
      |
      v
Redis lookup
      |
      +-- Cache hit  --> Redirect
      |
      +-- Cache miss --> PostgreSQL --> Save to Redis --> Redirect
```

---

## 📡 API Endpoints

| Method | Endpoint             | Description                | Auth |
| ------ | -------------------- | -------------------------- | ---- |
| POST   | `/api/auth/register` | Register new user          | No   |
| POST   | `/api/auth/login`    | Login and receive JWT      | No   |
| POST   | `/api/links`         | Create short link          | Yes  |
| GET    | `/api/links`         | Get current user's links   | Yes  |
| DELETE | `/api/links/{id}`    | Delete current user's link | Yes  |
| GET    | `/{shortCode}`       | Redirect to original URL   | No   |

---

## 🐳 Running with Docker

### Requirements

* Docker
* Docker Compose

### Start application

```bash
docker compose up --build
```

Application will be available at:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

---

## 🔎 Swagger

After starting the application, open:

```text
http://localhost:8080/swagger-ui/index.html
```

To use protected endpoints:

1. Register or login.
2. Copy JWT token.
3. Click **Authorize**.
4. Paste the token.
5. Execute protected requests.

---

## 🧪 Running tests

```bash
mvn clean test
```

---

## 🗄 Database

The project uses **Flyway** for database migrations.

Migration files are located in:

```text
src/main/resources/db/migration
```

---

## ✅ CI/CD

GitHub Actions runs automatically on each push and pull request to `main`.

Pipeline includes:

```text
Checkout repository
Setup Java 21
Run Maven tests
```

---

## 📁 Project Structure

```text
src
├── main
│   ├── java/com/skulkina/url_shortener_api
│   │   ├── config
│   │   ├── controller
│   │   ├── dto
│   │   ├── entity
│   │   ├── exception
│   │   ├── mapper
│   │   ├── repository
│   │   ├── security
│   │   └── service
│   └── resources
│       └── db/migration
└── test
```

---

## 👩‍💻 Author

**Yulia Skulkina**
Java Backend Developer

GitHub: [YSSkulkina](https://github.com/YSSkulkina)
