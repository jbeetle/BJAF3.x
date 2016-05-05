package com.beetle.WebDemo.presentation.onoff;

import javax.servlet.ServletContext;

import com.beetle.framework.web.onoff.*;

public class WebAppOnOff implements IStartUp, ICloseUp {

	@Override
	public void closeUp(ServletContext arg0) {
		System.out.println("close event work");
		
	}

	@Override
	public void startUp(ServletContext arg0) {
		System.out.println("startup event work");
		
	}

}
