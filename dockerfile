# Build stage
FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Install Maven
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Build the application
RUN mvn clean package -DskipTests

# List files to debug (helpful to see what was created)
RUN echo "Contents of target directory:" && ls -la target/

# Runtime stage
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy ANY jar file from target directory
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]