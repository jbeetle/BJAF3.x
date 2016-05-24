package com.beetle.component.security.web;

import javax.servlet.ServletContext;

import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.env.WebEnvironment;

import com.beetle.framework.web.GlobalDispatchServlet;

public class initializeListener extends EnvironmentLoaderListener{

	

	@Override
	public WebEnvironment initEnvironment(ServletContext servletContext) throws IllegalStateException {	
		GlobalDispatchServlet.checkConfig(servletContext);
		System.out.println("GlobalDispatchServlet.checkConfig called");
		return super.initEnvironment(servletContext);
	}

}
