FROM maven:3.8.5-openjdk-18 as build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:18
COPY --from=build /target/wallet-api-0.0.1-SNAPSHOT.jar wallet.jar
EXPOSE 8080
ENV MONGO_URL=${MONGO_URL}
ENV JWT_SECRET_KEY=${JWT_SECRET_KEY}
ENTRYPOINT [ "java", "-jar", "wallet.jar" ]
