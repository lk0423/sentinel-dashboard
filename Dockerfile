FROM openjdk:8

MAINTAINER lankun0606@163.com

ENV PARAM ""
ENV VERSION 1.8.0

RUN mkdir -p /sentinel

WORKDIR /sentinel

ADD ./target/sentinel-dashboard.jar ./app-${VERSION}.jar

# 设置时区，默认为UTC
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

RUN echo 'Asia/Shanghai' > /etc/timezone

ENTRYPOINT ["java", "${PARAM}", "-jar", "app-${VERSION}.jar"]