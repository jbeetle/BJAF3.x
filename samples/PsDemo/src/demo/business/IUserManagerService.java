package demo.business;

import java.util.List;

import demo.valueobject.ExpFriend;
import demo.valueobject.ExpUser;

public interface IUserManagerService {
	long createUser(ExpUser user) throws ServiceException;

	void createFriend(Long userId, ExpFriend friend) throws ServiceException;

	List<ExpFriend> getFriends(Long userId) throws ServiceException;

	void deleteFriend(long userid, long friendId) throws ServiceException;
}
