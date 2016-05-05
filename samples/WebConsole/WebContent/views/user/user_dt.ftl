<a class="addUserBtn btn btn-success" href="#" onclick="
bjaf_webServiceHtmlCall('$|views|user|addUser.ctrl');
">
    <i class="icon-plus icon-white"></i>  
										增加                                            
									</a>
  <a class="updateUserBtn btn btn-info" href="#" onclick="
  bjaf_webServiceHtmlCall('$user.UserManagerController.ctrl?$action=findUserAction&userid=${userId}');
  ">
    <i class="icon-edit icon-white"></i>  
										修改                                            
									</a>
  <a class="delUserBtn btn btn-danger" href="#" action="bjaf_webServiceJsonCall('$user.UserManagerController.ctrl?$action=delUserAction&userid=${userId}');" 
  onclick="
		var action=$(this).attr('action');
		bjaf_showConfirmDialog('您真的确定要删除此 <strong>${userName}</strong> 用户吗？',action);
  ">
    <i class="icon-trash icon-white"></i> 
										删除
									</a>
								
									
