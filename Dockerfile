FROM openjdk:18
COPY /target/wallet-api-0.0.1-SNAPSHOT.jar wallet.jar
EXPOSE 8080
ENV MONGO_URL=${MONGO_URL}
ENV JWT_SECRET_KEY=${WT_SECRET_KEY}
ENTRYPOINT [ "java", "-jar", "wallet.jar" ]
