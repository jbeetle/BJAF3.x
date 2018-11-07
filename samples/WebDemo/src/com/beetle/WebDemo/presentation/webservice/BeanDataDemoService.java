package com.beetle.WebDemo.presentation.webservice;

import com.beetle.WebDemo.common.User;
import com.beetle.WebDemo.service.UserService;
import com.beetle.framework.resource.dic.def.ServiceField;
import com.beetle.framework.web.controller.ControllerException;
import com.beetle.framework.web.controller.WebInput;
import com.beetle.framework.web.controller.WebServiceController;
import com.beetle.framework.web.view.ModelData;

public class BeanDataDemoService extends WebServiceController {
	@ServiceField
	private UserService userService;

	public ModelData defaultAction(WebInput wi) throws ControllerException {
		ModelData md = new ModelData();
		Integer userId = wi.getParameterAsInteger("id");
		if (userId == null) {
			userId = 1000;
		}
		User user = userService.queryUser(userId);
		md.setData(user);
		return md;
	}

}
