import java.time.Instant;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

public class CassandraPublisher implements Runnable {

	final static Logger log = Logger.getLogger("thread.CassandraPublisher");
	
	LinkedBlockingQueue<OrderBook> queue;
	String table;
	Cluster cluster;
	Session session;
	
	CassandraPublisher(String host, String keyspace, String table, LinkedBlockingQueue<OrderBook> queue) {
		try {
			log.info("Connecting to Cassandra on host "+host);
			cluster = Cluster.builder().addContactPoint(host).build();
			session = cluster.connect(keyspace);
		} catch (Exception e) {
			log.severe("Caught exception trying to connect to Cassandra");
			e.printStackTrace();
		}
		this.table = table;
		this.queue = queue;
	}
	
	public void run() {
		log.info("Processing delta updates");
		while (! Thread.currentThread().isInterrupted()) {
			// Main loop in normal running
			try {
				OrderBook ob = queue.take();
				PreparedStatement ps = session.prepare(
						"INSERT INTO tick "+
						"(currencyPair, MarketTime, OrderBookBuilderStartTime, OrderBookBuilderEndTime, sequenceNumber, BidPrice2, BidSize2, BidPrice1, BidSize1, BidPrice0, BidSize0, AskPrice0, AskSize0, AskPrice1, AskSize1, AskPrice2, AskSize2) " +
						"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
				BoundStatement bs = new BoundStatement(ps);
				session.execute(bs.bind(
						ob.currencyPair, 
						ob.MarketTime,
						ob.OrderBookBuilderStartTime,
						ob.OrderBookBuilderEndTime,
						Instant.now(),
						ob.sequenceNumber,
						ob.BidPrice2,
						ob.BidSize2,
						ob.BidPrice1,
						ob.BidSize1,
						ob.BidPrice0,
						ob.BidSize0,
						ob.AskPrice0,
						ob.AskSize0,
						ob.AskPrice1,
						ob.AskSize1,
						ob.AskPrice2,
						ob.AskSize2));
				
			} catch (InterruptedException e) {
				log.severe("Caught InterruptedException in CassandraPublisher");
				e.printStackTrace();
			}
		}
	}
	
}
