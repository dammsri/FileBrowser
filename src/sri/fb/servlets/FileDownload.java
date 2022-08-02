package sri.fb.servlets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import sri.fb.util.GetParams;
import sri.ssh.util.SSHUtil;

/**
 * Servlet implementation class FileDownload
 */
public class FileDownload extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int BUFSIZE = 65536;
	//private static final int BUFSIZE = 524288;

    private String filePath;   
    private String host;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FileDownload() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		HttpSession session = request.getSession();
		try {
			boolean isDownloading = (Boolean) session.getAttribute("isDownloading");
			if (isDownloading) {
				throw new UnsupportedOperationException("Download is in progress, please try later");
			}
		} catch (NullPointerException nex) {
			session.setAttribute("isDownloading", Boolean.FALSE);
		}
		
		host = request.getParameter("host");
		filePath = request.getParameter("file");
		System.out.println(filePath);
		GetParams cparam = (GetParams) session.getAttribute("cparam");
		SSHUtil ssh;
		try {
			ssh = cparam.getConnection(host);
		} catch (NullPointerException nex) {
			throw new NullPointerException("Not Connected to the Host : "+host);
		}
		//System.out.println(ssh);
        int fileSize = 0;
        fileSize = (int) ssh.getFileAttributes(filePath);
        System.out.println("fs="+fileSize);
        InputStream in = ssh.getFileInputStream(filePath);

        ServletOutputStream outStream = response.getOutputStream();
        ServletContext context  = getServletConfig().getServletContext();
        String mimetype = context.getMimeType(filePath);
        if (mimetype == null) {
            mimetype = "application/octet-stream";
        }
        response.setContentType(mimetype);
        response.setContentLength(fileSize);
        String fileName = (new File(filePath)).getName();
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        int length   = 0;
        int byteSoFar = 0;
        byte[] byteBuffer = new byte[BUFSIZE];

        int curTimeout =  session.getMaxInactiveInterval();
        int newTimeout = 0;
        if(fileSize > 4194304 ) {
        	newTimeout = curTimeout + (((fileSize/1024)/1024)/2)*60;
        } else {
        	newTimeout = curTimeout + (3*60);
        }
        session.setMaxInactiveInterval(newTimeout);
        System.out.println(GetParams.getDTTM()+" - session timeout --> "+session.getMaxInactiveInterval());
        System.out.println(GetParams.getDTTM()+" - "+fileName+" download started ...");
        session.setAttribute("isDownloading", Boolean.TRUE);
        try {
	        while ((in != null) && ((length = in.read(byteBuffer)) != -1))
	        {
	            outStream.write(byteBuffer,0,length);
	            /*byteSoFar += length;
	            System.out.println(GetParams.getDTTM()+" - Transferred [ "+(byteSoFar/1024) +
	            		" KB ] of [ "+(fileSize/1024)+" KB ] - "+((int)(((0.0+byteSoFar)/fileSize)*100))+"%");*/
	        }
        } catch (Exception ex) {
        	System.out.println(GetParams.getDTTM()+" - Error while reading file: "+fileName);
        	System.out.println(GetParams.getDTTM()+" - Download Interrupted");
        	ex.printStackTrace();
        } finally {
        	 if(in != null)
 	        	in.close();
 	        outStream.close();
            session.setAttribute("isDownloading", Boolean.FALSE);
            session.setMaxInactiveInterval(curTimeout);
        }
        //ssh.disconnect();
        System.out.println(GetParams.getDTTM()+" - "+fileName+" download completed ...");
        System.out.println(GetParams.getDTTM()+" - session timeout --> "+session.getMaxInactiveInterval());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
		// TODO Auto-generated method stub
	}

}
