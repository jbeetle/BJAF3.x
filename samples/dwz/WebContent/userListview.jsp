<%@page contentType="text/html; charset=utf-8"%>
<%@page session="false"%>
<%@page import="com.beetle.dwzdemo.valueobject.*"%>
<%@page import="com.beetle.framework.resource.define.PageList"%>
<%@page import="com.beetle.framework.web.view.ViewHelper"%>
<%@page import="com.beetle.framework.util.*"%>
<%
ViewHelper helper=new ViewHelper(request);
PageList<XxUser> pl=(PageList<XxUser>)helper.getDataValue("userPageList");
String orderField=helper.getDataValueAsString("orderField");
String orderDirection=helper.getDataValueAsString("orderDirection");
%>
<%!
String toSex(int x){
	if(x==1){
		return "男";
	}else if(x==0){
		return "女";
	}else{
		return "不男不女";
	}
}
%>
 <script type="text/javascript">
  <!--
	function navTabSearchWithFormValidate(form){
		var $form = $(form);
		if (!$form.valid()) {
			return false;
		}
		return navTabSearch($form);
	}
  //-->
  </script>
<form id="pagerForm" method="post" action="$user.UserManagerController.ctrl">
	<input type="hidden" name="$action" value="pageUserAction" />
	<input type="hidden" name="pageNum" value="1" />
	<input type="hidden" name="numPerPage" value="<%=pl.getPageSize()%>" />
	<input type="hidden" name="orderField" value="${param.orderField}" />
	<input type="hidden" name="orderDirection" value="${param.orderDirection}" />
</form>
<div class="pageHeader">
	<form onsubmit="return navTabSearchWithFormValidate(this);" class="pageForm required-validate" action="$user.UserManagerController.ctrl" method="post">
		<input type="hidden" name="$action" class="required" value="nameSearchAction" />
		<div class="searchBar">
			<table class="searchContent">
				<tr>
					<td>
						用户名：<input type="text" class="required" name="username" />
					</td>
					<td>
						<div class="buttonActive"><div class="buttonContent"><button type="submit">模糊检索</button>
					</td>
				</tr>
			</table>
		</div>
	</form>
</div>
<div class="pageContent">
	<div class="panelBar">
		<ul class="toolBar">
			<li><a class="add" href="addUser.html" target="dialog" mask="true"><span>添加用户</span></a></li>
			<li><a class="delete" href="$user.UserManagerController.ctrl?$action=delUserAction&uid={sid_user}" target="ajaxTodo" title="确定要删除吗?"><span>删除</span></a></li>
			<li><a class="edit" href="$user.UserManagerController.ctrl?$action=findUserAction&uid={sid_user}" target="dialog" mask="true"><span>修改</span></a></li>
			<li class="line">line</li>
		</ul>
	</div>
	<table class="table" width="100%" layoutH="138">
		<thead>
			<tr>
				<th orderField="userid" <%if(orderField.equals("userid")){%>class="<%=orderDirection%>"<%}%>>用户ID</th>
				<th >用户名</th>
				<th align="center">性别</th>
				<th >密码</th>
				<th >邮件</th>
				<th orderField="birthday" <%if(orderField.equals("birthday")){%>class="<%=orderDirection%>"<%}%>>生日</th>
			</tr>
		</thead>
		<tbody>
		    <%
			for(XxUser user:pl){
			%>
			<tr target="sid_user" rel="<%=user.getUserid()%>">
				<td><%=user.getUserid()%></td>
				<td><%=user.getUsername()%></td>
				<td><%=toSex(user.getSex())%></td>
				<td><%=user.getPasswd()%></td>
				<td><%=user.getEmail()%></td>
				<td><%=ConvertUtil.dateFormat(user.getBirthday(),"yyyy-MM-dd HH:mm:ss")%></td>
			</tr>
			<%
			}
			%>
		</tbody>
	</table>
	<div class="panelBar">
		<div class="pages">
			<span>显示</span>
			<select class="combox" name="numPerPage" onchange="navTabPageBreak({numPerPage:this.value})">
			    <%
				if(pl.getPageSize()==20){
				%>
				<option value="20" selected>20</option>
				<option value="30">30</option>
				<option value="50">50</option>
				<%
				}else if(pl.getPageSize()==30){
				%>
				<option value="20">20</option>
				<option value="30"selected>30</option>
				<option value="50">50</option>
				<%
				}else if(pl.getPageSize()==50){
				%>
				<option value="20">20</option>
				<option value="30">30</option>
				<option value="50" selected>50</option>
				<%
				}else{
				%>
				<option value="20">20</option>
				<option value="30">30</option>
				<option value="50">50</option>
				<%
				}
				%>
			</select>
			<span>条，共<%=pl.getRecordAmount()%>条</span>
		</div>
		<div class="pagination" targetType="navTab" totalCount="<%=pl.getRecordAmount()%>" numPerPage="<%=pl.getPageSize()%>" pageNumShown="10" currentPage="<%=pl.getCurPageNumber()%>">
		</div>

	</div>
</div>
