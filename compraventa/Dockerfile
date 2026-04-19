FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app

COPY . .

RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -pl compraventa -DskipTests

FROM eclipse-temurin:17-alpine

WORKDIR /app

COPY --from=builder /app/compraventa/target/compraventa-*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]