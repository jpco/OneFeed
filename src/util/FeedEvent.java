package util;

import java.util.Date;

import feed.Feed;

// TODO: Is Date the best class to use here? I don't think so
// Gotta make sure whatever I use is immutable/I work around mutability
public class FeedEvent {
	private Feed src;
	private Date time;
	private JsonTree msg;
	
	public FeedEvent(Feed s, Date t, JsonTree m) {
		src = s;
		time = t;
		msg = m;
	}
	
	public FeedEvent(Feed s, Date t, String key, String val) {
		src = s;
		time = t;
		msg = JsonTree.parse("{"+key+":\""+val+"\"}");
	}
	
	public String getSrc() {
		return src.toString();
	}
	public Date getTime() {
		return time;
	}
	public JsonTree getTree() {
		return msg;
	}
}
