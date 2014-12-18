package util;

/**
 * Utility interface to define messages from feeds to OneFeed.
 * @author jpco
 */
public interface FeedListener {
	/**
	 * Main message type to indicate an update in the feed.
	 * @param e A FeedEvent containing the necessary info
	 */
	public void update(FeedEvent e);
	
	/**
	 * Message type to indicate some failure in the feed.
	 * @param e A FeedEvent indicating error; must contain
	 * 			identity of the source and a trivial JsonTree
	 * 			with field "exception" with a String describing
	 * 			the error.
	 */
	public void except(FeedEvent e);
	
	/**
	 * A log message from the feed to the OneFeed object.
	 * @param e A trivial FeedEvent; must contain
	 * 			identity of the source and a trivial JsonTree
	 * 			with field "message" with the message a String.
	 */
	
	public void log(FeedEvent e);
}
