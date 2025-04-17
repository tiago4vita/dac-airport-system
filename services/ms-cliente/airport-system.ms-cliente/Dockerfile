FROM gradle:8.5-jdk17 AS build

WORKDIR /app
COPY . .

# Initialize Gradle wrapper
RUN gradle wrapper

# Make gradlew executable and build
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

ENV SPRING_DEVTOOLS_REMOTE_SECRET=mysecret
ENV SPRING_DEVTOOLS_RESTART_ENABLED=true

ENTRYPOINT ["java", "-Dspring.devtools.restart.enabled=true", "-jar", "app.jar"] 