FROM openjdk:17-jdk-slim
COPY target/your-spring-boot-app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
