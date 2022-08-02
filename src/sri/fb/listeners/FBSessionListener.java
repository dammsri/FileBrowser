package sri.fb.listeners;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import sri.fb.util.GetParams;

/**
 * Application Lifecycle Listener implementation class FBSessionListener
 *
 */
public class FBSessionListener implements HttpSessionListener {

    /**
     * Default constructor. 
     */
    public FBSessionListener() {
    	super();
    	//System.out.println(1);
        // TODO Auto-generated constructor stub
    }

	/**
     * @see HttpSessionListener#sessionCreated(HttpSessionEvent)
     */
    public void sessionCreated(HttpSessionEvent arg0) {
        // TODO Auto-generated method stub
    	System.out.println(GetParams.getDTTM()+" - session created : "+arg0.getSession().getMaxInactiveInterval());
    }

	/**
     * @see HttpSessionListener#sessionDestroyed(HttpSessionEvent)
     */
    public void sessionDestroyed(HttpSessionEvent arg0) {
        // TODO Auto-generated method stub
    	System.out.println(GetParams.getDTTM()+" - session destroy : "+arg0.getSession().getMaxInactiveInterval());
    	((GetParams)arg0.getSession().getAttribute("cparam")).closeConnection();
    }
	
}
