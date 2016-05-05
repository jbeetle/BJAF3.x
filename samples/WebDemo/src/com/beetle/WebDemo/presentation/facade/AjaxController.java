package com.beetle.WebDemo.presentation.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.beetle.WebDemo.common.User;
import com.beetle.framework.web.controller.ControllerException;
import com.beetle.framework.web.controller.FacadeController;
import com.beetle.framework.web.controller.WebInput;
import com.beetle.framework.web.view.ModelData;

/**
 * 
 * 在bjaf2.x中删除了bjaf1.x的ajax 控制器的实现，原来的实现提供了javascript客户端和ajax服务端。<br>
 * 由于jquery流行和功能强大，在bjaf2.x中我们推荐使用jquery+ws控制器来完成ajax的功能
 * 
 */
public class AjaxController extends FacadeController {

	public AjaxController() {
		super();
	}

	@WSCtrl
	public ModelData loginAction(WebInput wi) throws ControllerException {
		ModelData md = new ModelData();
		String userName = wi.getParameter("username"); // 获取页面输入的参数
		int password = wi.getParameterAsInteger("password");
		// 调用业务对象处理业务逻辑，本示例在这里只是简单地作了一个字符串比较
		if (userName.equals("HenryYu") && password == 888888) {
			User user = new User();
			user.setUserId(new Integer(10001));
			user.setName("余浩东");
			user.setPhone("13501583576");
			user.setSex("男");
			user.setYear(30);
			md.put("UserInfo", user);
			md.put("LoginTime", new Date(System.currentTimeMillis()));
			md.put("returnFlag", 0);
			md.put("returnMsg", "登陆成功！");
		} else {
			md.put("returnFlag", -1);
			md.put("returnMsg", "登陆失败！");
		}
		return md.asJSON();
	}

	@WSCtrl
	public ModelData showDataAction(WebInput webInput)
			throws ControllerException {
		List<User> userList = new ArrayList<User>();
		User user = new User();
		user.setUserId(new Integer(100));
		user.setName("Henry");
		user.setPhone("13501583576");
		user.setSex("M");
		user.setYear(31);
		userList.add(user);
		User user2 = new User();
		user2.setUserId(new Integer(101));
		user2.setName("Tom");
		user2.setPhone("13501583574");
		user2.setSex("M");
		user2.setYear(28);
		userList.add(user2);
		User user3 = new User();
		user3.setUserId(new Integer(103));
		user3.setName("Mary");
		user3.setPhone("13501583571");
		user3.setSex("F");
		user3.setYear(21);
		userList.add(user3);
		ModelData md = new ModelData();
		md.setData(userList);
		return md.asJSON();
	}
}
