FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app

COPY . .

RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -pl productos -DskipTests

FROM eclipse-temurin:17-alpine

WORKDIR /app

COPY --from=builder /app/productos/target/productos-*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]