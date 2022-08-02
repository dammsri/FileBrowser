<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" isErrorPage="true" import="java.io.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Error Page</title>
</head>
<body>

<%         
     response.setContentType("text/html");
      
      out.write("<h3>Exception Details</h3>");
      out.write("<ul><li>Exception Message: <b>"+request.getAttribute("errMessage")+"</b></li>");
      out.write("</ul>");
           
      out.write("<br><br>");
      out.write("<a href=\"http://localhost:8080/FileBrowser/index.jsp\">Home Page</a>");
%>
</body>
</html>