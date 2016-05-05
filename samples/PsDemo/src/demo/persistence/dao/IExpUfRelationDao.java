package demo.persistence.dao;

import java.util.List;

import com.beetle.framework.persistence.access.operator.DBOperatorException;

import demo.valueobject.ExpFriend;
import demo.valueobject.ExpUfRelation;

public interface IExpUfRelationDao {

	List<ExpFriend> queryFriendsByUserId(long userId)
			throws DBOperatorException;

	int insert(ExpUfRelation eur) throws DBOperatorException;

	int delete(long friendid, long userid) throws DBOperatorException;

}
