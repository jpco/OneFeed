public class DummyFeed extends Feed {
	String dstring;

	public DummyFeed(OneFeed mgr) {
		super(mgr);
		dstring = "This is a dummy feed!";
	}
	public DummyFeed(OneFeed mgr, String dstr) {
		super(mgr);
		dstring = dstr;
	}

	public void run() {
		while(true) {
			sendFeedEvent(dstring);
			try {
				Thread.sleep(100);
			} catch (Exception ex) { }
		}
	}
}
