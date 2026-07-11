# Build stage
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

# Runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
RUN groupadd -r cooksmart && useradd -r -g cooksmart cooksmart
COPY --from=build /app/target/cooksmart-backend-1.0.0.jar app.jar
USER cooksmart
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
