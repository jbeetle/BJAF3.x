package com.beetle.WebDemo.presentation.webservice;

import java.util.ArrayList;

import com.beetle.framework.web.controller.ControllerException;
import com.beetle.framework.web.controller.WebInput;
import com.beetle.framework.web.controller.WebServiceController;
import com.beetle.framework.web.view.ModelData;

public class AdaptiveDataBack extends WebServiceController {

	public ModelData defaultAction(WebInput wi) throws ControllerException {
		ModelData md = new ModelData();
		md.put("path", wi.getServletPath());
		md.put("name", "Henry");
		md.put("session", wi.getSession());
		ArrayList myList = new ArrayList();
		myList.add("aaaa");
		myList.add(new java.sql.Timestamp(System.currentTimeMillis()));
		md.put("myList", myList);
		return md;// 无需显式表明返回数据类型，由web.xml的WEB_SERVICE_DATA_DEFAULT_FORMAT参数决定
	}

}
