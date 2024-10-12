# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the application's JAR file to the container
COPY target/myapp.jar /app/myapp.jar

# Expose the port the app runs on
EXPOSE 8080

# Define environment variables
ENV DB_URL=jdbc:postgresql://localhost:5433/task-tracker
ENV DB_USER=${DB_USER}
ENV DB_PASSWORD=${DB_PASSWORD}

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/myapp.jar"]
