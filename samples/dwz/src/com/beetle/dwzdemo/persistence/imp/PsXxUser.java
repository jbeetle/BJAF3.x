package com.beetle.dwzdemo.persistence.imp;

import java.util.List;

import com.beetle.dwzdemo.persistence.dao.IXxUserDao;
import com.beetle.dwzdemo.valueobject.XxUser;
import com.beetle.framework.persistence.access.operator.DBOperatorException;
import com.beetle.framework.persistence.access.operator.SqlParameter;
import com.beetle.framework.persistence.access.operator.TableOperator;
import com.beetle.framework.persistence.pagination.PageParameter;
import com.beetle.framework.persistence.pagination.PageResult;
import com.beetle.framework.persistence.pagination.PaginationOperator;
import com.beetle.framework.persistence.seq.SeqOperator;
import com.beetle.framework.resource.define.PageList;

public class PsXxUser implements IXxUserDao {

	// fieldsNames:[birthday,passwd,sex,username,email,userid"]
	private TableOperator<XxUser> tot;

	public PsXxUser() {
		tot = new TableOperator<XxUser>("SYSDATASOURCE_DEFAULT", "xx_user",
				XxUser.class);
		// //针对包含自增字段的表，使用下面的构造函数
		// operator = new TableOperator("SYSDATASOURCE_DEFAULT", "xx_user",
		// XxUser.class,"IDENTITY_FIELD_NAME");
	}

	public XxUser get(Long id) throws DBOperatorException {
		return tot.selectByPrimaryKey(id);
	}

	public List<XxUser> getAll() throws DBOperatorException {
		return tot.selectByWhereCondition("", null);
	}

	public int insert(XxUser xxuser) throws DBOperatorException {
		return tot.insert(xxuser);
	}

	public int update(XxUser xxuser) throws DBOperatorException {
		return tot.update(xxuser);
	}

	public int delete(Long id) throws DBOperatorException {
		return tot.deleteByPrimaryKey(id);
	}

	@Override
	public PageList<XxUser> getAllUserByPage(int pageNumber, int pageSize,
			String orderField, String orderArith) throws DBOperatorException {
		PageParameter pp = new PageParameter();
		pp.setDataSourceName("SYSDATASOURCE_DEFAULT");
		pp.setCacheRecordAmountFlag(false);
		pp.setPageNumber(pageNumber);
		pp.setPageSize(pageSize);
		pp.setUserSql("SELECT " + tot.generateFieldsString()
				+ " FROM xx_user ORDER BY " + orderField + " " + orderArith);
		// pp.addParameter(new SqlParameter(orderField));
		// pp.addParameter(new SqlParameter(orderArith));
		PageResult pr = PaginationOperator.access(pp);
		try {
			return pr.getPageList(XxUser.class);
		} finally {
			pr.clearAll();
		}
	}

	@Override
	public long generateUserId() throws DBOperatorException {
		return SeqOperator.nextSequenceNum("SYSDATASOURCE_DEFAULT", "user");
	}

	@Override
	public PageList<XxUser> findByName(String username, int pageNumber,
			int pageSize, String orderField, String orderArith)
			throws DBOperatorException {
		PageParameter pp = new PageParameter();
		pp.setDataSourceName("SYSDATASOURCE_DEFAULT");
		pp.setCacheRecordAmountFlag(false);
		pp.setPageNumber(pageNumber);
		pp.setPageSize(pageSize);
		pp.setUserSql("SELECT " + tot.generateFieldsString()
				+ " FROM xx_user where username like ?  ORDER BY " + orderField
				+ " " + orderArith);
		pp.addParameter(new SqlParameter("%" + username + "%"));
		PageResult pr = PaginationOperator.access(pp);
		try {
			return pr.getPageList(XxUser.class);
		} finally {
			pr.clearAll();
		}
	}

}