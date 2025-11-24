FROM eclipse-temurin:17-jdk
COPY build/libs/*SNAPSHOT.jar app.jar
ENV TZ=Asia/Seoul
ENTRYPOINT ["java", "-jar", "/app.jar"]