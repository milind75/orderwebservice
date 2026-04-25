# ---- Build Stage ----
FROM gradle:9.4.1-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon

# ---- Run Stage ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8081

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
