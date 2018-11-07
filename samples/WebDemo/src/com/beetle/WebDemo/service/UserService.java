package com.beetle.WebDemo.service;

import com.beetle.WebDemo.common.User;

public interface UserService {
	User queryUser(Integer userId) throws ServiceException;
}
