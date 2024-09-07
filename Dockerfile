FROM openjdk:17-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=target/syntax_flavor_backend-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]