package old;

public abstract class OneFeedController {
	OneFeed m;
	public OneFeedController(OneFeed m) {
		this.m = m;
	}
	public abstract void init();
}
