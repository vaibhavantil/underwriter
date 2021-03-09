##### Dependencies stage #####
FROM maven:3.6.3-amazoncorretto-11 AS dependencies
WORKDIR /usr/app

# Set up the user running the tests (needed for embedded postgres)
RUN yum -y install python3 \
    python3-pip \
    shadow-utils \
    util-linux
RUN adduser underwriter

# Resolve dependencies and cache them
COPY pom.xml .
RUN mvn dependency:go-offline -s /usr/share/maven/ref/settings-docker.xml
# This is the maven repo in /usr/share/maven/ref/settings-docker.xml
# has to be readable by 'underwriter'
RUN chown -R underwriter /usr/share/maven/ref/repository


##### Build stage #####
FROM dependencies AS build
COPY src/main src/main
RUN mvn clean package -s /usr/share/maven/ref/settings-docker.xml -P no-git-hooks


##### Test stage #####
FROM build AS test
COPY bin bin
COPY src/test src/test
RUN chown -R underwriter .

# Tests must be run as custom user because of EmbeddedPostgres
RUN su underwriter -c 'mvn test -s /usr/share/maven/ref/settings-docker.xml -P no-git-hooks'


##### Assemble stage #####
FROM amazoncorretto:11 AS assemble

# Fetch the datadog agent
RUN curl -o dd-java-agent.jar -L 'https://repository.sonatype.org/service/local/artifact/maven/redirect?r=central-proxy&g=com.datadoghq&a=dd-java-agent&v=LATEST'

COPY --from=build /usr/app/target/underwriter-0.0.1-SNAPSHOT.jar .

# Define entry point
ENTRYPOINT java -javaagent:/dd-java-agent.jar -jar underwriter-0.0.1-SNAPSHOT.jar
