# Dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]