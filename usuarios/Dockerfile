FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app

COPY . .

RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -pl usuarios -DskipTests

FROM jetty:9.4-jdk17

WORKDIR /var/lib/jetty

COPY --from=builder /app/usuarios/target/usuarios-*.war ./webapps/ROOT.war

ENV JETTY_HTTP_PORT=8081
EXPOSE 8081