# AMCEF Microservice

A RESTful microservice for managing users' posts.

## Requirements
* Java 21
* docker and docker-compose

## Instalation
At first, if you are using Windows OS and docker desktop, go to the pom.xml and locate _spring-boot-maven-plugin_. Uncomment the lines which contain
```
<docker>
  <host>//./pipe/dockerDesktopLinuxEngine</host>
</docker>
```
This is an issue with Docker Desktop since version 4.31.0 https://github.com/spring-projects/spring-boot/issues/41199.

### Generating OpenAPI documentation
The documentation is being generated during the integration test phase and the app has to be started (it is stopped right after that phase). For this purpose, the _dev_ profile has to be used. (This profile is also used when running the app in an IDE.)
```
./mvnw clean verify -Pdev
```
The Swagger UI OpenAPI definition is located at http://localhost:8080/swagger-ui/index.html. But first, the app has to be run.

### Running the app
At first, create the JAR file. This time, _demo_ profile has to be used.
```
./mvnw clean package -Pdemo
```
Now, 2 docker containers need to be run. One with a database and second with the web app. Docker compose handles that.
```
docker-compose -f compose-demo.yaml up --build -d
```
When the containers are up and healthy, you can test the endpoints. The URL is http://localhost:8080/posts/. For example, to get post with id=1, use http://localhost:8080/posts/1.

To stop and remove the containers, run:
```
docker-compose -f compose-demo.yaml down
```
