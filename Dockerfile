FROM openjdk:23-jdk
LABEL authors="bigch"
ENV PROJECT_DIR=/app
WORKDIR $PROJECT_DIR
COPY build/libs/ttrpg-battle-manager-backend-0.0.4-SNAPSHOT.jar app.jar
CMD ["java","-jar","app.jar"]