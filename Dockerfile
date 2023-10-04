# Stage 1: Build the Spring Boot application
FROM maven:3.8.4 AS build
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mvn clean package

# Stage 2: Create a lightweight image
FROM openjdk:8-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
