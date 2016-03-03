CREATE KEYSPACE thread WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };
use thread;
CREATE TABLE tick (
	currencyPair text,
	MarketTime timestamp,
	OrderBookBuilderStartTime timestamp,
	OrderBookBuilderEndTime timestamp,
	sequenceNumber int,
	BidPrice2 double,
	BidSize2 double,
    BidPrice1 double,
    BidSize1 double,
    BidPrice0 double,
    BidSize0 double,
    AskPrice0 double,
    AskSize0 double,
    AskPrice1 double,
    AskSize1 double,
    AskPrice2 double,
    AskSize2 double,
    PRIMARY KEY ((currencyPair, MarketTime))
);