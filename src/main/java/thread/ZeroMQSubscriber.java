package thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import org.zeromq.ZMQ;

import com.google.gson.Gson;

public class ZeroMQSubscriber implements Runnable {
	
	final static Logger log = Logger.getLogger("thread.kdbfeed.ZeroMQSubscriber");
	final static Gson gson = new Gson();
	
	String gatewayHost;
	Integer gatewayPort;
	LinkedBlockingQueue<OrderBook> queue;
	
	ZeroMQSubscriber(String gatewayHost, Integer gatewayPort, LinkedBlockingQueue<OrderBook> queue) {
		this.gatewayHost = gatewayHost;
		this.gatewayPort = gatewayPort;
		this.queue = queue;
	}
	
	public void run() {
		// Initialize ZeroMQ subscriber
		ZMQ.Context context = ZMQ.context(1);
		ZMQ.Socket socket = context.socket(ZMQ.SUB);
		socket.connect("tcp://"+gatewayHost+":"+gatewayPort.toString());
		String filter = ""; // Get everything
		socket.subscribe(filter.getBytes());
		while (!Thread.currentThread().isInterrupted()) {
			String string = socket.recvStr();
			OrderBook ob = gson.fromJson(string, OrderBook.class);			
			queue.add(ob);
		}
	}
	

}
