FROM maven:3.8.8-eclipse-temurin-11 AS builder

WORKDIR /app

COPY pom.xml ./
COPY weblog-web/pom.xml weblog-web/pom.xml
COPY weblog-admin/pom.xml weblog-admin/pom.xml
COPY weblog-common/pom.xml weblog-common/pom.xml
COPY weblog-jwt/pom.xml weblog-jwt/pom.xml

RUN mvn -pl weblog-web -am dependency:go-offline -Pprod -DskipTests

COPY weblog-web/src weblog-web/src
COPY weblog-admin/src weblog-admin/src
COPY weblog-common/src weblog-common/src
COPY weblog-jwt/src weblog-jwt/src

RUN mvn -pl weblog-web -am clean package -Pprod -DskipTests

FROM eclipse-temurin:11-jre

WORKDIR /app

ENV TZ=Asia/Shanghai
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS=""
ENV LOG_PATH=/app/logs

COPY --from=builder /app/weblog-web/target/weblog-web-*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "mkdir -p \"${LOG_PATH:-/app/logs}\" && exec java $JAVA_OPTS -jar /app/app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod}"]
