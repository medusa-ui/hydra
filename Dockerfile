# Docker multi-stage build

# 1. Building the App with Maven
FROM maven:3-openjdk-18-slim as build

COPY ./hydra /hydra
WORKDIR /hydra

RUN mvn -B clean install -DskipTests=true

COPY ./hydra-showcase /showcase
WORKDIR /showcase

# Run Maven build
RUN mvn -B clean install -DskipTests=true

# Just using the build artifact and then removing the build-container
FROM openjdk:19-jdk

VOLUME /tmp

# Add Spring Boot app.jar to Container
COPY --from=build "/showcase/target/hydra-*-SNAPSHOT.jar" app.jar

# Fire up our Spring Boot app by default
CMD [ "sh", "-c", "java -Dserver.port=$PORT -Dmedusa.hydra.secret.private=$privatekey -Dmedusa.hydra.secret.public=$publickey -Xmx500m -Xss512k -XX:CICompilerCount=2 -Dfile.encoding=UTF-8 -XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
