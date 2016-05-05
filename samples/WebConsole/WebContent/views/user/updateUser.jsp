<%@page contentType="text/html; charset=utf-8"%>
<%@page session="false"%>
<%@page import="com.beetle.uidemo.valueobject.*"%>
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
<script type="text/javascript">
	  <!--
	  $(document).ready(function() {
			$("#birthday").datepicker({
			format: 'yyyy-mm-dd hh:ii:ss',
			weekStart: 1,
			days: ["日","一","二","三","四","五","六"],
			months: ["一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月"]
			});
			$('#updateCancel').on('click', function (e) {
				e.preventDefault();
				bjaf_webServiceHtmlCall('$user.UserManagerController.ctrl?$action=showMainView');
			});
			$('#updateSubmit').on('click', function (e) {
				e.preventDefault();
				var frm=$('#userForm');
				if($('#w_validation_pwd').val()!=''){
					if($('#w_validation_pwd').val()!=$('#w_validation_pwd2').val()){
						alert('密码前后不一致');
						return ;
					}
				}
				bjaf_ajaxFormSubmit(frm);
			});
		 } );
	  //-->
</script>
<div>
				<ul class="breadcrumb">
					<li>
						<a href="#">用户管理</a> <span class="divider">/</span>
					</li>
					<li>
						<a href="#">用户列表</a> <span class="divider">/</span>
					</li>
					<li>
						<a href="#">用户资料</a>
					</li>
				</ul>
</div>
<div class="row-fluid">
				<div class="box span12">
					<div class="box-header well header-no-mouse" data-original-title>
						<h2><i class="icon-edit"></i> 更新对话框</h2>
						<div class="box-icon">
							<a href="#" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
							<a href="#" class="btn btn-close btn-round"><i class="icon-remove"></i></a>
						</div>
					</div>
					<div class="box-content">
					<!--form-->
					<form id="userForm"  class="form-horizontal" method="post" action="$user.UserManagerController.ctrl" >
					<fieldset>
		<input type="hidden" name="$action" value="updateUserAction" />
		<input type="hidden" name="userid" value="<%=user.getUserid()%>" />
			 <div class="control-group">
				<label class="control-label">用户名：</label>
				<div class="controls">
					<input name="username" type="text" size="30" value="<%=user.getUsername()%>"/>
				</div>
			</div>
			<div class="control-group">
				<label  class="control-label">性别：</label>
				<div class="controls">
					<select name="sex">
						<option value="">请选择</option>
						<option value="1" <%=sexSel(1,user.getSex())%>>男</option>
						<option value="0" <%=sexSel(0,user.getSex())%>>女</option>
						<option value="2" <%=sexSel(2,user.getSex())%>>不男不女</option>
					</select>
				</div>
			</div>
			<div class="control-group">
				<label  class="control-label">电子邮件：</label>
				<div class="controls">
				<input type="text" name="email" value="<%=user.getEmail()%>"/>
				</div>
			</div>
			<div class="control-group">
				<label  class="control-label">密码：</label>
				<div class="controls">
					<input id="w_validation_pwd" type="password" name="passwd" />
					<input  id="w_validation_pwd2" type="password" name="passwd2" />
				</div>
			</div>
			<div class="control-group">
				<label  class="control-label">生日：</label>
				<div class="controls">
				<input id="birthday" type="text" name="birthday" data-datepicker-format="yyyy-mm-dd hh:ii:ss" value="<%=ConvertUtil.dateFormat(user.getBirthday(),"yyyy-MM-dd HH:mm:ss")%>" />
				</div>
			</div>
		<div class="form-actions">
			<button id="updateSubmit" class="btn btn-primary">更新</button>
			<button id="updateCancel" class="btn" >取消</button>
		 </div>
		</fieldset>
	</form>
	<!--end form -->
					</div>
				</div><!--/span-->
			</div><!--/row-->

	

