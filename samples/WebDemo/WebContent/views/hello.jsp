<%@page contentType="text/html; charset=gb2312"%>
<%@page session="false"%>
<%@page import="com.beetle.framework.web.view.ViewHelper"%>
<html>
<head> 
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<link href="t-1.css" rel="stylesheet" type="text/css">
</head>
<body>
<%
ViewHelper helper=new ViewHelper(request);
String name=helper.getDataFromRequest("name");
%>
<div align="center">
Hello,<%=name %>!</div>
<p align="center"><a href="./index.html">返回</a></p>
</body>
</html>
