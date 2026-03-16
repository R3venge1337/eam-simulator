# Używamy najnowszego obrazu Java 25 LTS na lekkim Alpine Linux
FROM eclipse-temurin:25-jdk-alpine

# Dobra praktyka: nie uruchamiamy aplikacji jako root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Kopiujemy JAR z folderu modułu eam-app
ARG JAR_FILE=eam-monolith/target/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]