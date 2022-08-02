package sri.fb.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import sri.ssh.util.SSHUtil;

public class GetParams {

	private Properties prop = null;

	private String PDIR = "/";
	private String CDIR = "/";
	
	private String strUser;
	private String strPasswd;
	private String strPubKey;
	private String authType;
	
	private int nPort = 22;
	
	private HashMap<String,SSHUtil> conList = new HashMap<String,SSHUtil>();
	
	public GetParams(String strFile) {
		prop = new Properties();
		try {
			if(strFile != null) {
				prop.load(new FileInputStream(strFile));
			} else {
				prop.load(this.getClass().getClassLoader().getResourceAsStream("HostParameters.properties"));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getPDIR() {
		return PDIR;
	}

	public void setPDIR(String pDIR) {
		PDIR = pDIR;
	}

	public String getCDIR() {
		return CDIR;
	}

	public void setCDIR(String cDIR) {
		PDIR = getCDIR();
		CDIR = cDIR;
	}

	public void setHostParams(String strHost) {
		try {
			String[] hparams = prop.getProperty(strHost).split(",");
			authType = hparams[0];
			if(authType.equalsIgnoreCase("PASSWD")) {
				nPort = Integer.parseInt(hparams[1]);
				strUser = hparams[2];
				strPasswd = hparams[3];
			} else {
				nPort = Integer.parseInt(hparams[1]);
				strUser = hparams[2];
				strPasswd = hparams[3];
				strPubKey = hparams[4];
			} 
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}
	public SSHUtil getConnection(String strHost) {
		SSHUtil ssh = null;
		if(conList.containsKey(strHost.toLowerCase())){
			ssh = conList.get(strHost.toLowerCase());
		} else {
			setHostParams(strHost);
			ssh = (authType.equalsIgnoreCase("PASSWD")) ? new SSHUtil(strHost,nPort, strUser, strPasswd) : 
														  new SSHUtil(strHost, nPort, strUser, strPasswd, new File(strPubKey));
			conList.put(strHost.toLowerCase(), ssh);
		}
		return ssh;
	}
	
	public void closeConnection() {
		Iterator<String> it = conList.keySet().iterator();
		while(it.hasNext()) {
			conList.get(it.next()).disconnect();
		}
	}
	
	public static String getDTTM(long tm) {
		Calendar cl = Calendar.getInstance();
		cl.setTimeInMillis(tm);
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		//SimpleDateFormat df1 = new SimpleDateFormat("EEEE, MMMM d, yyyy");
		String dttm = df.format(cl.getTime());
		return dttm;
	}
	public static String getDTTM() {
		Calendar cl = Calendar.getInstance();
		//cl.setTimeInMillis(tm);
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		//SimpleDateFormat df1 = new SimpleDateFormat("EEEE, MMMM d, yyyy");
		String dttm = df.format(cl.getTime());
		return dttm;
	}
}
