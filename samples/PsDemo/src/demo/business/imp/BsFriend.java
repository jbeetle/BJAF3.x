package demo.business.imp;

import java.util.List;

import com.beetle.framework.resource.dic.def.DaoField;
import com.beetle.framework.resource.dic.def.ServiceTransaction;

import demo.business.IFriendService;
import demo.business.ServiceException;
import demo.persistence.dao.IExpFriendDao;
import demo.valueobject.ExpFriend;

public class BsFriend implements IFriendService {
	@DaoField
	private IExpFriendDao friendDao;

	@ServiceTransaction
	// @ServiceTransaction(manner = ServiceTransaction.Manner.REQUIRES_NEW)
	@Override
	public long addFriend(ExpFriend friend) throws ServiceException {
		long fid = friendDao.getNextSeqVal();
		friend.setFRIENDID(fid);
		friendDao.insert(friend);
		return friend.getFRIENDID();
	}

	@Override
	public List<ExpFriend> queryFriendsByName(String friendName) throws ServiceException {
		return friendDao.queryByName(friendName);
	}

}
