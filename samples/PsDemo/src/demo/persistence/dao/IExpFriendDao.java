package demo.persistence.dao;

import java.util.List;

import com.beetle.framework.persistence.access.operator.DBOperatorException;

import demo.valueobject.ExpFriend;

public interface IExpFriendDao {
	ExpFriend get(long friendid) throws DBOperatorException;

	List<ExpFriend> getAll() throws DBOperatorException;

	int insert(ExpFriend tESTFRIEND) throws DBOperatorException;

	int update(ExpFriend tESTFRIEND) throws DBOperatorException;

	int delete(long friendid) throws DBOperatorException;

	long getNextSeqVal() throws DBOperatorException;

	List<ExpFriend> queryByName(String name) throws DBOperatorException;
}
