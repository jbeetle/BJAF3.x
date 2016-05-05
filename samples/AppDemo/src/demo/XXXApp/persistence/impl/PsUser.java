package demo.XXXApp.persistence.impl;

import java.util.List;

import com.beetle.framework.persistence.access.operator.QueryOperator;
import com.beetle.framework.persistence.access.operator.RsDataSet;
import com.beetle.framework.persistence.access.operator.TableOperator;

import demo.XXXApp.common.Const;
import demo.XXXApp.common.dto.User;
import demo.XXXApp.persistence.dao.IUserDao;

public class PsUser implements IUserDao {
	private TableOperator<User> table;

	public PsUser() {
		this.table = new TableOperator<User>(Const.SYSDATASOURCE_DEFAULT,
				"xx_user", User.class);
	}

	@Override
	public int insert(User user) {
		return table.insert(user);
	}

	@Override
	public int update(User user) {
		return table.update(user);
	}

	@Override
	public int delete(long id) {
		return table.deleteByPrimaryKey(id);
	}

	@Override
	public User select(long id) {
		return table.selectByPrimaryKey(id);
	}

	@Override
	public List<User> searchByName(String username) {
		return table.selectByWhereCondition("where username like ?",
				new Object[] { "%" + username + "%" });
	}

	private static final String count_sql = "select count(*) from xx_User ";

	@Override
	public int count() {
		QueryOperator query = new QueryOperator();
		query.setDataSourceName(Const.SYSDATASOURCE_DEFAULT);
		query.setSql(count_sql);
		query.access();
		RsDataSet rs = new RsDataSet(query.getSqlResultSet());
		int i = rs.getFieldValueAsInteger(0);
		rs.clearAll();
		return i;
	}
}
