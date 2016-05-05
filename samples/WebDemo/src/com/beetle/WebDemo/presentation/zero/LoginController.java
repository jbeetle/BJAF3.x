package com.beetle.WebDemo.presentation.zero;

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

	public View perform(WebInput wi) throws ControllerException {
		View view = null;
		String userName = wi.getParameter("username"); // 获取页面输入的参数
		int password = wi.getParameterAsInteger("password");
		int veiwFlag = wi.getParameterAsInteger("veiwFlag");// 为了视图演示的标记
		if (userName.equals("HenryYu") && password == 888888) {
			LoginInfo loginInfo = new LoginInfo();
			loginInfo.setLoginUser(userName);
			loginInfo.setPassword(password);
			loginInfo.setLoginTime(new Date(System.currentTimeMillis()));
			HttpSession session = wi.getSession(true);// 创建会话，保存登录数据(操作Session演示)
			session.setAttribute("LoginInfo", loginInfo);// 以便后用。。。
			ModelData vd = new ModelData();
			vd.put("Login_Info", loginInfo);
			if (veiwFlag == 0) {// 采取标准的JSP视图显示数据
				view = new View("/views/main.jsp", vd); // 直接返回视图的具体物理路径和文件名
			} else if (veiwFlag == 1) {// 采取freemarker模板作为视图显示数据
				view = new View("/views/template/LoginView.ftl", vd);
			}
		} else {
			ModelData vd = new ModelData();
			vd.put(Const.WEB_FORWARD_URL, "login.html");
			vd.put(Const.WEB_RETURN_MSG, "用户名不存在或者密码不正确，请重新输入，谢谢！");
			view = new View("/common/infoView.jsp", vd); // 直接返回视图的具体物理路径和文件名
		}
		return view;
	}

}
