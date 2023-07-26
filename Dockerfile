FROM openjdk:8-jre

WORKDIR /app

COPY target/movie-player-bot-*.jar app.jar

CMD ["java", "-jar", "app.jar"]