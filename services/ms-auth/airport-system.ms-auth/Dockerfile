FROM openjdk:17-jdk-slim

WORKDIR /app

COPY . .

# Make gradlew executable and build the project
RUN chmod +x ./gradlew && \
    ./gradlew clean bootJar

EXPOSE 8080

CMD ["java", "-jar", "build/libs/airport-system.ms-auth-0.0.1-SNAPSHOT.jar"] 