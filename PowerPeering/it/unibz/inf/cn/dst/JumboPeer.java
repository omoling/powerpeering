package it.unibz.inf.cn.dst;

public interface JumboPeer {

	/**
	 * Ping messages - request the transitive closure of connected nodes to
	 * identify them, essentially asking the question "Are you there?"
	 */
	public void ping();

	/**
	 * Pong messages - response by a node upon receiving a Ping; the responding
	 * node provides her e- mail address, the number of sharable files it
	 * contains and a list of her neighbors. This gives the answer that
	 * "Yes, I am here..."
	 */
	public void pong();

	/**
	 * Query messages - request to locate a set of files matching some filter
	 * criteria. These are messages stating, "I am looking for…"
	 */
	public void query();

	/**
	 * QueryHit messages - response to a Query message giving a list of files
	 * matching the filter criteria and the e-mail address of the provider
	 */
	public void queryHit();

	/**
	 * Get messages – the requestor asks the provider to send the requested file
	 * as an e-mail attachment
	 */
	public void get();

	/**
	 * Put messages – the provider sends the requested file
	 */
	public void put();

}
