FROM gradle:8.5-jdk17 AS build

WORKDIR /app
COPY . .
RUN gradle build -x test

FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

ENV SPRING_DEVTOOLS_REMOTE_SECRET=mysecret
ENV SPRING_DEVTOOLS_RESTART_ENABLED=true

ENTRYPOINT ["java", "-Dspring.devtools.restart.enabled=true", "-Dspring-boot.run.jvmArguments='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8000'", "-jar", "app.jar"] 