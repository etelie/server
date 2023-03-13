FROM amazoncorretto:17-alpine-jdk

ARG PROJECT_VERSION
ENV PROJECT_VERSION=${PROJECT_VERSION}

COPY com.etelie.server-* /libs/
CMD java -jar /libs/com.etelie.server-${PROJECT_VERSION}.jar
