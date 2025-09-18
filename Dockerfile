FROM openjdk:21-jdk-slim

ARG JAR_FILE=target/examapp-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

COPY src/main/resources/application.yml application.yaml

EXPOSE 9090
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.config.location=classpath:/application.yaml"]
