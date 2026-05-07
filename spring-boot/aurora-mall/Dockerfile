FROM maven:3.9.9-eclipse-temurin-8 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:8-jre
WORKDIR /app
ENV APP_PROFILE=prod
COPY --from=build /workspace/target/aurora-mall-1.0.0-SNAPSHOT.jar /app/app.jar
EXPOSE 28089
ENTRYPOINT ["sh", "-c", "java -jar /app/app.jar"]
