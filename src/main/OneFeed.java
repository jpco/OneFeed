package main;

import java.util.ArrayList;

import util.FeedEvent;
import util.FeedListener;

/**
 * The heart of the OneFeed program; the Model in the MVC,
 * the blackboard in the board, the dispatcher of Feeds...
 * might be a bit of a God object, maybe should break up? Nah
 * 
 * @author jpco
 */
public class OneFeed implements FeedListener {
	
	private ArrayList<OneFeedView> views;
	
	public static void main(String[] args) {
		new OneFeed().init();
	}
	
	// The big initialization method!
	private void init() {
		views = new ArrayList<OneFeedView>();
		
		// read from prefs.json
		
		// initialize prefs.json OneFeedView type
		
		// initialize prefs.json Feeds
	}

	/**
	 * Methods which catch feed events. When implementing, REMEMBER SYNCHRONIZATION!!!!!!!!
	 */
	@Override
	public void update(FeedEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
	public void except(FeedEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
	public void log(FeedEvent e) {
		// TODO Auto-generated method stub
	}
}