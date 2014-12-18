package old;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * OneFeed; represents the model of the application. Might be bass-ackwards 
 * to initialize the program at the model, but whatever.
 * 
 * @author jpco
 */
public class OneFeed {
    Set<Feed> feeds;
	Set<Feed> rfeeds;
	List<FeedEvent> elist;
	Preferences prefs;
	OneFeedView view;

    public static void main(String[] args) {
		new OneFeed().init();
    }
	private void init() {
        view = new OneFeedCliView(this);
        (new Thread(view)).start();
        view.log("Starting OneFeed");

		feeds = new HashSet<Feed>();
		rfeeds = Collections.unmodifiableSet(feeds);
		elist = new LinkedList<FeedEvent>();

		prefs = Preferences.userRoot().node("OneFeed");
		reloadFeeds();

		// set up shutdown process
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
			public void run() {
                view.log("Running shutdown hook");

				for(Feed feed : rfeeds) {
					feed.kill();
				}
				try {
					prefs.sync();
					prefs.flush();
				} catch (BackingStoreException bse) {
					view.error("Couldn't save preferences.");
				}
			}
		}));
	}

	// I hate this entire method, holy cow
	private Feed makeFeedFromString(String name)
			throws ClassNotFoundException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		return (Feed)((Class.forName(name))
				.getConstructor(OneFeed.class, Preferences.class))
				.newInstance(this, prefs.node(name));
	}

	private void reloadFeeds() {
		for(Feed feed : rfeeds) {
			if(feed != null) feed.kill();
		}
		
		try {
			if(feeds.size() != prefs.childrenNames().length) {
				loadFeedsFromPrefs();
			}
		} catch (BackingStoreException ex) {
			view.error(ex, "Could not access preferences");
		}
		
		rfeeds = Collections.unmodifiableSet(feeds);
		for(Feed feed : rfeeds) {
			(new Thread(feed)).start();
		}
        view.updateFeedset(rfeeds);
	}
	
	private void loadFeedsFromPrefs() {
		feeds = new HashSet<Feed>();
		
		try {
			for(String feedName : prefs.childrenNames()) {
				try {
					Feed f = makeFeedFromString(feedName);
					feeds.add(f);
                    view.log("Added feed "+feedName);
                } catch (ClassNotFoundException ex) {
                    view.error(ex, "No such feed "+feedName);
                    prefs.node(feedName).removeNode();
				} catch (Exception ex) {
                    view.error(ex, "Couldn't load feed "+feedName);
				}
			}
		} catch (BackingStoreException bse) {
			view.error("Could not load preferences.");
		}
	}

	public void getFeedEvent(FeedEvent fe) {
		view.getFeedEvent(fe);
	}

	public void addFeed(String feedname) {
		try {
			Feed feed = makeFeedFromString(feedname);
			feeds.add(feed);
			reloadFeeds();
		} catch (Exception ex) {
			view.error("Could not create feed "+feedname+": "+ex);
		}
	}
}