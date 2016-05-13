package com.beetle.component.security.persistence.imp;

import com.beetle.framework.AppProperties;

public class Helper {
	public static String DATASOURCE = "";
	static {
		DATASOURCE = AppProperties.get("security_datasource", "SYSDATASOURCE_DEFAULT");
	}

}
