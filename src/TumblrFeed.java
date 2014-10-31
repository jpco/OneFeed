import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.prefs.Preferences;

import org.scribe.builder.api.TumblrApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

public class TumblrFeed extends Feed implements Runnable {
	
	public TumblrFeed(OneFeed mgr, Preferences pref) {
		super(mgr, pref);
		
		apikey = "vSGPcp43S286hQMhIekwgaUYcAcsoxlwTIaXpD6mpIH2TeGV8M";
		apisecret = "ea4TW4OHMGXvaCBmmDF9okhcS0MDUzizSz8AguucxV9Smlt4bm";
		feedUrl = "https://api.tumblr.com/v2/user/dashboard";
		oAuthApi = TumblrApi.class;
		serviceMainUrl = "http://www.tumblr.com";
		callbackPort = 32042;
	}
	
	public void run() {
		if(!userAuth()) {
			mgr.frontend.error("Could not log in to "+getName()+"; aborting feed");
			return;
		}
		
		OAuthRequest streamReq = new OAuthRequest(Verb.GET, feedUrl);
		streamReq.setConnectionKeepAlive(true);
		streamReq.addHeader("user-agent", "OneFeed");
		oas.signRequest(accessToken, streamReq);
		Response streamResponse = streamReq.send();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(streamResponse.getStream()));
		
		try {
			String line = "";
			while(run && (line = in.readLine()) != null) {
				sendFeedEvent(line);
			}
			in.close();
		} catch(IOException ex) {
			mgr.frontend.error("Error while reading "+getName()+" feed");
		}
	}

	public String getName() {
		return "Tumblr";
	}
	public String getSName() {
		return "Tu";
	}
}

