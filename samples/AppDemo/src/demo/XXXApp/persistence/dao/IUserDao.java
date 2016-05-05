package demo.XXXApp.persistence.dao;

import java.util.List;

import demo.XXXApp.common.dto.User;

public interface IUserDao {
	int insert(User user);

	int update(User user);

	int delete(long id);

	User select(long id);

	List<User> searchByName(String username);

	/**
	 * 计算用户总数
	 * @return
	 */
	int count();
}
