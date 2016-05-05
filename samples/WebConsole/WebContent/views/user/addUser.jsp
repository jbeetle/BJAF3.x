<%@page contentType="text/html; charset=utf-8"%>
<%@page session="false"%>
<%@page import="com.beetle.uidemo.valueobject.*"%>
<%@page import="com.beetle.framework.web.view.ViewHelper"%>
<%@page import="com.beetle.framework.util.*"%>

<div>
				<ul class="breadcrumb">
					<li>
						<a href="#">用户管理</a> <span class="divider">/</span>
					</li>
					<li>
						<a href="#">用户列表</a> <span class="divider">/</span>
					</li>
					<li>
						<a href="#">添加用户</a>
					</li>
				</ul>
</div>
<script type="text/javascript">
	  <!--
	  $(document).ready(function() {
			$("#birthday").datepicker({
			format: 'yyyy-mm-dd hh:ii:ss',
			weekStart: 1,
			days: ["日","一","二","三","四","五","六"],
			months: ["一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月"]
			});
			$('#addCancel').on('click', function (e) {
				e.preventDefault();
				bjaf_webServiceHtmlCall('$user.UserManagerController.ctrl?$action=showMainView');
			});
			$('#addSubmit').on('click', function (e) {
				e.preventDefault();
				var  xx=$("#userForm").validate({
				//http://docs.jquery.com/Plugins/Validation
				rules:{
					username:"required",
					email:{
							required:true,
							email: true
						},
					passwd:{
						required:true,
						minlength: 6
					},
					passwd2:{
						required:true,
						equalTo: "#passwd"
					},
					sex:"required"
				},
				messages:{
					username:"请输入用户名",
					email:{
						required:"请输入你的邮件地址",
						email:"请输入合法的邮件地址"
					},
					passwd:{
						required:"请输入你的密码",
						minlength:"密码至少6位"
					},
					passwd2:{
						required:"请输入你确定密码",
						equalTo:"密码前后不一致，必须要一致"
					},
					sex:"请选择性别"
				},
				errorClass: "help-inline",
				errorElement: "span",
				highlight:function(element, errorClass, validClass) {
					$(element).parents('.control-group').addClass('error');
				},
				unhighlight: function(element, errorClass, validClass) {
					$(element).parents('.control-group').removeClass('error');
					$(element).parents('.control-group').addClass('success');
				}
			});
			if(xx.form()){
				var frm=$('#userForm');
				bjaf_ajaxFormSubmit(frm);
			}
			});
			//
			
			//
		 } );
	  //-->
</script>
<div class="row-fluid">
				<div class="box span12">
					<div class="box-header well header-no-mouse" data-original-title>
						<h2><i class="icon-edit"></i> 添加对话框</h2>
						<div class="box-icon">
							<a href="#" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
							<a href="#" class="btn btn-close btn-round"><i class="icon-remove"></i></a>
						</div>
					</div>
					<div class="box-content">
					<!--form-->
					<form id="userForm" name="userForm" class="form-horizontal" method="post" action="$user.UserManagerController.ctrl" >
					<fieldset>
		<input type="hidden" name="$action" value="addUserAction" />
			 <div class="control-group">
				<label class="control-label" >用户名：</label>
				<div class="controls">
					<input name="username" type="text" size="30" id="username" />
				</div>
			</div>
			<div class="control-group">
				<label  class="control-label">性别：</label>
				<div class="controls">
					<select name="sex" id="sex">
						<option value="">请选择</option>
						<option value="1" >男</option>
						<option value="0" >女</option>
						<option value="2" >不男不女</option>
					</select>
				</div>
			</div>
			<div class="control-group">
				<label  class="control-label">电子邮件：</label>
				<div class="controls">
				<input type="text" name="email" id="email"   />
				</div>
			</div>
			<div class="control-group">
				<label  class="control-label" >密码：</label>
				<div class="controls">
					<input id="passwd" type="password" name="passwd"  />
				</div>
			</div>
			<div class="control-group">
				<label  class="control-label">确定密码：</label>
				<div class="controls">
					<input  id="passwd2" type="password" name="passwd2"  />
				</div>
			</div>
			<div class="control-group">
				<label  class="control-label">生日：</label>
				<div class="controls">
				<input id="birthday" type="text" name="birthday" data-datepicker-format="yyyy-mm-dd hh:ii:ss"  />
				</div>
			</div>
			<div class="form-actions">
			<button id="addSubmit" class="btn btn-primary">添加</button>
			<button id="addCancel" class="btn" >取消</button>
		 </div>
		</fieldset>
	</form>
	<!--end form -->
					</div>
				</div><!--/span-->
			</div><!--/row-->

	

