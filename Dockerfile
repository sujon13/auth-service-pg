# Use the Eclipse Temurin JDK 21 base image to build the application
FROM eclipse-temurin:21-jdk-alpine AS build

# Set the working directory for the build stage
WORKDIR /app

# Copy the Gradle wrapper and build files to the container
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Download Gradle dependencies (this will cache dependencies for faster builds)
RUN ./gradlew --no-daemon dependencies

# temporary solution
#COPY gradle-8.10.1-bin.zip /tmp/gradle.zip
#RUN unzip /tmp/gradle.zip -d /opt && \
#    ln -s /opt/gradle-8.10.1/bin/gradle /usr/bin/gradle


# Copy the source code into the container
COPY src src

# Build the Spring Boot application using Gradle
RUN ./gradlew build --no-daemon -x test
# RUN gradle build --no-daemon -x test

# Use a smaller JDK 21 image to run the application
FROM eclipse-temurin:21-jdk-alpine
#
## Set the working directory for the run stage
WORKDIR /app
#
## Copy the JAR file from the build stage into this final image
COPY --from=build /app/build/libs/*.jar /app/application.jar
#
## Expose the port on which the Spring Boot app will run (default 8080)
EXPOSE 8080
#
## Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "application.jar"]
