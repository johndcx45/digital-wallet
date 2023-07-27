FROM maven:3.8.2-jdk-11

WORKDIR /app

COPY ./target/user-service.jar ./

ENTRYPOINT ["java", "-jar", "user-service.jar"]
