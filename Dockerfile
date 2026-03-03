# Stage 1: Build the Maven project
FROM maven:3.8.4-openjdk-11-slim AS build
WORKDIR /app

# Download dependencies first to cache them
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the WAR file
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime environment with Tomcat
FROM tomcat:9.0.83-jdk11

# Remove default ROOT application to deploy ours at the root path (/)
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Copy the constructed WAR file and deploy it as the ROOT app
COPY --from=build /app/target/ocean-view-resort-*.war /usr/local/tomcat/webapps/ROOT.war

# Port 8080 is exposed by default in Tomcat image, Railway will detect and map it
EXPOSE 8080
CMD ["catalina.sh", "run"]
