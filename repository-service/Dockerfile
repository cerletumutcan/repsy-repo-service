# Java 24 JDK kullanan hafif bir Alpine imajı çektim.
FROM eclipse-temurin:24-jdk-alpine

# Uygulama JAR dosyasının adı
ARG JAR_FILE=target/repository-service-1.0-SNAPSHOT.jar

# JAR dosyasını container içine kopyalıyorum.
COPY ${JAR_FILE} app.jar

# Spring Boot uygulamasının default çalıştığı port
EXPOSE 8080

# Container başladığında bu komut çalışıyor olacak
ENTRYPOINT ["java", "-jar", "/app.jar"]

