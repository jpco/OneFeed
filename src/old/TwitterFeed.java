package old;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.prefs.Preferences;

import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

public class TwitterFeed extends Feed implements Runnable {
	
	public TwitterFeed(OneFeed mgr, Preferences pref) {
		super(mgr, pref);
		
		apikey = "y62sFjps53KzruuOs3oPxjq8d";
		apisecret = "H2yyNcDSDnEddnBg7q0XLGcfQhNEAvjD3OYJDQewwUDz4da3Ti";
		feedUrl = "https://userstream.twitter.com/1.1/user.json";
		oAuthApi = TwitterApi.class;
		serviceMainUrl = "http://www.twitter.com";
		callbackPort = 13245;
	}
	
	public void run() {
		if(!userAuth()) {
			mgr.view.error("Could not log in to "+getName()+"; aborting feed");
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
				if(line.equals("")) continue;
				sendFeedEvent(line);
			}
			in.close();
		} catch(IOException ex) {
			mgr.view.error("Error while reading "+getName()+" feed");
		}
	}

	public String getName() {
		return "Twitter";
	}
	public String getSName() {
		return "Tw";
	}
}

