<%@page contentType="text/html; charset=utf-8"%>
<%@page session="false"%>
<div class="container-fluid">
		<div class="row-fluid">
			<!-- left menu starts -->
			<div class="span2 main-menu-span">
				
				<div class="well nav-collapse sidebar-nav" id="bjaf_leftMenu">
					<%@include file="left.jsp"%>	
				</div> <!--/.well -->
			</div><!--/span-->
			<!-- left menu ends -->
							
			<div id="content" class="span10">
			<!-- content starts -->
		<%
		Boolean lh=helper.getDataValueAsBoolean("loadHomePage");
		if(lh!=null&&lh==true){
		%>
			<%@include file="home.jsp"%>	
		<%
		}
		%>
		
			<!-- content ends -->
			</div><!--/#content.span10-->
		
		</div><!--/fluid-row-->
</div><!--/.fluid-container-->