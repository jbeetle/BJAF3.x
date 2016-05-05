package demo.business;

import java.util.List;

import demo.valueobject.ExpFriend;

public interface IFriendService {
	long addFriend(ExpFriend friend) throws ServiceException;

	List<ExpFriend> queryFriendsByName(String friendName)
			throws ServiceException;
}
