package com.beetle.WebDemo.persistence;

import com.beetle.WebDemo.common.User;
import com.beetle.framework.persistence.access.operator.DBOperatorException;

public class UserDaoImpl implements UserDao {

	@Override
	public User findUserById(Integer userId) throws DBOperatorException {
		User user = new User();
		user.setName("Henry");
		user.setPhone("13501583576");
		// user.setSex("男");
		user.setSex("m");
		user.setSex("xxx");
		user.setUserId(new Integer(10001));
		user.setYear(25);
		return user;
	}

}
