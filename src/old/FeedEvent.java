package old;

import java.util.EventObject;

public class FeedEvent extends EventObject {
	static final long serialVersionUID = 0;
	private String str;

	public FeedEvent(Feed src) {
		super(src);
	}
	public FeedEvent(Feed src, String str) {
		super(src);
		this.str = str;
	}

	public String getString() {
		return str;
	}

	public Feed getSource() {
		return (Feed)super.getSource();
	}
}
