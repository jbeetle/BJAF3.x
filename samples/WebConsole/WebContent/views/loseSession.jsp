<%@page contentType="text/html; charset=utf-8"%>
<%@page session="false"%>
<%@page import="com.beetle.uidemo.valueobject.*"%>
<%@page import="com.beetle.uidemo.web.common.*"%>
<%@page import="com.beetle.framework.web.view.ViewHelper"%>
<%@page import="com.beetle.framework.util.*"%>
<div id="sessionLosedModal" class="modal hide fade " >
  <div class="modal-header">
	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
    <h3 id="myModalLabel">信息提示</h3>
  </div>
  <div class="modal-body">
    <p>会话已经过期，请重新登录！</p>
  </div>
  <div class="modal-footer">
    <button class="btn" data-dismiss="modal" aria-hidden="true">确定</button>
 </div>
</div>
 <script type="text/javascript">
  <!--
	$('#sessionLosedModal').modal(
	{keyboard: false,backdrop:'static'},'toggle'
	);
	$('#sessionLosedModal').on('hidden', function () {
		window.location='index.html';
	});
  //-->
</script>