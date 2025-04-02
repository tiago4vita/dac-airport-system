FROM openjdk:17-jdk-slim

WORKDIR /app

COPY . .

RUN ./gradlew build -x test

EXPOSE 8080

CMD ["java", "-jar", "build/libs/airport-system.ms-auth-0.0.1-SNAPSHOT.jar"] 