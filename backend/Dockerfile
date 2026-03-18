# Build stage (Maven)
FROM maven:3.9.4-eclipse-temurin-21 AS builder

WORKDIR /app

# Download dependencies first (cache layer)
COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline

# Copy sources and build
COPY src ./src
RUN mvn -B -DskipTests package

# Runtime stage
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]