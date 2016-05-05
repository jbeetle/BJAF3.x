package com.beetle.WebDemo.presentation;

import java.util.Date;

import com.beetle.WebDemo.common.Const;
import com.beetle.WebDemo.common.LoginInfo;
import com.beetle.framework.web.controller.ControllerException;
import com.beetle.framework.web.controller.ControllerImp;
import com.beetle.framework.web.controller.WebInput;
import com.beetle.framework.web.view.ModelData;
import com.beetle.framework.web.view.View;

public class LoginWithVerifyCodeController extends ControllerImp {

	public View perform(WebInput webInput) throws ControllerException {
		View view = null;
		String userName = webInput.getParameter("username"); // 获取页面输入的参数
		int password = webInput.getParameterAsInteger("password");
		String checkCode = webInput.getParameter("checkCode");// 获取用户页面输入验证码
		String verifyCode = webInput.getVerifyCode();// 获取系统生成的验证码
		if (checkCode.trim().equalsIgnoreCase(verifyCode)) {
			// 调用业务对象处理业务逻辑，本示例在这里只是简单地作了一个字符串比较
			if (userName.equals("HenryYu") && password == 888888) {
				LoginInfo loginInfo = new LoginInfo();
				loginInfo.setLoginUser(userName);
				loginInfo.setPassword(password);
				loginInfo.setLoginTime(new Date(System.currentTimeMillis()));
				ModelData vd = new ModelData();
				vd.put("Login_Info", loginInfo);
				view = new View("MainView", vd); // 返回MainView视图（连同LoginInfo数据对象一起返回）
			} else {
				ModelData vd = new ModelData();
				vd.put(Const.WEB_FORWARD_URL, "login3.html");
				vd.put(Const.WEB_RETURN_MSG, "用户名不存在或者密码不正确，请重新输入，谢谢！");
				view = new View("InfoView", vd); // 返回InfoView视图
			}
		} else {
			ModelData vd = new ModelData();
			vd.put(Const.WEB_FORWARD_URL, "login3.html");
			vd.put(Const.WEB_RETURN_MSG, "验证码输入不正确，请重新输入，谢谢！");
			view = new View("InfoView", vd);
		}
		return view;
	}

}
