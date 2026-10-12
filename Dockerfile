# UBI9 OpenJDK 17 runtime image (OpenShift-friendly)
FROM registry.access.redhat.com/ubi9/openjdk-17

# Create non-root user (OpenShift requires this)
USER 1001

WORKDIR /app

# Copy your locally built Spring Boot JAR
COPY target/storeops.jar /app/storeops.jar

# Expose Spring Boot port
EXPOSE 8080

# Optional JVM tuning
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Run the Spring Boot application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/storeops.jar"]
