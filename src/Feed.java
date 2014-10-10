abstract public class Feed implements Runnable {
	OneFeed mgr;

	public Feed(OneFeed mgr) {
		this.mgr = mgr;
	}

	public void sendFeedEvent(String evts) {
		mgr.getFeedEvent(new FeedEvent(this, evts));
	}
}

