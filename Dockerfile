ARG BUILD_IMAGE=public.ecr.aws/docker/library/maven:3.8.3-amazoncorretto-17
ARG RUNTIME_IMAGE=public.ecr.aws/amazoncorretto/amazoncorretto:17-al2-full

### STEP 1 build executable binary
FROM ${BUILD_IMAGE} AS builder

# PREPARE SRC TO BUILD
COPY pom.xml /tmp/
COPY src /tmp/src/
WORKDIR /tmp/

# BUILD
RUN mvn package -Dmaven.test.skip

## STEP 2 build final image (small image)
FROM ${RUNTIME_IMAGE}
COPY --from=builder /tmp/target/*.jar /app/application.jar

ENTRYPOINT ["sh", "-c", "env && java -jar /app/application.jar"]

#ENTRYPOINT ["java", "-jar", "/app/application.jar"]
