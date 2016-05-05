package com.beetle.WebDemo.presentation.webservice;

import com.beetle.WebDemo.common.User;
import com.beetle.framework.web.controller.ControllerException;
import com.beetle.framework.web.controller.WebInput;
import com.beetle.framework.web.controller.WebServiceController;
import com.beetle.framework.web.view.ModelData;

public class BeanDataDemoService extends WebServiceController {

	public ModelData defaultAction(WebInput wi) throws ControllerException {
		ModelData md = new ModelData();
		User user = new User();
		user.setName("Henry");
		user.setPhone("13501583576");
		//user.setSex("男");
		user.setSex("m");
		user.setSex("xxx");
		user.setUserId(new Integer(10001));
		user.setYear(25);
		md.setData(user);
		return md;
	}

}
