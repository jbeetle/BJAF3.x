<%@page contentType="text/html; charset=GBK"%>
<%@page session="false"%>
<%@page import="com.beetle.framework.web.view.ViewHelper"%>
<html>
<link href="../views/t-1.css" rel="stylesheet" type="text/css">
<body bgcolor="#ffffff">
<h1>错误信息显示页面</h1>
<%
ViewHelper helper=new ViewHelper(request);
%>
<br>Error Code is: <%= helper.getErrCode() %><br>
<br>Error Message is: <%= helper.getErrMessage() %><br>
Stack Trace is : <pre><font color="red"><%=helper.getErrStackTraceInfo() %></font></pre>
<br>
<p align="center"><a href="../index.html">返回</a></p>
</body>
</html>