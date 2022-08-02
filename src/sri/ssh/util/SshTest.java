package sri.ssh.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SshTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String host = "192.168.56.11";
		String user = "dsrirangam";
		String passwd = "Hello#1234";
		
		String cdir="/home/dsrirangam/.ssh/";
		
		/*System.out.println(cdir.lastIndexOf("/")+"-"+cdir.indexOf("/", 1));
		String[] pdirs = cdir.split("/");
		System.out.println(pdirs.length);
		String urhere = "<a href='http://localhost:8080/FileBrowser/browsefilesystem.jsp?host="+host+"&cdir=/'>/</a>";
		for(int i=1;i<pdirs.length;i++){
			System.out.println(pdirs[i]);
			String cl="";
			for(int j=1;j<=i;j++) {
				cl += "/"+pdirs[j];
			}
			if(i == pdirs.length - 1) {
				urhere += pdirs[i];
			} else {
				urhere += "<a href='http://localhost:8080/FileBrowser/browsefilesystem.jsp?host="+host+"&cdir="+cl+"/'>"+pdirs[i]+"/</a>";
			}
		}
		//System.out.println(urhere);
		System.out.println(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime()));
		System.out.println((int)(((0.0+5)/20)*100));*/

		//SSHUtil ssh = new SSHUtil(host, user, passwd);
		SSHUtil ssh = new SSHUtil(host, user, null, new File("C:/Users/dsrirangam/Documents/tmp/id_rsa"));
		if(ssh.isConnected()) {
			/*List farr = ssh.getFileList("/home/wasadm");
			for(Object sfile: farr) {
				System.out.println(sfile.toString());
			}
			System.out.println("size: "+ssh.getFileAttributes("/home/wasadm/test.sh"));*/
			ArrayList<String> arr = ssh.getList("/home/");
			System.out.println("length: "+arr.size());
			for(String item: arr) {
				
				System.out.println(item);
			}
			/*BufferedReader br = new BufferedReader(new InputStreamReader(ssh.getFileInputStream("/home/wasadm/test.sh")));
			String line;
			try {
				while((line = br.readLine()) != null) {
					System.out.println(line);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		ssh.disconnect();
	}

}
