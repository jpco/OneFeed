import java.awt.Desktop;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.prefs.Preferences;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

abstract public class Feed implements Runnable {
	protected OneFeed mgr;
	protected Preferences prefs;
	
	protected String apikey = "";
	protected String apisecret = "";
	protected String feedUrl;
	protected String serviceMainUrl;
	protected Class<? extends Api> oAuthApi;
	
	protected OAuthService oas;
	protected Token accessToken;
	
	protected boolean run = true;
	protected int callbackPort = -1;

	public Feed(OneFeed mgr, Preferences pref) {
		this.mgr = mgr;
		prefs = pref;
	}

	final public void sendFeedEvent(String evts) {
		mgr.getFeedEvent(new FeedEvent(this, evts));
	}

	public void kill() {
		run = false;
	}

	abstract public String getName();
	abstract public String getSName();
	
	protected boolean userAuth() {

		oas = new ServiceBuilder()
					.provider(oAuthApi)
					.apiKey(apikey)
					.apiSecret(apisecret)
					.callback("http://localhost:"+callbackPort+"/")
					.build();
		
		// Get string from prefs
		String tk = prefs.get("token", "");
		String tksec = prefs.get("secret", "");
		
		if(tk.equals("")) {
			Token reqToken;
			try {
				reqToken = oas.getRequestToken();
			} catch (Exception ex) {
				ex.printStackTrace();
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
				ServerSocket sSocket = new ServerSocket(callbackPort);
				Socket s = sSocket.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				String line = "";
				while((line = in.readLine()) != null && !line.equals("")) {
					if(req == null) req = line;
				}
				
				BufferedWriter out = new BufferedWriter(
						new OutputStreamWriter(
								new BufferedOutputStream(s.getOutputStream())));
				
				String body = "<html><head><script>window.open('"+serviceMainUrl+"','_self');</script></head></html>";
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
				mgr.frontend.error("Could not save "+getName()+" account to preferences");
			}
		} else {
			accessToken = new Token(tk, tksec);
		}
		
		mgr.frontend.log("Logged in to "+getName());
		return true;
	}
	
	public void init() {
		String[] prompts = {"Login", "Password"};
		String[] loginInfo = mgr.frontend.prompt(prompts);
		while(loginInfo.length != 2) {
			loginInfo = mgr.frontend.prompt(prompts);
		}
		prefs.put("login", loginInfo[0]);
		prefs.put("pw", mgr.encrypt(loginInfo[1]));
	}
}

