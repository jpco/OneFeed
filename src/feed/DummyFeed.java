package feed;

import feed.Feed;

public class DummyFeed extends Feed {

	@Override
	public void run() {
		while(true) {
			sendLogMsg(this, "Yeah!");
			try {
				Thread.sleep(500);
			} catch(InterruptedException ex) {
				sendException(this, "InterruptedException!");
			}
		}
	}
	
	@Override
	public String getLongName() {
		return "DummyFeed";
	}
	public String getShortName() {
		return "Df";
	}
}