FROM arm64v8/amazoncorretto:17

ARG BUILD_TAG
ENV BUILD_TAG=${BUILD_TAG}

ARG PORT
EXPOSE ${PORT}

COPY com.etelie.server-* /libs/
CMD java -jar /libs/com.etelie.server-${BUILD_TAG}.jar
