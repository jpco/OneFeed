
abstract public class OneFeedFrontend {
	OneFeed mgr;

	public OneFeedFrontend(OneFeed m) {
		mgr = m;
	}
	
	abstract public void reportExc(Exception ex, String msg);
	abstract public void log(String msg);
}