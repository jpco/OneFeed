import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;

public class OneFeed implements FeedListener {
    Set<Feed> feeds;
	List<FeedEvent> elist;

    public static void main(String[] args) {
        new OneFeed().init();
    }
	private void init() {
		feeds = new HashSet<Feed>();
		elist = new LinkedList<FeedEvent>();
		System.out.println("Starting OneFeed");

		// add feeds here
		feeds.add(new DummyFeed(this, "dFeed 1!"));
		feeds.add(new DummyFeed(this, "dFeed 2!"));

		for(Feed feed : feeds) {
			(new Thread(feed)).start();
		}
	}

	public void getFeedEvent(FeedEvent fe) {
		// elist.add(fe);
		System.out.println(fe.getString());
	}
}

interface FeedListener {
	public void getFeedEvent(FeedEvent fe);
}
