package feed;

import java.util.ArrayList;
import java.util.Date;

import util.FeedEvent;
import util.FeedListener;
import util.JsonTree;

/**
 * Abstract class defining necessary behavior of any feed.
 * @author jpco
 */
public abstract class Feed implements Runnable {
	private ArrayList<FeedListener> listeners;
	
	public final void addListener(FeedListener newFL) {
		listeners.add(newFL);
	}
	
	protected void sendUpdate(Feed src, JsonTree update) {
		for(FeedListener listener : listeners) {
			listener.update(new FeedEvent(src, new Date(), update));
		}
	}
	protected void sendException(Feed src, String exception) {
		for(FeedListener listener : listeners) {
			listener.except(new FeedEvent(src, new Date(), "exception", exception));
		}
	}
	protected void sendLogMsg(Feed src, String msg) {
		for(FeedListener listener : listeners) {
			listener.log(new FeedEvent(src, new Date(), "message", msg));
		}
	}
	
	public String getLongName() {
		return "Feed";
	}
	public String getShortName() {
		return "Fd";
	}
	@Override
	public String toString() {
		return getLongName();
	}
}