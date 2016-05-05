package demo.persistence.imp;

import java.util.List;

import com.beetle.framework.persistence.access.operator.DBOperatorException;
import com.beetle.framework.persistence.access.operator.QueryOperator;
import com.beetle.framework.persistence.access.operator.TableOperator;

import demo.persistence.dao.IExpUfRelationDao;
import demo.valueobject.ExpFriend;
import demo.valueobject.ExpUfRelation;

public class PsExpUfRelation implements IExpUfRelationDao {

	// fieldsNames:[FRIENDID,USERID"]
	private TableOperator<ExpUfRelation> operator;

	public PsExpUfRelation() {
		operator = new TableOperator<ExpUfRelation>("SYSDATASOURCE_DEFAULT",
				"EXP_UF_RELATION", ExpUfRelation.class);
		// //针对包含自增字段的表，使用下面的构造函数
		// operator = new TableOperator("SYSDATASOURCE_DEFAULT",
		// "TEST_UF_RELATION", TESTUFRELATION.class,"IDENTITY_FIELD_NAME");
	}

	public int insert(ExpUfRelation testufrelation) throws DBOperatorException {
		return operator.insert(testufrelation);
	}

	private final static String queryFriendsByUserId_sql = "Select a.FRIENDID,a.FRIENDNAME,a.PHONE,a.EMAIL,a.ADDRESS From EXP_UF_RELATION t,EXP_FRIEND a  where a.FRIENDID = t.FRIENDID and t.userid=?";

	@Override
	public List<ExpFriend> queryFriendsByUserId(long userId)
			throws DBOperatorException {
		QueryOperator q = new QueryOperator();
		q.setDataSourceName("SYSDATASOURCE_DEFAULT");
		q.setSql(queryFriendsByUserId_sql);
		q.addParameter(userId);
		q.access();
		return q.getResultList(ExpFriend.class);
	}

	@Override
	public int delete(long friendid, long userid) throws DBOperatorException {
		return this.operator.deleteByWhereCondition(
				"where FRIENDID=? and USERID=?", new Object[] { friendid,
						userid });
	}

}