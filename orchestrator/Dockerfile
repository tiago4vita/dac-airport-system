FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app
COPY . .

# Build with Maven
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENV SPRING_DEVTOOLS_REMOTE_SECRET=mysecret
ENV SPRING_DEVTOOLS_RESTART_ENABLED=true

ENTRYPOINT ["java", "-Dspring.devtools.restart.enabled=true", "-jar", "app.jar"]