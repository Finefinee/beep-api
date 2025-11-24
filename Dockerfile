FROM openjdk:17
COPY build/libs/*SNAPSHOT.jar app.jar
ENV TZ=Asia/Seoul
ENTRYPOINT ["java", "-jar", "/app.jar"]