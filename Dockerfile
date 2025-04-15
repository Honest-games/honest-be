FROM node:18-alpine AS diplodoc
RUN npm install -g @diplodoc/cli
WORKDIR /app
COPY docs ./
RUN yfm -o ./docs-out

FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml ./

RUN mvn dependency:go-offline -B

COPY src ./src
COPY --from=diplodoc app/docs-out ./src/main/resources/static/docs

RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 80

ENTRYPOINT ["java", "-jar", "app.jar"]
