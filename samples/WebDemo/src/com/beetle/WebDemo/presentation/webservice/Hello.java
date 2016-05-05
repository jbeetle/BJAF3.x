package com.beetle.WebDemo.presentation.webservice;

import java.util.ArrayList;

import com.beetle.framework.web.controller.ControllerException;
import com.beetle.framework.web.controller.WebInput;
import com.beetle.framework.web.controller.WebServiceController;
import com.beetle.framework.web.view.ModelData;

public class Hello extends WebServiceController {

	public ModelData defaultAction(WebInput wi) throws ControllerException {
		ModelData md = new ModelData();
		md.put("path", wi.getServletPath());
		md.put("name", "Henry");
		md.put("session", wi.getSession());
		ArrayList myList = new ArrayList();
		myList.add("aaaa");
		myList.add(new java.sql.Timestamp(System.currentTimeMillis()));
		md.put("myList", myList);
		return md.asXML();
	}

	public ModelData jsonTestService(WebInput wi) throws ControllerException {
		ModelData md = new ModelData();
		md.put("path", wi.getServletPath());
		md.put("name", "Henry");
		md.put("phone", wi.getParameter("phone"));
		md.put("session", wi.getSession());
		ArrayList myList = new ArrayList();
		myList.add("aaaa");
		myList.add(new java.sql.Timestamp(System.currentTimeMillis()));
		md.put("myList", myList);
		return md.asJSON();
	}
}
