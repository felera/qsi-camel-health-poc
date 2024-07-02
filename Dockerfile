# Use an official OpenJDK runtime as a parent image
FROM openjdk:11-jre-slim

# Set the working directory
WORKDIR /app

# Copy the application jar file to the container
COPY target/qsi-camel-health-poc-0.0.1-SNAPSHOT.jar /app/qsi-camel-health-poc-0.0.1-SNAPSHOT.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "qsi-camel-health-poc-0.0.1-SNAPSHOT.jar"]
