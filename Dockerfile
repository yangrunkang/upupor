FROM openjdk:8-jdk-alpine
ARG JAR_FILE=upupor-web/target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar","-Xmx1024m","-Xms1024m"]