import java.awt.Desktop;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TumblrApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TumblrFeed extends Feed implements Runnable {
	
	private String apikey = "vSGPcp43S286hQMhIekwgaUYcAcsoxlwTIaXpD6mpIH2TeGV8M";
	private String apisecret = "ea4TW4OHMGXvaCBmmDF9okhcS0MDUzizSz8AguucxV9Smlt4bm";
	
	private OAuthService oas;
	private Token accessToken;
	
	Gson gson = new Gson();
	Type strObjMap = new TypeToken<Map<String, Object>>(){}.getType();
	private boolean run = true;

	public TumblrFeed(OneFeed mgr, Preferences pref) {
		super(mgr, pref);
	}
	
	static public void add(OneFeed m, Preferences p) {
		String[] prompts = {"Login", "Password"};
		String[] loginInfo = m.frontend.prompt(prompts);
		while(loginInfo.length != 2) {
			loginInfo = m.frontend.prompt(prompts);
		}
		p.put("login", loginInfo[0]);
		p.put("pw", m.encrypt(loginInfo[1]));
	}
	
	public void run() {
		if(!userAuth()) {
			mgr.frontend.error("Could not log in to "+getName()+"; feed aborting");
			return;
		}
		
		OAuthRequest streamReq = new OAuthRequest(Verb.GET, "https://api.tumblr.com/v2/user/dashboard");
		streamReq.setConnectionKeepAlive(true);
		streamReq.addHeader("user-agent", "OneFeed-TumblrFeed");
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
			mgr.frontend.error("Error while reading "+getName()+" feed");
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
					.provider(TumblrApi.class)
					.apiKey(apikey)
					.apiSecret(apisecret)
					.callback("http://localhost:32042/")
					.build();
		
		// Get string from prefs
		String tk = prefs.get("token", "");
		String tksec = prefs.get("secret", "");
		
		if(tk.equals("")) {
			Token reqToken;
			try {
				reqToken = oas.getRequestToken();
			} catch(Exception ex) {
				mgr.frontend.error("Could not log in to "+getName()+"; probably caused by bad OAuth request");
				return false;
			}
			String authUrl = oas.getAuthorizationUrl(reqToken);
			try {
				Desktop.getDesktop().browse(new URL(authUrl).toURI());
			} catch(Exception ex) {
				mgr.frontend.error(ex, "could not open browser to connect");
				return false;
			}
			String req = null;
			try {
				ServerSocket sSocket = new ServerSocket(32042);
				Socket s = sSocket.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				String line = "";
				while((line = in.readLine()) != null && !line.equals("")) {
					if(req == null) req = line;
				}
				
				BufferedWriter out = new BufferedWriter(
						new OutputStreamWriter(
								new BufferedOutputStream(s.getOutputStream())));
				
				String body = "<html><head><script>window.open('http://tumblr.com/','_self');</script></head></html>";
				out.write("HTTP/1.1 200 OK\r\n" +
						"Content-Type: text/html\r\n" +
						"Content-Length: "+body.length()+"\r\n\r\n" + body);
				out.flush();
				
				in.close();
				out.close();
				s.close();
				sSocket.close();
			} catch(Exception ex) { }
			
			//convert req to Token
			if(req == null) return false;
			
			try {
				tk = req.substring(req.indexOf("oauth_token=")+12,req.indexOf("&",req.indexOf("oauth_token=")));
				tksec = req.substring(req.indexOf("oauth_verifier=")+15,req.indexOf(" ",req.indexOf("oauth_verifier=")));
			} catch(StringIndexOutOfBoundsException t) {
				mgr.frontend.error("Error while parsing OAuth HTTP request");
				return false;
			}
			
			accessToken = oas.getAccessToken(reqToken, new Verifier(tksec));
			
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
		
		OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.tumblr.com/v2/user/info");
		
		oas.signRequest(accessToken, request);
		Response response = request.send();
		if(response.getCode() != 200) {
			mgr.frontend.error("Got a "+response.getCode()+" response code...");
			return false;
		}
		
		gson = new Gson();
		strObjMap = new TypeToken<Map<String, Object>>(){}.getType();
		
		Map<String, Object> body = new HashMap<String, Object>();
		body = gson.fromJson(response.getBody(), strObjMap);
		
		mgr.frontend.log("Logged in to "+getName());
		
		return true;
	}

	public void kill() {
		run = false;
	}

	public String getName() {
		return "Tumblr";
	}
	public String getSName() {
		return "Tu";
	}
}

