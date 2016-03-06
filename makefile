all:
	mvn assembly:assembly
	docker build -t nof20/threadcassandrafeed:latest .
	docker push nof20/threadcassandrafeed

tar:
	mvn assembly:assembly
	tar cvf ThreadCassandraFeed.tar ThreadCassandraFeed.sh
	tar rf ThreadCassandraFeed.tar *.properties
	tar rf ThreadCassandraFeed.tar target/ThreadCassandraFeed-*with-dependencies.jar
	tar rf ThreadCassandraFeed.tar cql/*
	gzip ThreadCassandraFeed.tar
