FROM maven:3.9.5-eclipse-temurin-17-alpine AS build
WORKDIR /workspace

# Copia o POM pai para a raiz do workspace
COPY pom.xml ./pom.xml

# Copia ambos os módulos (POM pai declara ambos, Maven precisa validá-los)
COPY petfriends-almoxarifado/pom.xml ./petfriends-almoxarifado/pom.xml
COPY petfriends-almoxarifado/src ./petfriends-almoxarifado/src

COPY petfriends-transporte/pom.xml ./petfriends-transporte/pom.xml
COPY petfriends-transporte/src ./petfriends-transporte/src

# Build apenas do módulo almoxarifado
RUN mvn clean package -DskipTests -pl petfriends-almoxarifado -am

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /workspace/petfriends-almoxarifado/target/*.jar app.jar

EXPOSE 8082

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8082/actuator/health || exit 1

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]

