FROM maven:3.9.5-eclipse-temurin-17-alpine AS build
WORKDIR /app

COPY pom.xml ../pom.xml

COPY petfriends-almoxarifado/pom.xml ./pom.xml
COPY petfriends-almoxarifado/src ./src

RUN mvn clean package -DskipTests -f ./pom.xml

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8082

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8082/actuator/health || exit 1

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]

