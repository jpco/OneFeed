package old;

import java.util.Scanner;


public class OneFeedCliController extends OneFeedController {
	
	public OneFeedCliController(OneFeed m) {
		super(m);
	}
	public void init() {
		Scanner in = new Scanner(System.in);
		String line = "";
		while(!(line = in.nextLine()).equals("q")) {
			m.addFeed(line);
		}
		in.close();
	}
}
