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
			OneFeed of = new OneFeed();
			of.frontend = new OneFeedFrontendCli(of);
			of.prefs = Preferences.userRoot().node("OneFeed");
			of.addFeed(args[1]);
		} else if(args.length == 2 && args[0].equals("rmfeed")) {
			OneFeed of = new OneFeed();
			of.frontend = new OneFeedFrontendCli(of);
			of.prefs = Preferences.userRoot().node("OneFeed");
			of.rmFeed(args[1]);
		} else {
			new OneFeed().init();
		}
    }
	private void init() {
        frontend = new OneFeedFrontendCli(this);
        frontend.init();
        frontend.log("Starting OneFeed");

		feeds = new HashSet<Feed>();
		rfeeds = Collections.unmodifiableSet(feeds);
		elist = new LinkedList<FeedEvent>();

		// load preferences
		prefs = Preferences.userRoot().node("OneFeed");
		loadFeedsFromPrefs();

		// start feeds!
		rfeeds = Collections.unmodifiableSet(feeds);
		for(Feed feed : rfeeds) {
			(new Thread(feed)).start();
		}

		// set up shutdown process
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
			public void run() {
                frontend.log("Running shutdown hook");

				for(Feed feed : rfeeds) {
					feed.kill();
				}
				try {
					prefs.sync();
				//	printPrefs();
					prefs.flush();
				} catch (BackingStoreException bse) {
					frontend.error("Couldn't save preferences.");
				}
			}
		}));
	}

	private void addFeed(String nFeed) {
		Preferences fPref = prefs.node(nFeed);
		try {
			Class.forName(nFeed).getDeclaredMethod("add", OneFeed.class, Preferences.class).invoke(null, this, fPref);
		} catch (Exception ex) {
			frontend.error(ex, "Could not perform internal feed initialization");
		}
	}
	private void rmFeed(String dFeed) {
		try {
			Preferences kNode = prefs.node(dFeed);
			kNode.removeNode();
		} catch(BackingStoreException ex) {
			frontend.error("Could not remove feed "+dFeed);
		}
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
			frontend.error(ex, "Could not access preferences");
		}
		
		rfeeds = Collections.unmodifiableSet(feeds);
		for(Feed feed : rfeeds) {
			(new Thread(feed)).start();
		}
        frontend.updateFeedset(rfeeds);
	}
	
	private void loadFeedsFromPrefs() {
		feeds = new HashSet<Feed>();
		try {
			for(String feedName : prefs.childrenNames()) {
				try {
					feeds.add(makeFeedFromString(feedName));
                    frontend.log("Added feed "+feedName);
                } catch (ClassNotFoundException ex) {
                    frontend.error(ex, "No such feed "+feedName);
                    prefs.node(feedName).removeNode();
				} catch (Exception ex) {
                    frontend.error(ex, "Couldn't load feed "+feedName);
				}
			}
		} catch (BackingStoreException bse) {
			frontend.error("Could not load preferences.");
		}
	}

	public void getFeedEvent(FeedEvent fe) {
		frontend.getFeedEvent(fe);
	}
	
	public String encrypt(String in) {
		return in;
	}
	
	private void printPrefs() {
		printPrefs("", prefs);
	}
	private void printPrefs(String sofar, Preferences cnode) {
		try {
			System.out.println(sofar+"/"+cnode.name());
			for(String k : cnode.keys()) {
				System.out.println(" - "+k+" : "+cnode.get(k, "-fuck-"));
			}
			for(String c : cnode.childrenNames()) {
				Preferences p = cnode.node(sofar+"/"+cnode.name()+"/"+c);
				printPrefs(sofar+"/"+cnode.name(), p);
			}
		} catch(Exception ex) {
			System.out.println("Damn");
		}
	}
}