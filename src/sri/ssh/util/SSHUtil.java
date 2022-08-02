package sri.ssh.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import sri.fb.util.GetParams;

import com.sshtools.j2ssh.ScpClient;
import com.sshtools.j2ssh.SftpClient;
import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.SshException;
import com.sshtools.j2ssh.sftp.FileAttributes;
import com.sshtools.j2ssh.sftp.SftpFile;
import com.sshtools.j2ssh.sftp.SftpFileInputStream;
import com.sshtools.j2ssh.sftp.SftpSubsystemClient;
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState;
import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient;
import com.sshtools.j2ssh.authentication.PublicKeyAuthenticationClient;
import com.sshtools.j2ssh.session.SessionChannelClient;
import com.sshtools.j2ssh.transport.IgnoreHostKeyVerification;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKey;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKeyFile;

public class SSHUtil {

    private SshClient ssh = null;
    private SftpSubsystemClient  sftp = null;
    private ScpClient scp = null;
    
    public SSHUtil(String strHost, int nPort, String strUser, String strPasswd) {
    	
    	int authStatus = AuthenticationProtocolState.READY;
    	SshClient sshClient = new SshClient();
    	try {
			sshClient.connect(strHost, nPort, new IgnoreHostKeyVerification());
			if(sshClient.isConnected()) {
				System.out.println("Connection Successful --> "+strHost);
				PasswordAuthenticationClient passAuthCient = new PasswordAuthenticationClient();
				passAuthCient.setUsername(strUser);
				passAuthCient.setPassword(strPasswd);
				authStatus = sshClient.authenticate(passAuthCient);
				if (authStatus != AuthenticationProtocolState.COMPLETE) {
					sshClient.disconnect();
					sshClient = null;
				}
				System.out.println("Authentication Successful --> "+strHost);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if (sshClient != null )
				sshClient.disconnect();
			e.printStackTrace();
		}
    	ssh = sshClient;
    }
    
    public SSHUtil(String strHost, String strUser, String strPasswd) {
		this(strHost,22,strUser,strPasswd);
    }
    
    public SSHUtil(String strHost, String strUser, String strPasswd, File strKeyFile) {
		this(strHost,22,strUser,strPasswd,strKeyFile);
    }
    
    public SSHUtil(String strHost, int nPort, String strUser, String strPasswd, File strKeyFile) {

    	int authStatus = AuthenticationProtocolState.READY;
    	SshClient sshClient = new SshClient();
    	try {
			sshClient.connect(strHost, nPort, new IgnoreHostKeyVerification());
			if(sshClient.isConnected()) {
				System.out.println(GetParams.getDTTM()+" - Connection Successful --> "+strHost);
				PublicKeyAuthenticationClient keyAuthClient = new PublicKeyAuthenticationClient();
				SshPrivateKeyFile sshPvtKeyFile = SshPrivateKeyFile.parse(strKeyFile);
				SshPrivateKey sshPvtKey = sshPvtKeyFile.toPrivateKey((strPasswd == null || strPasswd.length() == 0 )?"":strPasswd); // passphrase
				keyAuthClient.setUsername(strUser);
				keyAuthClient.setKey(sshPvtKey);
				authStatus = sshClient.authenticate(keyAuthClient);
				if (authStatus != AuthenticationProtocolState.COMPLETE) {
					sshClient.disconnect();
					sshClient = null;
				}
				System.out.println(GetParams.getDTTM()+" - Authentication Successful --> "+strHost);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if (sshClient != null )
				sshClient.disconnect();
			e.printStackTrace();
		}
    	ssh = sshClient;
    }
    
    public boolean isConnected() {
    	return (ssh.isConnected() && ssh.isAuthenticated());
    }
    
    public void disconnect() {
    	if(sftp != null) {
    		try {
    			System.out.println(GetParams.getDTTM()+" - Closing sftp client --> "+ssh.getConnectionProperties().getHost());
				sftp.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	System.out.println(GetParams.getDTTM()+" - Closing Connection --> "+ssh.getConnectionProperties().getHost());
    	if(ssh != null) {
    		ssh.disconnect();
    	}
    	
    }
    
    public List<String> getFileList(String strCDIR) {
    	List<String> flist = new Vector<String>();
    	try {
			SftpClient sftpc = ssh.openSftpClient();
			//SftpSubsystemClient  sftp = ssh.openSftpChannel();
			List<SftpFile> slist = sftpc.ls(strCDIR);
			Iterator<SftpFile> it = slist.iterator();
			while(it.hasNext()) {
				SftpFile sfile = it.next();
				flist.add(sfile.getLongname());
				//System.out.println(it.next());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flist;
    }
    
    public ArrayList<String> getList(String strCmd) {
    	ArrayList<String> fileList = new ArrayList<String>();
    	try {
			SessionChannelClient session = ssh.openSessionChannel();
			//String strCmd = "cd "+strCDIR+" && ls -lat | grep -v total | awk '{print $1\"#\"$3\"#\"$4\"#\"$5\"#\"$6\"#\"$7\"#\"$8\"#\"$9}'";
			session.executeCommand(strCmd);
			InputStream in = session.getInputStream();
			//Reader reader = new InputStreamReader(in);
			/*int data = reader.read();
			while(data != -1){
			    char theChar = (char) data;
			    strOutput.append(theChar);
			    data = reader.read();
			}*/
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			while((line = br.readLine()) != null) {
				//System.out.println(line);
				fileList.add(line);
			}
			System.out.println("Command Exit Code : "+session.getExitCode());
			in.close(); 
			session.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileList;  	
    }
    
    public long getFileAttributes(String strFile) throws IOException, NullPointerException {
    	try {
			FileAttributes attrs = null;
			if (sftp == null)
				sftp = ssh.openSftpChannel();
			attrs = sftp.getAttributes(strFile);
			return attrs.getSize().longValue();
		} catch (SshException sex) {
			System.out.println(sex.getMessage());
			return getSCPFileSize(strFile);
		}
    }
    
    private long getSCPFileSize(String strFile) throws IOException {
		if(scp == null)
			scp = ssh.openScpClient();
		return scp.getFileSize(strFile);
	}

	public InputStream getFileInputStream(String strFile) throws IOException {
    	try {
    		SftpFileInputStream in = null;
    		if(sftp == null) 
    			sftp = ssh.openSftpChannel();
    		in = new SftpFileInputStream(sftp.openFile(strFile, SftpSubsystemClient.OPEN_READ));
    		return in;
    	} catch(SshException sex) {
    		System.out.println(sex.getMessage());
    		return getSCPFileInputStream(strFile);
    	}   	
    }

	private InputStream getSCPFileInputStream(String strFile) throws IOException {
		if(scp == null)
			scp = ssh.openScpClient();
		return scp.get(strFile);
	}
    
}
