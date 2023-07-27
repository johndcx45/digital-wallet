FROM maven:3.8.2-jdk-11

WORKDIR /app

COPY ./target/wallet-service.jar ./

ENTRYPOINT ["java", "-jar", "wallet-service.jar"]
