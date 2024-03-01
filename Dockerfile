FROM openjdk:17-jdk-alpine
MAINTAINER jonatanSoto
COPY target/foodSimulator-1.0.4.jar foodSimulator-1.0.4.jar
ENTRYPOINT ["java","-jar","/foodSimulator-1.0.4.jar"]