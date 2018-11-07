package com.beetle.WebDemo.persistence;

import com.beetle.WebDemo.common.User;
import com.beetle.framework.persistence.access.operator.DBOperatorException;

public interface UserDao {
	User findUserById(Integer userId) throws DBOperatorException;
}
