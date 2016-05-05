<%@page contentType="text/html; charset=utf-8"%>
<%@page session="false"%>
<%@page import="com.beetle.dwzdemo.valueobject.*"%>
<%@page import="com.beetle.dwzdemo.web.common.*"%>
<%@page import="com.beetle.framework.web.view.ViewHelper"%>
<%@page import="com.beetle.framework.util.*"%>
<HTML>
<HEAD>
<TITLE></TITLE>
<SCRIPT ID=clientEventHandlersJS LANGUAGE=javascript>
<!--

function window_onload() {
  <%
  ViewHelper helper=new ViewHelper(request);
  String msg=helper.getDataValue(Const.WEB_RETURN_MSG).toString();
  String url=helper.getDataValue(Const.WEB_FORWARD_URL).toString();
  %>
alert('<%=msg%>');
window.location='<%=url%>';
 //window.open('','clientframe','');
}

//-->
</SCRIPT>
</HEAD>
<BODY LANGUAGE=javascript onload="return window_onload()"></BODY>
</HTML>
