package master;

import java.util.LinkedList;
import java.util.Queue;

public class LoadBalancer {

	/** Allows the round-robin politics. */
	private Queue<String> rank;

	public LoadBalancer() {
		rank = new LinkedList<String>();
	}

	/**
	 * Adds a new ip to the rank.
	 * 
	 * @param e
	 */
	public void addToRank(String e) {
		rank.offer(e);
	}

	/**
	 * This is a dumb class with a dumb method, just return the front of the
	 * queue and put it at the end.
	 * 
	 * @return ip in the front of the queue
	 */
	public String lessUsed() {
		String ip = rank.remove();
		rank.offer(ip);
		return ip;
	}

	public String moreUsed() {
		return null;
	}

}
