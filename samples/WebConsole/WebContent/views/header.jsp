<%@page contentType="text/html; charset=utf-8"%>
<%@page session="false"%>
<%@page import="com.beetle.uidemo.valueobject.*"%>
<%@page import="com.beetle.framework.web.view.ViewHelper"%>
<%@page import="com.beetle.framework.util.*"%>
<%
ViewHelper helper=new ViewHelper(request);
if(!helper.existSession()){
	response.sendRedirect("index.html");
}
%>
<!DOCTYPE html>
<html lang="cn">
<head>
	<meta charset="utf-8">
	<title>BJAF Admin Template Demo</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="author" content="BJAF Team">
	<!-- The styles -->
	<link id="bs-css" href="css/bootstrap-cerulean.css" rel="stylesheet">
	<style type="text/css">
	  body {
		padding-bottom: 40px;
	  }
	  .sidebar-nav {
		padding: 9px 0;
	  }
	  *[hidden] {
		display: none;
	  }
	</style>
	<link href="css/bootstrap-responsive.css" rel="stylesheet">
	<link href="css/charisma-app.css" rel="stylesheet">
	<link href="css/jquery-ui-1.8.21.custom.css" rel="stylesheet">
	<link href='css/fullcalendar.css' rel='stylesheet'>
	<link href='css/fullcalendar.print.css' rel='stylesheet'  media='print'>
	<link href='css/chosen.css' rel='stylesheet'>
	<link href='css/uniform.default.css' rel='stylesheet'>
	<link href='css/colorbox.css' rel='stylesheet'>
	<link href='css/jquery.cleditor.css' rel='stylesheet'>
	<link href='css/jquery.noty.css' rel='stylesheet'>
	<link href='css/noty_theme_default.css' rel='stylesheet'>
	<link href='css/elfinder.min.css' rel='stylesheet'>
	<link href='css/elfinder.theme.css' rel='stylesheet'>
	<link href='css/jquery.iphone.toggle.css' rel='stylesheet'>
	<link href='css/opa-icons.css' rel='stylesheet'>
	<link href='css/uploadify.css' rel='stylesheet'>
	<link href='css/datepicker.css' rel='stylesheet'>
	<!-- The HTML5 shim, for IE6-8 support of HTML5 elements -->
	<!--[if lt IE 9]>
	  <script src="js/html5.js"></script>
	<![endif]-->
	<!-- The fav icon -->
	<link rel="shortcut icon" href="img/favicon.ico">
</head>

<body>
	<!-- topbar starts -->
	<div class="navbar navbar-static-top">
		<div class="navbar-inner">
			<div class="container-fluid">
				<a class="btn btn-navbar" data-toggle="collapse" data-target=".top-nav.nav-collapse,.sidebar-nav.nav-collapse">
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</a>
				<a class="brand" href="#"> <img alt="Charisma Logo" src="img/logo20.png" /> <span>XXX管理系统</span></a>
				<div class="btn-group pull-right" >
					<a class="btn" href="#" action="window.location='$SysDoorController.ctrl?$action=logoutAction'" onclick="
						var action=$(this).attr('action');
						bjaf_showConfirmDialog('您真的确定退出？',action);
					">
						<i class="icon-off"></i>
					</a>
				</div>
				<div class="btn-group pull-right" >
					<a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
						<i class="icon-user"></i><span class="hidden-phone"> <%=helper.getDataFromSession("loginUser")%></span>
						<span class="caret"></span>
					</a>
					<ul class="dropdown-menu" >
						<li><a href="#"><i class="icon-blank"></i> 个人资料</a></li>
						<li><a href="help.html" target="_blank"><i class="icon-question-sign"></i> 帮助文档</a></li>
						<li class="divider"></li>
						<ul id="themes"><i class="icon-gift"></i>更换主题
						<li><a data-value="classic" href="#"><i class="icon-blank"></i> 经典主题</a></li>
						<li><a data-value="simplex" href="#"><i class="icon-blank"></i> 简约主题</a></li>
						<li><a data-value="cyborg" href="#"><i class="icon-blank"></i> 科技主题</a></li>
						<li><a data-value="united" href="#"><i class="icon-blank"></i> 香橙主题</a></li>
						<li><a data-value="redy" href="#"><i class="icon-blank"></i> 丽人主题</a></li>
						<li><a data-value="cerulean" href="#"><i class="icon-blank"></i> 海洋主题</a></li>
						</ul>
					</ul>
				</div>
				<!-- mainmenu -->
				<ul class="nav nav-pills" id="main_top_menu">
                    <li class="active"><a class="ajax-link" seq="seq-1" href="$SysDoorController.ctrl?$action=homeAction"><i class="icon-home"></i> 首页</a></li>
                    <li><a class="ajax-link" seq="seq-2" href="$user.UserManagerController.ctrl?$action=showMainView">用户管理</a></li>
                    <li><a class="ajax-link" href="views/module2/module2.jsp" seq="seq-3">module2</a></li>
                    <li><a class="ajax-link" href="views/sample/ui.html" seq="seq-4">UI演示</a></li>
                </ul>
				<div class="nav-collapse collapse navbar-responsive-collapse">
                    <form class="navbar-search pull-right" action="">
                      <input type="text" class="search-query span2" placeholder="Search">
                    </form>
				</div>
				<!-- end -->
			</div>
		</div>
	</div>
	<!-- topbar ends -->
	