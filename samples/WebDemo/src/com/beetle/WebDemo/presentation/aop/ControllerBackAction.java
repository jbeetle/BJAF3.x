package com.beetle.WebDemo.presentation.aop;
import com.beetle.framework.web.controller.ControllerException;
import com.beetle.framework.web.controller.ICutBackAction;
import com.beetle.framework.web.controller.WebInput;
import com.beetle.framework.web.view.View;
public class ControllerBackAction implements ICutBackAction{

	public View act(WebInput wi) throws ControllerException {
		System.out.println("log->["+wi.getControllerName()+"]...");
		return null;
	}


}
