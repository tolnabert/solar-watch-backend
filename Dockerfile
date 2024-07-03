# Dockerfile

#Stage 1 (build)
#this eclipse has no maven in it
FROM maven:3.9.8-eclipse-temurin-21-alpine AS build
WORKDIR /app
#pom.xml saved to /app
COPY pom.xml .
#src folder created insdide the container
COPY src ./src
#might find a better instead of -DskipTests
RUN mvn clean package -DskipTests

#Stage 2 (dev)
FROM maven:3.9.8-eclipse-temurin-21-alpine AS dev
WORKDIR /app
#refer to the build stage and creates target folder and names the image as backend.jar
COPY --from=build /app/target/*.jar ./target/backend.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/target/backend.jar"]
