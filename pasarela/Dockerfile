FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app

COPY . .

RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -pl pasarela -DskipTests

FROM eclipse-temurin:17-alpine

WORKDIR /app

COPY --from=builder /app/pasarela/target/pasarela-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]