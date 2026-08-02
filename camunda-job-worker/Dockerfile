FROM gradle:9-jdk-25-and-26-alpine AS build
WORKDIR /app

COPY build.gradle settings.gradle lombok.config /app/
COPY src /app/src

RUN gradle build --no-daemon


FROM eclipse-temurin:26-jdk-alpine
WORKDIR /app

COPY --from=build /app/build/libs/camunda-job-workers.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]