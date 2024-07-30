FROM amazoncorretto:17
# FROM openjdk:17-jdk
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} jgs-spring-boot.jar
# COPY build/libs/*.jar my-project.jar
ENTRYPOINT ["java","-jar","/jgs-spring-boot.jar"]
RUN ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime
