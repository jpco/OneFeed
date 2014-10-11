import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class OneFeed {
    Set<Feed> feeds;
	Set<Feed> rfeeds;
	List<FeedEvent> elist;
	Preferences prefs;
	OneFeedFrontend frontend;

    public static void main(String[] args) {
		if(args.length == 2 && args[0].equals("addfeed")) {
			new OneFeed().addFeed(args);
		} else {
			new OneFeed().init();
		}
    }
	private void init() {
		System.out.println("Starting OneFeed");

		feeds = new HashSet<Feed>();
		rfeeds = Collections.unmodifiableSet(feeds);
		elist = new LinkedList<FeedEvent>();
		
		// load preferences
		prefs = Preferences.userRoot();

		// create feeds based on preferences
		try {
			for(String feedName : prefs.childrenNames()) {
				try {
					feeds.add(makeFeedFromString(feedName));
				} catch (Exception ex) {
					System.out.println("Couldn't load feed "+feedName);
					System.out.println(ex);
				}
			}
		} catch (BackingStoreException bse) {
			System.out.println("Could not load preferences.");
		}

		// start feeds!
		rfeeds = Collections.unmodifiableSet(feeds);
		for(Feed feed : rfeeds) {
			(new Thread(feed)).start();
		}

		// set up shutdown process
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
			public void run() {
				System.out.println("Running shutdown hook");
				
				for(Feed feed : rfeeds) {
					feed.kill();
				}
				try {
					prefs.flush();
				} catch (BackingStoreException bse) {
					System.out.println("Couldn't save preferences.");
				}
			}
		}));
	}

	private void addFeed(String[] nFeed) {
		prefs = Preferences.userRoot();
		prefs.node("/"+nFeed[1]);
	}
	
	// I hate this entire method, holy cow
	private Feed makeFeedFromString(String name)
			throws ClassNotFoundException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		return (Feed)((Class.forName(name))
				.getConstructor(OneFeed.class, Preferences.class))
				.newInstance(this, prefs.node("/"+name));
	}

	private void reloadFeeds() {
		for(Feed feed : rfeeds) {
			feed.kill();
		}
		rfeeds = Collections.unmodifiableSet(feeds);
		for(Feed feed : rfeeds) {
			(new Thread(feed)).start();
		}
	}

	public void getFeedEvent(FeedEvent fe) {
		System.out.println(fe.getSource().getName() + ": " + fe.getString());
	}
}
