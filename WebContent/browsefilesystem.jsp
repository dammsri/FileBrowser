<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import='sri.ssh.util.SSHUtil' %>
<%@ page import='java.util.ArrayList' %>
<%@ page import='sri.fb.util.GetParams' %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>File Browser</title>
<link rel='stylesheet' type='text/css' href='styles.css'>
</head>
<body>

<table id='wborder'>
<%
	String host = request.getParameter("host");
	String cdir = request.getParameter("cdir");
	GetParams params = null;
	SSHUtil ssh = null;
	//String prfile = "C:\\Users\\Rikhil Pardhiv\\Documents\\workspace\\FileBrowser\\src\\HostParameters.properties";
	if(session.getAttribute("cparam") == null ) {
		params = new GetParams(null);
		session.setAttribute("cparam", params);
	} else {
		params = (GetParams)session.getAttribute("cparam");
	}
	
	int cols = 0;
	boolean blnFlag;
	if(!cdir.equalsIgnoreCase("DF")) {
		blnFlag = true;
		params.setCDIR(cdir);
		cols = 8;
	} else {
		blnFlag = false;
		//cdir = params.getCDIR();
		cdir = "/";
		cols = 6;
	}
	//String pdir=params.getPDIR();
	ssh = params.getConnection(host);
	String[] pdirs = cdir.split("/");
	String urhere = "<a href='http://localhost:8080/FileBrowser/browsefilesystem.jsp?host="+host+"&cdir=/'>/</a>";
	for(int i=1;i<pdirs.length;i++){
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
	out.write("<tr><th colspan="+cols+"><a href='http://localhost:8080/FileBrowser/index.jsp'>Home</a>&nbsp|&nbsp Server: "+host+" &nbsp|&nbsp Path : &nbsp"+urhere+"</th></tr>");
	if(ssh.isConnected()) {
		String strCmd = "";
		ArrayList<String> fList = null;
		if(blnFlag) {
			strCmd = "cd "+cdir+" && ls -lat | grep -v total | awk '{print $1\"#\"$3\"#\"$4\"#\"$5\"#\"$6\"#\"$7\"#\"$8\"#\"$9}'";
			fList = ssh.getList(strCmd);
			if(fList.size() == 0 ){
				out.write("<tr><td colspan="+cols+" align='center'> Permission Denied !!</td></tr>");
			}
			for(String file: fList) {
				String[] fattr = file.split("#");
				out.write("<tr>");
				for(int i=0;i<fattr.length;i++) {
					if(fattr[fattr.length -1].equals(".") || fattr[fattr.length -1].equals(".."))
						continue;
					if(i == fattr.length - 1) {
						if(fattr[0].charAt(0) == 'd' || fattr[0].charAt(0) == 'l' ) {
							out.write("<td><a href='http://localhost:8080/FileBrowser/browsefilesystem.jsp?host="+host+"&cdir="+cdir+fattr[i]+"/'>"+fattr[i]+"</a></td>");
						} else {
							out.write("<td><a href='http://localhost:8080/FileBrowser/FileDownload?host="+host+"&file="+cdir+fattr[i]+"'>"+fattr[i]+"</a></td>");
						}
					} else {
						if(i==3 || i==5 ||i ==6){
							out.write("<td align='right'>"+fattr[i]+"</td>");
						} else {
							out.write("<td>"+fattr[i]+"</td>");
						}
					}
				}
				out.write("</tr>");
			}
		} else {
			strCmd = "[[ `uname` == \"Linux\" ]] && df -hP|awk '{print $1\"#\"$2\"#\"$3\"#\"$4\"#\"$5\"#\"$6}' || df -gP|awk '{print $1\"#\"$2\"#\"$3\"#\"$4\"#\"$5\"#\"$6}';";
			fList = ssh.getList(strCmd);
			if(fList.size() == 0 ){
				out.write("<tr><td colspan="+cols+" align='center'> Permission Denied !!</td></tr>");
			}
			fList.remove(0);
			out.write("<tr><td><b>Filesystem</b></td><td><b>Size</b></td><td><b>Used</b></td><td><b>Avail</b></td><td><b>Use%</b></td><td><b>Mounted On</b></td></tr>");
			for(String file: fList) {
				String[] fattr = file.split("#");
				out.write("<tr>");
				for(int i=0;i<fattr.length;i++) {
					if(i == fattr.length - 1) {
							out.write("<td><a href='http://localhost:8080/FileBrowser/browsefilesystem.jsp?host="+host+"&cdir="+(fattr[i].equals("/")?"/":fattr[i]+"/")+"'>"+fattr[i]+"</a></td>");
					} else {
						if(i==1 || i==2 ||i ==3 || i==4){
							out.write("<td align='right'>"+fattr[i]+"</td>");
						} else {
							out.write("<td>"+fattr[i]+"</td>");
						}
					}
				}
				out.write("</tr>");
			}
		} 
	} else {
		out.write("<tr><td colspan="+cols+" align='center'>ERROR: Unable to connect to "+host+"</td></tr>");
	}
	//ssh.disconnect();

%>
</table>
</body>
</html>