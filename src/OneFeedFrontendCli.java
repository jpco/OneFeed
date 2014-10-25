import java.io.Console;
import java.util.Calendar;
import java.util.Set;

public class OneFeedFrontendCli extends OneFeedFrontend {
    public OneFeedFrontendCli(OneFeed m) {
        super(m);
    }

    public void init() { }
    public void destroy() { }

    public void error(Exception ex, String str) {
        System.err.println(getTime()+"error: "+str+"; passed exception:");
        System.err.println(ex);
    }
    public void error(String str) {
        System.err.println(getTime()+"error: "+str);
    }
    public void log(String str) {
        System.out.println(getTime()+"log: "+str);
    }
    public void updateFeedset(Set<Feed> feedset) {
        System.out.println(getTime()+"Feeds in set:");
        for(Feed f : feedset) {
            System.out.println(getTime()+"  "+f.getName());
        }
    }
    public void getFeedEvent(FeedEvent fe) {
        System.out.println(getTime()+"EVENT: "+fe.getSource().getSName()
                +": "+fe.getString());
    }
    
    public String[] prompt(String[] prompts) {
    	String[] outs = prompts;
    	Console c = System.console();
    	for(int i = 0; i<prompts.length; i++) {
    		outs[i] = c.readLine(prompts[i]);
    	}
    	return outs;
    }

    private String getTime() {
        Calendar cal = Calendar.getInstance();
        return "["+cal.get(Calendar.MONTH)+"/"+cal.get(Calendar.DAY_OF_MONTH)+
        "/"+cal.get(Calendar.YEAR)+" "+cal.get(Calendar.HOUR)+":"+
        cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND)+"."+
        cal.get(Calendar.MILLISECOND)+"]";
    }
}
