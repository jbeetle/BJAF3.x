package com.beetle.dwzdemo.persistence.dao;

import java.util.List;

import com.beetle.dwzdemo.valueobject.XxFriend;
import com.beetle.framework.persistence.access.operator.DBOperatorException;

public interface IXxFriendDao{
    XxFriend get(Long friendid)throws DBOperatorException;
    List<XxFriend> getAll()throws DBOperatorException;
    int insert(XxFriend xxFriend)throws DBOperatorException;
    int update(XxFriend xxFriend)throws DBOperatorException;
    int delete(Long friendid)throws DBOperatorException;

}
