FROM maven:3.6.3-openjdk-8-slim as builder

WORKDIR '/api'

COPY . .

RUN mvn clean package -DskipTests -Dmaven.javadoc.skip=true -B -V


FROM openjdk:8-jdk-alpine

COPY --from=builder /api/target/lib .
COPY --from=builder /api/target/*.jar .

CMD ["java","-jar","*.jar"]