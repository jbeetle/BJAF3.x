package com.beetle.WebDemo.presentation;

import java.util.Date;

import javax.servlet.http.HttpSession;

import com.beetle.WebDemo.common.Const;
import com.beetle.WebDemo.common.LoginInfo;
import com.beetle.framework.web.controller.ControllerException;
import com.beetle.framework.web.controller.ControllerImp;
import com.beetle.framework.web.controller.WebInput;
import com.beetle.framework.web.view.ModelData;
import com.beetle.framework.web.view.View;

public class LoginController extends ControllerImp {
	public LoginController() {

	}

	public View perform(WebInput webInput) throws ControllerException {
		View view = null;
		String userName = webInput.getParameter("username"); // 获取页面输入的参数
		int password = webInput.getParameterAsInteger("password");
		int veiwFlag = webInput.getParameterAsInteger("veiwFlag");// 为了视图演示的标记
		// 调用业务对象处理业务逻辑，本示例在这里只是简单地作了一个字符串比较
		if (userName.equals("HenryYu") && password == 888888) {
			LoginInfo loginInfo = new LoginInfo();
			loginInfo.setLoginUser(userName);
			loginInfo.setPassword(password);
			loginInfo.setLoginTime(new Date(System.currentTimeMillis()));
			HttpSession session = webInput.getSession(true);// 创建会话，保存登录数据(操作Session演示)
			session.setAttribute("LoginInfo", loginInfo);// 以便后用。。。
			ModelData vd = new ModelData();
			vd.put("Login_Info", loginInfo);
			if (veiwFlag == 0) {// 采取标准的JSP视图显示数据
				view = new View("MainView", vd); // 返回MainView视图（连同LoginInfo数据对象一起返回）
			} else if (veiwFlag == 1) {// 采取freemarker模板作为视图显示数据
				view = new View("LginFtlView", vd);
			}
		} else {
			ModelData vd = new ModelData();
			vd.put(Const.WEB_FORWARD_URL, "login.html");
			vd.put(Const.WEB_RETURN_MSG, "用户名不存在或者密码不正确，请重新输入，谢谢！");
			view = new View("InfoView", vd); // 返回InfoView视图
		}
		return view;
	}

}
