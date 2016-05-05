<%@page contentType="text/html; charset=utf-8"%>
<%@page session="false"%>
<%@page import="com.beetle.dwzdemo.valueobject.*"%>
<%@page import="com.beetle.framework.web.view.ViewHelper"%>
<%@page import="com.beetle.framework.util.*"%>
<%
ViewHelper helper=new ViewHelper(request);
XxUser user=(XxUser)helper.getDataValue("user");
%>
<%!
String sexSel(int x,int y){
	if(x==y){
		return "selected";
	}else{
		return "";
	}
}
%>
<div class="pageContent">
	<form method="post" action="$user.UserManagerController.ctrl" class="pageForm required-validate" onsubmit="return validateCallback(this,dialogAjaxDone);">
		<input type="hidden" name="$action" value="updateUserAction" />
		<input type="hidden" name="userid" value="<%=user.getUserid()%>" />
		<div class="pageFormContent" layoutH="56">
			<p>
				<label>用户名：</label>
				<input name="username" class="required" type="text" size="30" value="<%=user.getUsername()%>"/>
			</p>
			<p>
				<label>性别：</label>
				<select name="sex" class="required combox">
					<option value="">请选择</option>
					<option value="1" <%=sexSel(1,user.getSex())%>>男</option>
					<option value="0" <%=sexSel(0,user.getSex())%>>女</option>
					<option value="-1" <%=sexSel(-1,user.getSex())%>>不男不女</option>
				</select>
			</p>
			<p>
				<label>电子邮件：</label>
				<input type="text" name="email" class="required email" value="<%=user.getEmail()%>"/>
			</p>
			<p>
				<label>密码：</label>
				<input id="w_validation_pwd" type="password" name="passwd" class="alphanumeric" minlength="6" maxlength="20" alt="字母、数字、下划线 6-20位"/>
			</p>
			<p>
				<label>确认密码：</label>
				<input type="password" name="passwd2" equalto="#w_validation_pwd"/>
			</p>
			<p>
				<label>生日：</label>
				<input type="text" name="birthday" class="required date" format="yyyy-MM-dd HH:mm:ss" readonly="true" size="30" value="<%=ConvertUtil.dateFormat(user.getBirthday(),"yyyy-MM-dd HH:mm:ss")%>"/>
				<a class="inputDateButton" href="javascript:;">选择</a>
			</p>
		</div>
		<div class="formBar">
			<ul>
				<li><div class="buttonActive"><div class="buttonContent"><button type="submit">更新</button></div></div></li>
				<li>
					<div class="button"><div class="buttonContent"><button type="button" class="close">关闭</button></div></div>
				</li>
			</ul>
		</div>
	</form>
</div>
