FROM eclipse-temurin:21-jdk-alpine

COPY ./target/transfer-service-*.jar /transfer-service.jar

ENTRYPOINT ["sh", "-c", "java -jar /transfer-service.jar"]
