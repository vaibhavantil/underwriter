
##### Dependencies stage #####
FROM maven:3.6.3-amazoncorretto-11 AS dependencies
WORKDIR /usr/app

# Resolve dependencies and cache them
COPY pom.xml .
RUN mvn dependency:go-offline -s /usr/share/maven/ref/settings-docker.xml


##### Build stage #####
FROM dependencies AS build

# Copy application source and build it
COPY src/main src/main
# A plugin in the pom.xml file requires maven to be executed inside a git repo
COPY .git .git
# Git repo needed for git hook plugin
RUN mvn clean package -s /usr/share/maven/ref/settings-docker.xml


##### Test stage #####
FROM scratch AS test
# No-op, this service only has integration tests

##### Integration test stage #####
FROM build AS integration_test
COPY src/test src/test
RUN mvn test-compile -s /usr/share/maven/ref/settings-docker.xml

RUN yum -y install python3 \
    python3-pip \
    shadow-utils \
    util-linux
RUN adduser underwriter
USER underwriter
ENV POSTGRES_URL=jdbc:postgresql://test_db:5432

ENTRYPOINT ["mvn", "integration-test", "-f", "/usr/app/pom.xml", "-s", "/usr/share/maven/ref/settings-docker.xml"]


##### Assemble stage #####
FROM amazoncorretto:11 AS assemble

# Fetch the datadog agent
RUN curl -o dd-java-agent.jar -L 'https://repository.sonatype.org/service/local/artifact/maven/redirect?r=central-proxy&g=com.datadoghq&a=dd-java-agent&v=LATEST'

# Copy the jar from build stage to this one
COPY --from=build /usr/app/target/underwriter-0.0.1-SNAPSHOT.jar .

# Define entry point
ENTRYPOINT java -javaagent:/dd-java-agent.jar -jar underwriter-0.0.1-SNAPSHOT.jar
