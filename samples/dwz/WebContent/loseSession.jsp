<%@page contentType="text/html; charset=utf-8"%>
<%@page session="false"%>
<%@page import="com.beetle.dwzdemo.valueobject.*"%>
<%@page import="com.beetle.dwzdemo.web.common.*"%>
<%@page import="com.beetle.framework.web.view.ViewHelper"%>
<%@page import="com.beetle.framework.util.*"%>
<%
WsStatus ws=new WsStatus();
ws.setStatusCode(301);
ws.setMessage("会话不存在，请重新登录");
out.println(ObjectUtil.objectToJsonWithJackson(ws)); 
%>