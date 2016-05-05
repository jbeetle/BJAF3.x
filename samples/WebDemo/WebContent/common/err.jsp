<%@ page contentType="text/html; charset=GBK" %>
<%@ page isErrorPage="true" %>
<html>
<link href="../t-1.css" rel="stylesheet" type="text/css">
<body bgcolor="#ffffff">

<h1>错误信息显示页面</h1>
<br>Error Message is: <%= exception.getMessage() %><br>
Stack Trace is : <pre><font color="red"><% 
 java.io.CharArrayWriter cw = new java.io.CharArrayWriter(); 
 java.io.PrintWriter pw = new java.io.PrintWriter(cw,true); 
 exception.printStackTrace(pw); 
 out.println(cw.toString()); 
 %></font></pre>
<br></body>
</html>