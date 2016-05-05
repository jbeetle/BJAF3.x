package com.beetle.WebDemo.presentation;

import java.util.Date;

import com.beetle.framework.web.controller.ControllerException;
import com.beetle.framework.web.controller.ControllerImp;
import com.beetle.framework.web.controller.WebInput;
import com.beetle.framework.web.view.ModelData;
import com.beetle.framework.web.view.View;

public class CacheDemoController extends ControllerImp {

	public CacheDemoController() {
		super();
		this.setCacheSeconds(-1);// 为了演示，禁止浏览器进行缓存处理
	}

	public View perform(WebInput wi) throws ControllerException {
		View view = null;
		Date now = new Date(System.currentTimeMillis());
		ModelData vd = new ModelData();
		vd.put("NOW", now);
		view = new View("DemoCacheView", vd);
		return view;
	}

}
