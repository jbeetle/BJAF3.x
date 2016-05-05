package com.beetle.dwzdemo.business;

import com.beetle.dwzdemo.valueobject.XxUser;
import com.beetle.framework.resource.define.PageList;

public interface IUserManagerService {
	PageList<XxUser> showAllUserByPage(int pageNumber, int pageSize,
			String orderField, String orderArith) throws ServiceException;

	void createUser(XxUser user) throws ServiceException;

	int deleteUser(long userId) throws ServiceException;

	XxUser findUser(long userId) throws ServiceException;

	int updateUser(XxUser user) throws ServiceException;

	PageList<XxUser> searchByName(String username, int pageNumber,
			int pageSize, String orderField, String orderArith)
			throws ServiceException;

}
