package com.beetle.uidemo.persistence.dao;

import java.util.List;

import com.beetle.framework.persistence.access.operator.DBOperatorException;
import com.beetle.uidemo.valueobject.XxFriend;

public interface IXxFriendDao {
	XxFriend get(Long friendid) throws DBOperatorException;

	List<XxFriend> getAll() throws DBOperatorException;

	int insert(XxFriend xxFriend) throws DBOperatorException;

	int update(XxFriend xxFriend) throws DBOperatorException;

	int delete(Long friendid) throws DBOperatorException;

}
