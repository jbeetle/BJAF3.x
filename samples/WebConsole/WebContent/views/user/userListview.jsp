<%@page contentType="text/html; charset=utf-8"%>
<%@page session="false"%>
<%@page import="com.beetle.uidemo.valueobject.*"%>
<%@page import="com.beetle.framework.resource.define.PageList"%>
<%@page import="com.beetle.framework.web.view.ViewHelper"%>
<%@page import="com.beetle.framework.util.*"%>
 <div>
				<ul class="breadcrumb">
					<li>
						<a href="#">用户管理</a> <span class="divider">/</span>
					</li>
					<li>
						<a href="#">用户列表</a>
					</li>
				</ul>
</div>
<div class="row-fluid ">	
<div class="box span12">
	<div class="box-header well header-no-mouse" data-original-title>
		<h2><i class="icon-user"></i> 用户</h2>
		<div class="box-icon">
			<a href="#" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
			<a href="#" class="btn btn-close btn-round"><i class="icon-remove"></i></a>
		</div>
	</div>
	<div class="box-content">
	<table class="table table-striped table-bordered bootstrap-datatable datatable" id="userTable" >
		<thead>
			<tr>
				<th width="5%">ID</th>
				<th width="15%">用户名</th>
				<th width="8%">性别</th>
				<th>邮件</th>
				<th>生日</th>
				<th >操作</th>
			</tr>
		</thead>
		<tbody>
		    <tr>
				<td colspan="6">Loading...</td>
			</tr>
		</tbody>
		
	</table>
		 <script type="text/javascript">
	  <!--
	  $(document).ready(function() {
		$('#userTable').dataTable( {
			"sDom": "<'row-fluid'<'span6'l><'span6'f>r>t<'row-fluid'<'span12'i><'span12 center'p>>",
			"bProcessing": true,
			"iDisplayLength": 5,
			"aLengthMenu": [[5, 10, 20, 100], [5, 10, 20, 100]],
			"bServerSide": true,
			"bAutoWidth": true ,
			"bSort":true,
			"bFilter":true,
			"sPaginationType": "bootstrap",
			"sLengthMenu": "_MENU_ records per page",
			/*使用post方式
			"fnServerData": function ( sSource, aoData, fnCallback ) {
				$.ajax( {
					"dataType": 'json',
					"type": "POST",
					"url": sSource,
					"data": aoData,
					"success": fnCallback
				} );
			}*/
			"sAjaxSource": "$user.UserManagerController.ctrl?$action=pageUserAction",
			"oLanguage": {
				"sUrl": "css/dataTableCn.txt"
			},
			"aoColumns": [
				{ "sName": "userid" },
				{ "sName": "username" },
				{ "sName": "sex" },
				{ "sName": "email" },
				{ "sName": "birthday" },
				{ "sName": "actions" }
			]//
		} );
	  });
	  //-->
		</script>
	</div>
	</div>
</div>
