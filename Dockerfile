FROM amazoncorretto:17-alpine

RUN apk add --no-cache curl

WORKDIR /app
COPY target/keycloak-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"] 