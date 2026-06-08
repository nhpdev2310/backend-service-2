#using mvn 3.9.15 and java 21 of temurin
FROM maven:3.9.15-eclipse-temurin-21 AS build
# create working directory in container
WORKDIR /app
# copy file pom.xml and first download these dependencies for cache, help build faster next time
COPY pom.xml .
RUN mvn dependency:go-offline -B
#Copy all source code to container
COPY src ./src
#Build project with maven to create .jar file (skip test for faster build this time)
RUN mvn package -DskipTests
#End phase 1

# Chỉ cần JRE 21 để chạy, giúp giảm dung lượng Image từ ~800MB xuống còn ~200MB
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Copy built file .jar from phase 1 to phase 2
COPY --from=build /app/target/*.jar backend-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "backend-service.jar"]