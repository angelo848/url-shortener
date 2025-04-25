# URL Shortener Application

## Overview

The **URL Shortener Application** is a reactive web application built with **Spring Boot** and **Project Reactor**. It provides functionality to shorten long URLs and redirect users to the original URLs using the shortened codes. The application is designed to be lightweight, efficient, and scalable, leveraging in-memory storage for simplicity.

## Features

- **Shorten URLs**: Generate a shortened URL for any valid input URL.
- **Redirect URLs**: Redirect users to the original URL using the shortened code.
- **Access Count Tracking**: Track the number of times a shortened URL has been accessed.
- **Error Handling**: Comprehensive error handling for invalid URLs and non-existent shortened codes.
- **Reactive Programming**: Fully asynchronous and non-blocking using **Spring WebFlux**.

## Technologies Used

- **Java 17**
- **Spring Boot**
- **Spring WebFlux**
- **Project Reactor**
- **Gradle** (Build Tool)
- **Lombok** (For reducing boilerplate code)

## Project Structure

- **`api`**: Contains controllers and DTOs for handling API requests and responses.
- **`application`**: Includes use cases, utilities, and service interfaces.
- **`core`**: Defines core entities, exceptions, and repository interfaces.
- **`infrastructure`**: Implements storage and database services.
- **`UrlShortenerApplication`**: The main entry point of the application.

## Endpoints

### 1. **Shorten URL**
- **POST** `/shorten`
  - **Request Body**:
    ```json
    {
      "url": "https://example.com"
    }
    ```
  - **Response**:
    ```json
    {
    "originalUrl": "https://example.com",
    "shortenedUrl": "abc123",
    "fullShortUrl": "http://localhost:8080/abc123"
    }
    ```

### 2. Redirect URL

**GET** `/{shortenedUrl}`  
**Response**: Redirects to the original URL.

### 3. Ping

**GET** `/ping`  
**Response**: `"pong"`

### Error Handling

- **404 Not Found**: Returned when a shortened URL does not exist.
- **400 Bad Request**: Returned for invalid URL formats.

---

## How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/asalles/url-shortener.git
   cd url-shortener
   ```

2. Build the project:
   ```bash
   ./gradlew build
   ```

3. Run the application:
   ```bash
   ./gradlew bootRun
   ```

4. Access the application at [http://localhost:8080](http://localhost:8080).

---

## Future Enhancements

- Add persistent storage (e.g., relational or NoSQL database).
- Implement caching for frequently accessed URLs.
- Add user authentication and authorization.
- Support custom expiration times for shortened URLs.

---

## License

This project is licensed under the MIT License.
