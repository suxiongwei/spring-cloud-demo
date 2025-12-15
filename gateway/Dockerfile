# ----- Build stage -----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY gateway/pom.xml gateway/
COPY model/pom.xml model/
# 复制整个源码（也可以用更细的 COPY，视 CI 需求而定）
COPY . .
RUN mvn -pl gateway -am package -DskipTests

# ----- Runtime stage -----
FROM eclipse-temurin:21-jre
WORKDIR /app
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV SPRING_PROFILES_ACTIVE=dev

COPY target/gateway-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 80
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
