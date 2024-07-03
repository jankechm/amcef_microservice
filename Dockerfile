FROM amazoncorretto:21-alpine-jdk
ENV spring_profiles_active demo
COPY target/microservice-0.0.1-SNAPSHOT.jar microservice-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/microservice-0.0.1-SNAPSHOT.jar"]