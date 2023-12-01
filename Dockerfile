FROM registry.cn-hangzhou.aliyuncs.com/medo/node:12 AS buildDoc
WORKDIR /app
COPY . .
RUN npm --registry https://registry.npmmirror.com install apidoc@0.24.0 -g
RUN apidoc -i src/main/java -o src/main/resources/static/apidoc

FROM registry.cn-hangzhou.aliyuncs.com/medo/gradle:7.6-jdk17-alpine AS build
WORKDIR /app
COPY --from=buildDoc  /app .
RUN gradle bootJar


FROM registry.cn-hangzhou.aliyuncs.com/medo/openjdk:17.0.2-jdk
ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN mkdir /app
WORKDIR /app
COPY --from=build /app/build/libs/monibot-base-api-*.jar /app/monibot-base-api.jar
CMD ["java","-jar","monibot-base-api.jar"]
