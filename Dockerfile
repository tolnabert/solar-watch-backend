# Dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/*.jar ./app-solarwatch.jar
ENTRYPOINT ["java","-jar","app-solarwatch.jar"]