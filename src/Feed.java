import java.util.prefs.Preferences;

abstract public class Feed implements Runnable {
	protected OneFeed mgr;
	protected Preferences prefs;

	public Feed(OneFeed mgr, Preferences pref) {
		this.mgr = mgr;
		prefs = pref;
	}

	final public void sendFeedEvent(String evts) {
		mgr.getFeedEvent(new FeedEvent(this, evts));
	}

	abstract public void kill();

	abstract public String getName();
	abstract public String getSName();
}

