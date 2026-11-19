FROM docker.io/library/maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY . .
RUN mvn -q package -DskipTests

FROM docker.io/library/eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/app.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
