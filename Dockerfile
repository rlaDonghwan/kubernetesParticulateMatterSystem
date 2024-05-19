# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the build.gradle file and other Gradle files
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Give gradlew execution permission
RUN chmod +x gradlew

# Download dependencies
RUN ./gradlew build -x test || return 0

# Copy the rest of the project
COPY . .

# Build the application
RUN ./gradlew build -x test --no-daemon

# Copy the built jar file into the container
COPY build/libs/fineDust-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8181

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
