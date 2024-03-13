# Loan App Backend

This backend system is developed using Java Spring Boot and incorporates microservices architecture, Zuul for API gateway, and Eureka server for service registration and discovery. Additionally, the entire application has been containerized using Docker for seamless deployment.

## Features

- **Microservices Integration**: The backend is structured as microservices, providing modularity and scalability.

- **API Gateway with Zuul**: Zuul is implemented as an API gateway, facilitating routing and filtering of requests to the appropriate microservices.

- **Service Registration and Discovery with Eureka**: Eureka server is utilized for service registration and discovery, enabling efficient communication between microservices.

- **Dockerized Application**: The entire application is containerized using Docker, simplifying deployment and ensuring consistency across different environments.

## Getting Started

Follow these steps to set up and run the Loan App Backend on your local machine.

### Prerequisites

- Java Development Kit (JDK)
- Docker
- [Spring Tool Suite (STS)](https://spring.io/tools) or your preferred Java IDE

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/Winfred-Achieng/Backend-Loanapp-client1.git
2. Open the project in your Java IDE.

3. Build and run each microservice using STS or the following command:

    ```bash
    ./mvnw spring-boot:run
    ```

4. Navigate to the Zuul API Gateway and Eureka server URLs to verify successful setup.

## Docker Deployment

1. **Build Docker images for each microservice:**

    ```bash
    docker build -t loan-service-client1:latest ./loan-service-client1
    docker build -t loan-service-client2:latest ./loan-service-client2
    docker build -t gateway-service:latest ./gateway-service
    docker build -t eureka-service:latest ./eureka-service
    ```

2. **Run Docker containers:**

    ```bash
    docker-compose up
    ```

## Usage

- Access the Zuul API Gateway for endpoint routing: [http://localhost:8080](http://localhost:8080)

- Monitor service registration and discovery with Eureka: [http://localhost:8761](http://localhost:8761)

## Contributing

If you'd like to contribute, please fork the repository and create a pull request. Feel free to open issues for any enhancements or bug fixes.

