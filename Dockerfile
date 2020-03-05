FROM openjdk:8
ADD target/twitter-polls.jar twitter-polls.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "twitter-polls.jar"]