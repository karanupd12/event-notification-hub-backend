# ⚡ Event-Driven Notification Hub (EDNH) – Backend

A modern, production-ready **Spring Boot** backend for aggregating, filtering, and broadcasting real-time notifications from any number of sources (social networks, applications, external services) to users with robust, scalable, and secure APIs.

---

## 🚀 Features

- 🔑 **JWT-Based Authentication** (access/refresh tokens)  
- 🗄️ **MongoDB Persistence** for users, notifications, preferences and external applications  
- 🌐 **Webhook Ingestion API** – receive notifications from any external source  
- 📰 **Centralized Notification Feed** with status and filtering (unread, priority, type, etc.)  
- 🔔 **User Preferences and Muting/Quiet Hours**  
- ⚡ **Real-time WebSocket Delivery** using STOMP  
- 🏢 **Multi-App & Multi-Tenant Ready** (for multiple notification sources)  
- 🛡️ **Global Exception Handling and Validation**  
- 🧩 **Scalable, Modular Service/Repository Architecture**

---

## 🛠️ Tech Stack

- **Backend:** Spring Boot 3.2+, Spring Security, Spring WebSocket  
- **Database:** MongoDB with Spring Data  
- **Authentication:** JWT with refresh token rotation  
- **Real-time:** WebSocket with STOMP protocol  
- **Build Tool:** Maven 3.6+  
- **Java Version:** 17+

---

## 🏗️ Project Structure

### 📋 Directory Breakdown

#### **Core Application (`src/main/java/com/ednh/`)**
- **`EventNotificationHubApplication.java`** – Spring Boot main class  
- **`config/`** – Configuration classes (Security, JWT, WebSocket, MongoDB)  
- **`controller/`** – REST API endpoints and WebSocket controllers  
- **`dto/`** – Data Transfer Objects (request/response models)  
- **`entity/`** – MongoDB document schemas  
- **`repository/`** – Spring Data MongoDB repositories  
- **`service/`** – Business logic and service layer  
- **`websocket/`** – WebSocket handlers for real-time communication  
- **`exception/`** – Global exception handling  

#### **Resources (`src/main/resources/`)**
- **`application.properties`** – Main configuration file  
- **`application-dev.properties`** – Development environment config  
- **`static/`** – Static web assets (if any)  
- **`templates/`** – Template files (if using server-side rendering)  

#### **Testing (`src/test/java/com/ednh/`)**
- **Unit tests** for controllers, services, and repositories  
- **Integration tests** for API endpoints  
- **Test configuration** files  

#### **Build & Documentation**
- **`pom.xml`** – Maven dependencies and build configuration  
- **`target/`** – Compiled classes and build artifacts (generated)  
- **`docs/`** – Project documentation and guides  
- **`.mvn/`** – Maven wrapper files  
- **Maven scripts** (`mvnw`, `mvnw.cmd`) for cross-platform builds  

#### **Project Files**
- **`.gitignore`** – Git ignore patterns  
- **`README.md`** – Project documentation  
- **`HELP.md`** – Spring Boot generated help file  

---

### 🎯 Key Architecture Patterns

- **📁 Layered Architecture** – Controller → Service → Repository → Entity  
- **📁 DTO Pattern** – Separate request/response objects  
- **📁 Configuration Separation** – Environment-specific configs  
- **📁 Exception Handling** – Centralized error management  
- **📁 Real-time Communication** – WebSocket integration  
- **📁 Security Layer** – JWT authentication & authorization  

---

### 🔧 Dependencies (pom.xml highlights)

**Connector Example:**  
- Build microservices that poll social network APIs  
- Transform their events into EDNH notification format  
- POST to `/webhook/{social-platform-app-id}` with JWT authentication  

---

## 🗄️ Database Schema

### Collections

- **`users`** – Registered users with authentication data  
- **`notifications`** – All notifications with metadata and delivery status  
- **`applications`** – External registered sources (apps, social integrations)  
- **`refresh_tokens`** – JWT refresh tokens with expiration  
- **`user_preferences`** – User-specific notification settings and filters  

### Key Indexes

- `users.username` (unique)  
- `users.email` (unique)  
- `notifications.userId` + `notifications.createdAt`  
- `notifications.status` + `notifications.userId`  
- `applications.appId` (unique)  

---

## 🔐 Security

- **JWT Authentication:** Access tokens (15 min) + Refresh tokens (7 days)  
- **Password Hashing:** BCrypt with strength 12  
- **CORS Configuration:** Configurable allowed origins  
- **Rate Limiting:** Built-in per-application limits  
- **Input Validation:** Comprehensive DTO validation  
- **Secure Headers:** Production-ready security headers  

---

## 📊 Performance & Scaling

- **Concurrent Users:** Designed for 1000+ concurrent WebSocket connections  
- **Throughput:** 10,000+ notifications per minute  
- **Database:** Optimized MongoDB queries with proper indexing  
- **Memory:** ~512MB baseline, scales with user/notification volume  
- **Caching:** Application-level caching for user preferences  

### Horizontal Scaling
- Stateless design enables easy load balancing  
- WebSocket sessions can be clustered with Redis  
- MongoDB supports sharding for large datasets  

---

## ⭐ Acknowledgments

- Built with [Spring Boot](https://spring.io/projects/spring-boot)  
- Powered by [MongoDB](https://www.mongodb.com/)  
- Real-time messaging with [STOMP](https://stomp.github.io/)  
- Inspired by modern notification systems and event-driven architectures  

---

> **EDNH** is an open, extensible notification aggregation backend for modern SaaS and user-centric workflows.  
> Easily add more sources, extend with analytics, or build new real-time frontends on top of this solid, production-ready foundation.  

**⚡ Happy coding!** 🚀  

---

<div align="center">

Made with ❤️ by [Karan Upadhyay](https://github.com/karanupd12)

</div>
