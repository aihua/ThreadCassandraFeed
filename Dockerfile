FROM java:8-jre
MAINTAINER Nick Franklin <n.o.franklin@gmail.com>

# Copy binaries/config
RUN mkdir /target
RUN mkdir /cql
COPY ThreadCassandraFeed.sh /
COPY thread.properties /
COPY logging.properties /
COPY target/MarketCassandraFeed-0.0.1-SNAPSHOT-jar-with-dependencies.jar /target/
COPY cql/* /cql/

CMD ["/ThreadCassandraFeed.sh"]
