<%@page contentType="text/html; charset=gb2312"%>
<%@page session="false"%>
<%@page import="com.beetle.framework.web.view.ViewHelper"%>
<%@page import="java.util.*"%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<link href="t-1.css" rel="stylesheet" type="text/css">
</head>
<body>
<%
ViewHelper helper=new ViewHelper(request);
Date now=(Date)helper.getDataValue("NOW");
%>
<div align="center">
<%=now.toString() %> -&gt;<a href="#" onClick="history.go()">刷新</a></div>
<p align="center"><a href="./index.html">返回</a></p>
</body>
</html>
