package demo.persistence.imp;

import java.math.BigDecimal;
import java.util.List;

import com.beetle.framework.persistence.access.operator.DBOperatorException;
import com.beetle.framework.persistence.access.operator.TableOperator;
import com.beetle.framework.persistence.seq.SeqOperator;

import demo.persistence.dao.IExpUserDao;
import demo.valueobject.ExpUser;

public class PsExpUser implements IExpUserDao {

	// fieldsNames:[BIRTHDAY,SEX,PASSWD,EMAIL,USERNAME,USERID"]
	private TableOperator<ExpUser> operator;

	public PsExpUser() {
		operator = new TableOperator<ExpUser>("SYSDATASOURCE_DEFAULT",
				"EXP_USER", ExpUser.class);
	}

	public ExpUser get(BigDecimal id) throws DBOperatorException {
		return operator.selectByPrimaryKey(id);
	}

	public List<ExpUser> getAll() throws DBOperatorException {
		return operator.selectByWhereCondition("", null);
	}

	public int insert(ExpUser testuser) throws DBOperatorException {
		return operator.insert(testuser);
	}

	public int update(ExpUser testuser) throws DBOperatorException {
		return operator.update(testuser);
	}

	public int delete(BigDecimal id) throws DBOperatorException {
		return operator.deleteByPrimaryKey(id);
	}

	@Override
	public long getNextSeqVal() throws DBOperatorException {
		return SeqOperator.nextSequenceNum("SYSDATASOURCE_DEFAULT",
				"seq_exp_user");
	}

}