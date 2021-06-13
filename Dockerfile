FROM openjdk:8-jdk-alpine
RUN apk add xfce4
WORKDIR /opt/app
COPY voucher.jrxml /opt/app
COPY transtourImage.png /opt/app
COPY build/libs/*.jar  /opt/app/app.jar
RUN mkdir -p /opt/app/voucher
ENTRYPOINT ["java","-jar", "/opt/app/app.jar"]
