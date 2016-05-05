package com.beetle.WebDemo.presentation.aop;

import javax.servlet.http.HttpSession;

import com.beetle.WebDemo.common.Const;
import com.beetle.WebDemo.common.LoginInfo;
import com.beetle.framework.web.controller.ControllerException;
import com.beetle.framework.web.controller.ICutFrontAction;
import com.beetle.framework.web.controller.WebInput;
import com.beetle.framework.web.view.ModelData;
import com.beetle.framework.web.view.View;

public class ControllerFrontAction implements ICutFrontAction {

	public View act(WebInput wi) throws ControllerException {
		String ctrlName = wi.getControllerName();
		if (ctrlName.equalsIgnoreCase("DemoDraw2Controller.draw")) {// ֻ�Դ˿���������
			HttpSession session = wi.getSession(false);
			if (session == null) {
				return errView();
			} else {
				LoginInfo info = (LoginInfo) session.getAttribute("LoginInfo");
				if (!info.getLoginUser().equals("HenryYu")) {
					return errView();
				}
			}
		}
		return null;

	}

	private View errView() {
		ModelData vd = new ModelData();
		vd.put(Const.WEB_FORWARD_URL, "aop.html");
		vd.put(Const.WEB_RETURN_MSG, "�οͲ��ܽ��д˲����������ȵ�¼��лл��");
		View view = new View("InfoView", vd);
		return view;
	}

}
