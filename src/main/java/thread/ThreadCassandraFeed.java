package thread;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ThreadCassandraFeed {

	final static Logger log = Logger.getLogger("thread.ThreadCassandraFeed");
	
	static String gatewayHost;
	static Integer gatewayPort;
	static String cassandraHost;
	static String cassandraTable;
	static String cassandraKeyspace;

	public static void main(String[] args) {
		log.info("Loading Cassandra Feed...");
		
		// Load logging properties
		try {
			FileInputStream fis =  new FileInputStream("logging.properties");
			LogManager.getLogManager().readConfiguration(fis);
		    fis.close();
		} catch (FileNotFoundException e) {
			log.severe("Cannot find logging properties file: "+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			log.severe("Caught IOException trying to load logging properties: "+e.getMessage());
			e.printStackTrace();
		}
		
		// Load properties
		Properties p = new Properties();
		try {
			p.load(new FileInputStream("thread.properties"));
		} catch (FileNotFoundException e) {
			log.severe("Cannot find properties file: "+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			log.severe("Caught IOException trying to load properties: "+e.getMessage());
			e.printStackTrace();
		}
		cassandraHost = p.getProperty("cassandraHost");
		cassandraTable = p.getProperty("cassandraTable");
		cassandraKeyspace = p.getProperty("cassandraKeyspace");
		gatewayHost = p.getProperty("gatewayHost");
		gatewayPort = Integer.parseInt(p.getProperty("gatewayPort"));
		log.info("Got properties: "+p.toString());
		
		// Create pipeline and initialize subscriber
		LinkedBlockingQueue<OrderBook> queue = new LinkedBlockingQueue<OrderBook>();
		Thread subscriberThread = new Thread(
				new ZeroMQSubscriber(gatewayHost, gatewayPort, queue),
				"SubscriberThread");
		subscriberThread.start();
		
		// Start KDB publisher
		Thread cassandraPublisherThread = new Thread(
				new CassandraPublisher(cassandraHost, cassandraKeyspace, cassandraTable, queue),
				"CassandraPublisherThread");
		cassandraPublisherThread.start();


	}

}
