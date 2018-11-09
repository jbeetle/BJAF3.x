package com.beetle.WebDemo.service;

import com.beetle.WebDemo.common.User;
import com.beetle.WebDemo.persistence.UserDao;
import com.beetle.framework.resource.dic.def.DaoField;

public class UserServiceImpl implements UserService {
	@DaoField
	private UserDao userDao;

	@Override
	public User queryUser(Integer userId) throws ServiceException {
		if (userId == null) {
			throw new ServiceException(-201, "Userid can't be null");
		}
		return userDao.findUserById(userId);
	}

}
