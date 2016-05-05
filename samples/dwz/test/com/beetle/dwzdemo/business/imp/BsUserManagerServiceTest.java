package com.beetle.dwzdemo.business.imp;

import static org.junit.Assert.*;

import org.junit.Test;

import com.beetle.dwzdemo.business.IUserManagerService;
import com.beetle.dwzdemo.valueobject.XxUser;
import com.beetle.framework.AppContext;
import com.beetle.framework.resource.define.PageList;

public class BsUserManagerServiceTest {

	@Test
	public void testShowAllUserByPage() {

		IUserManagerService us = AppContext.getInstance().lookup(
				IUserManagerService.class);
		PageList<XxUser> pl = us.showAllUserByPage(1, 20, "userid", "desc");
		System.out.println(pl);
		assertNotNull(pl);
	}

}
