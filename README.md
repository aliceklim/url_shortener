# URL Shortener Service

An asynchronous URL shortening service built with Spring Boot. It supports configurable expiration policies, hash generation, and a blocking queue mechanism to manage short URLs.

## Features

- **Shorten URLs**: Generate unique, base62-encoded short URLs.
- **Redirect**: Access original URLs via short codes.
- **Configurable Expiration**: Define expiration thresholds using ISO-8601 durations (e.g., `P1D` for 1 day).
- **Scheduled Cleanup**: Automatically remove expired URLs based on a cron expression.
- **Hash Caching**: Utilize `ArrayBlockingQueue` for efficient hash management.
- **Asynchronous Processing**: Leverage Spring’s `@Async` for non-blocking operations.
- **RESTful API**: Interact with the service using standard HTTP methods.

## Technologies Used

- Java 17
- Spring Boot 3
- Spring Data JPA
- PostgreSQL
- Lombok
- Gradle
