package demo.persistence.dao;

import java.math.BigDecimal;
import java.util.List;

import com.beetle.framework.persistence.access.operator.DBOperatorException;

import demo.valueobject.ExpUser;

public interface IExpUserDao {
	ExpUser get(BigDecimal userid) throws DBOperatorException;

	List<ExpUser> getAll() throws DBOperatorException;

	int insert(ExpUser tESTUSER) throws DBOperatorException;

	int update(ExpUser tESTUSER) throws DBOperatorException;

	int delete(BigDecimal userid) throws DBOperatorException;

	long getNextSeqVal() throws DBOperatorException;
}
