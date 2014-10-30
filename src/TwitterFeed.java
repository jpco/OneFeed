import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TwitterFeed extends Feed implements Runnable {
	
	private String apikey = "y62sFjps53KzruuOs3oPxjq8d";
	private String apisecret = "H2yyNcDSDnEddnBg7q0XLGcfQhNEAvjD3OYJDQewwUDz4da3Ti";
	
	private OAuthService oas;
	private Token accessToken;
	
	Gson gson = new Gson();
	Type strObjMap = new TypeToken<Map<String, Object>>(){}.getType();
	private boolean run = true;

	public TwitterFeed(OneFeed mgr, Preferences pref) {
		super(mgr, pref);
	}
	
	static public void add(OneFeed m, Preferences p) {
		String[] prompts = {"Login: ", "Password: "};
		String[] loginInfo = m.frontend.prompt(prompts);
		while(loginInfo.length != 2) {
			loginInfo = m.frontend.prompt(prompts);
		}
		p.put("login", loginInfo[0]);
		p.put("pw", m.encrypt(loginInfo[1]));
	}
	
	public void run() {
		if(!userAuth()) {
			mgr.frontend.error("Could not log in to Twitter; aborting TwitterFeed");
		}
		
		OAuthRequest streamReq = new OAuthRequest(Verb.GET, "https://userstream.twitter.com/1.1/user.json");
		streamReq.setConnectionKeepAlive(true);
		streamReq.addHeader("user-agent", "OneFeed-TwitterFeed");
		oas.signRequest(accessToken, streamReq);
		Response streamResponse = streamReq.send();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(streamResponse.getStream()));
		
		try {
			String line = "";
			while(run && (line = in.readLine()) != null) {
				parseLine(line);
			}
			in.close();
		} catch(IOException ex) {
			mgr.frontend.error("Error while reading Twitter feed");
		}
	}
	
	private void parseLine(String lineString) {
		if(lineString.equals("")) return;
		Map<String, Object> line = new HashMap<String, Object>();
		line = gson.fromJson(lineString, strObjMap);
		
		for(String s : line.keySet()) {
			System.out.println(s+":");
			System.out.println(" - "+line.get(s));
			if(line.get(s) != null) System.out.println(" - "+line.get(s).getClass());
		}
		
		sendFeedEvent(lineString);
	}
	
	// connects the user to the feed
	private boolean userAuth() {

		oas = new ServiceBuilder()
					.provider(TwitterApi.class)
					.apiKey(apikey)
					.apiSecret(apisecret)
					.build();
		
		// Get string from prefs
		String tk = prefs.get("token", "");
		String tksec = prefs.get("secret", "");
		
		if(tk.equals("")) {
			Token reqToken = oas.getRequestToken();
			String authUrl = oas.getAuthorizationUrl(reqToken);
			try {
				Desktop.getDesktop().browse(new URL(authUrl).toURI());
			} catch(Exception ex) {
				mgr.frontend.error(ex, "could not connect to Twitter");
				return false;
			}
			String code = mgr.frontend.prompt(new String[]{"Code"})[0];
			Verifier v = new Verifier(code);
			accessToken = oas.getAccessToken(reqToken, v);
			prefs.put("token", accessToken.getToken());
			prefs.put("secret", accessToken.getSecret());
			try {
				prefs.flush();
			} catch(Exception ex) {
				mgr.frontend.error("Could not save Twitter account to preferences");
			}
		} else {
			accessToken = new Token(tk, tksec);
		}
		
		OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/account/verify_credentials.json");
		
		oas.signRequest(accessToken, request);
		Response response = request.send();
		if(response.getCode() != 200) return false;
		
		gson = new Gson();
		strObjMap = new TypeToken<Map<String, Object>>(){}.getType();
		
		Map<String, Object> body = new HashMap<String, Object>();
		body = gson.fromJson(response.getBody(), strObjMap);
		
		mgr.frontend.log("@"+body.get("screen_name")+" logged in to Twitter");
		
		return true;
	}

	public void kill() {
		run = false;
	}

	public String getName() {
		return "Twitter";
	}
	public String getSName() {
		return "Tw";
	}
}

