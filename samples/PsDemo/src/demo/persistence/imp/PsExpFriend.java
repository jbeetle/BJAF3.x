package demo.persistence.imp;

import java.util.List;

import com.beetle.framework.persistence.access.operator.DBOperatorException;
import com.beetle.framework.persistence.access.operator.TableOperator;
import com.beetle.framework.persistence.seq.SeqOperator;

import demo.persistence.dao.IExpFriendDao;
import demo.valueobject.ExpFriend;

public class PsExpFriend implements IExpFriendDao {

	// fieldsNames:[PHONE,FRIENDID,ADDRESS,EMAIL,FRIENDNAME"]
	private TableOperator<ExpFriend> operator;

	public PsExpFriend() {
		operator = new TableOperator<ExpFriend>("SYSDATASOURCE_DEFAULT",
				"EXP_FRIEND", ExpFriend.class);
		// //针对包含自增字段的表，使用下面的构造函数
		// operator = new TableOperator("SYSDATASOURCE_DEFAULT", "TEST_FRIEND",
		// TESTFRIEND.class,"IDENTITY_FIELD_NAME");
	}

	public ExpFriend get(long id) throws DBOperatorException {
		return operator.selectByPrimaryKey(id);
	}

	public List<ExpFriend> getAll() throws DBOperatorException {
		return operator.selectByWhereCondition("", null);
	}

	public int insert(ExpFriend testfriend) throws DBOperatorException {
		return operator.insert(testfriend);
	}

	public int update(ExpFriend testfriend) throws DBOperatorException {
		return operator.update(testfriend);
	}

	public int delete(long id) throws DBOperatorException {
		return operator.deleteByPrimaryKey(id);
	}

	/*
	 * 也可以使用QueryOperator查询器进行，例如根据主键查找此记录，实现如下：
	 * （一般地，我们复杂查询操作使用QueryOperator比较方便） public TESTFRIEND
	 * getTESTFRIEND(BigDecimal id)throws DBOperatorException { TESTFRIEND
	 * TESTFRIEND = null; QueryOperator query = new QueryOperator();
	 * query.setDataSourceName("SYSDATASOURCE_DEFAULT");
	 * query.setSql("select "+operator
	 * .generateFieldsString()+" from "+operator.getTableName());
	 * query.addParameter(new SqlParameter(SqlType.unknown_type, id));
	 * query.access(); RsDataSet rs = new RsDataSet(query.getSqlResultSet()); if
	 * (rs.rowCount > 0) { TESTFRIEND = new TESTFRIEND();
	 * TESTFRIEND.setPHONE(rs.getFieldValueAsString("PHONE"));
	 * TESTFRIEND.setFRIENDID(rs.getFieldValueAsBigDecimal("FRIENDID"));
	 * TESTFRIEND.setADDRESS(rs.getFieldValueAsString("ADDRESS"));
	 * TESTFRIEND.setEMAIL(rs.getFieldValueAsString("EMAIL"));
	 * TESTFRIEND.setFRIENDNAME(rs.getFieldValueAsString("FRIENDNAME"));
	 * rs.clearAll(); } return TESTFRIEND; }
	 */

	@Override
	public long getNextSeqVal() throws DBOperatorException {
		return SeqOperator.nextSequenceNum("SYSDATASOURCE_DEFAULT",
				"seq_exp_friend");
	}

	@Override
	public List<ExpFriend> queryByName(String name) throws DBOperatorException {
		return operator.selectByWhereCondition("where FRIENDNAME like ? ",
				new Object[] { "%" + name + "%" });
	}
}