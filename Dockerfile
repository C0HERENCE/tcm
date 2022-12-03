FROM maven:3-openjdk-11-slim as builder
COPY . /project
WORKDIR /project
COPY ./settings.xml /root/.m2/settings.xml
RUN mvn clean package -Dmaven.test.skip=true

FROM openjdk:11
COPY --from=builder /project/tcm-web/target/tcm.jar /
ENTRYPOINT ["java","-jar","/tcm.jar"]

EXPOSE 5000
