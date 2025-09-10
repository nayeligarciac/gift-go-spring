
### Spring project

## Description
This project is a Spring Boot application that filters incoming HTTP requests based on client IP address and geolocation.
It uses custom filters and services to validate IPs and log request details.

## Technologies
- Java
- Spring Boot
- Gradle

## Prerequisites
- Java 17+
- Gradle 7+

## Setup
1. **Clone the repository**
  ```bash
    git clone https://github.com/nayeligarciac/gift-go-spring.git
    cd gift-go-spring
  ```
2. **Build the project**
  ```bash
   ./gradlew build
  ```
3. **Run the application**
 
 ```bash
    ./gradlew run
 ```
## Testing
To run tests:
 ```bash
    ./gradlew test
 ```

## API Endpoints

| Method | Path       | Description                                            |
|--------|------------|--------------------------------------------------------|
| POST   | /file      | Takes a file and process it to create an Outcome file. |
| GET    | /file/logs | Retrieves request logs                                 |
