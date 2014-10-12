import java.util.Set;

abstract public class OneFeedFrontend {
	OneFeed mgr;

	public OneFeedFrontend(OneFeed m) {
		mgr = m;
	}

	abstract public void init();
	abstract public void destroy();

	abstract public void error(Exception ex, String str);
	abstract public void error(String str);
	abstract public void log(String str);
	abstract public void updateFeedset(Set<Feed> feedset);
	abstract public void getFeedEvent(FeedEvent fe);
}
