# Multi-stage: React UI + Spring Boot API in one image (easy Render/Railway deploy)

FROM node:22-alpine AS frontend-build
WORKDIR /frontend
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend/ ./
ENV VITE_API_BASE_URL=
RUN npm run build

FROM maven:3.9.9-eclipse-temurin-17 AS backend-build
WORKDIR /backend
COPY backend/pom.xml .
COPY backend/src ./src
COPY --from=frontend-build /frontend/dist ./src/main/resources/static
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
RUN groupadd -r cooksmart && useradd -r -g cooksmart cooksmart
COPY --from=backend-build /backend/target/cooksmart-backend-1.0.0.jar app.jar
USER cooksmart
EXPOSE 8080
ENV SERVER_PORT=8080
ENTRYPOINT ["java", "-jar", "app.jar"]
