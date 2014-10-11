import java.util.prefs.Preferences;

public class DummyFeed extends Feed {
	private boolean run = true;

	public DummyFeed(OneFeed mgr, Preferences pref) {
		super(mgr, pref);
		prefs.put("msg", "This is a DummyFeed!");
	}

	public void run() {
		while(run) {
			sendFeedEvent(prefs.get("msg", "default message from dummy"));
			try {
				Thread.sleep(100);
			} catch (Exception ex) { }
		}
	}

	public void kill() {
		run = false;
	}
	
	public String getName() {
		return "Dummy";
	}
	public String getSName() {
		return "Dm";
	}
}
