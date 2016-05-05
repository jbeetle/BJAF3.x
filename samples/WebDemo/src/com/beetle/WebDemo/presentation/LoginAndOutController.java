package com.beetle.WebDemo.presentation;

import java.util.Date;

import javax.servlet.http.HttpSession;

import com.beetle.WebDemo.common.Const;
import com.beetle.WebDemo.common.LoginInfo;
import com.beetle.framework.web.controller.ControllerException;
import com.beetle.framework.web.controller.MultiActionController;
import com.beetle.framework.web.controller.WebInput;
import com.beetle.framework.web.view.ModelData;
import com.beetle.framework.web.view.View;

public class LoginAndOutController extends MultiActionController {

	public View defaultAction(WebInput arg0) throws ControllerException {
		// TODO Auto-generated method stub
		return null;
	}

	public View loginAction(WebInput webInput) throws ControllerException {
		View view = null;
		String userName = webInput.getParameter("username");
		int password = webInput.getParameterAsInteger("password");
		if (userName.equals("HenryYu") && password == 888888) {
			LoginInfo loginInfo = new LoginInfo();
			loginInfo.setLoginUser(userName);
			loginInfo.setPassword(password);
			loginInfo.setLoginTime(new Date(System.currentTimeMillis()));
			HttpSession session = webInput.getSession(true);// 创建会话，保存登录数据(操作Session演示)
			session.setAttribute("LoginInfo", loginInfo);// 以便后用。。。
			ModelData vd = new ModelData();
			vd.put("Login_Info", loginInfo);
			view = new View("LoginoutView", vd); // 返回MainView视图（连同LoginInfo数据对象一起返回）
		} else {
			ModelData vd = new ModelData();
			vd.put(Const.WEB_FORWARD_URL, "multiActionController.html");
			vd.put(Const.WEB_RETURN_MSG, "用户名不存在或者密码不正确，请重新输入，谢谢！");
			view = new View("InfoView", vd); // 返回InfoView视图
		}
		return view;
	}

	public View logoutAction(WebInput wi) throws ControllerException {
		HttpSession session = wi.getSession(false);
		session.removeAttribute("LoginInfo");
		ModelData vd = new ModelData();
		vd.put(Const.WEB_FORWARD_URL, "multiActionController.html");
		vd.put(Const.WEB_RETURN_MSG, "成功退出！");
		return new View("InfoView", vd);
	}
}
