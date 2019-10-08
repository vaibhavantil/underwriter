FROM amazoncorretto:11

ADD target/underwriter-0.0.1-SNAPSHOT.jar /

ENTRYPOINT java -jar underwriter-0.0.1-SNAPSHOT.jar
