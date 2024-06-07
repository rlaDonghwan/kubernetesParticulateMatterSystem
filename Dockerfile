FROM openjdk:17-jdk-slim

# Add a volume pointing to /tmp
VOLUME /tmp

# Make port 8181 available to the world outside this container
EXPOSE 8181

# Install curl and other necessary tools
RUN apt-get update && apt-get install -y curl net-tools

# The application's jar file
ARG JAR_FILE=build/libs/kubernetesParticulateMatterSystem-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
ADD ${JAR_FILE} app.jar

# Run the jar file
ENTRYPOINT ["java","-jar","/app.jar"]