FROM eclipse-temurin:17-jre
WORKDIR /app
ENV SERVER_PORT=8080
COPY target/legal-diary.war app.war
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.war"]

