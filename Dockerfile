FROM maven:3.5.2-jdk-8-alpine

COPY pom.xml /build/
COPY src /build/src/

WORKDIR /build/
#RUN mvn install -Dmaven.test.skip=true

ENTRYPOINT ["mvn","spring-boot:run"]
