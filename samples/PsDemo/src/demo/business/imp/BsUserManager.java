package demo.business.imp;

import java.util.List;

import com.beetle.framework.resource.dic.def.InjectField;
import com.beetle.framework.resource.dic.def.ServiceTransaction;

import demo.business.IFriendService;
import demo.business.IUserManagerService;
import demo.business.ServiceException;
import demo.business.validate.BussineValidator;
import demo.business.validate.ValidateException;
import demo.persistence.dao.IExpFriendDao;
import demo.persistence.dao.IExpUfRelationDao;
import demo.persistence.dao.IExpUserDao;
import demo.valueobject.ExpFriend;
import demo.valueobject.ExpUfRelation;
import demo.valueobject.ExpUser;

public class BsUserManager implements IUserManagerService {
	@InjectField
	private IFriendService friendSrvc;
	@InjectField
	private IExpUserDao userDao;
	@InjectField
	private IExpUfRelationDao ufRelationDao;
	@InjectField
	private IExpFriendDao friendDao;

	@ServiceTransaction
	@Override
	public long createUser(ExpUser user) throws ServiceException {
		try {
			BussineValidator.userValidate(user);
		} catch (ValidateException e) {
			throw new ServiceException(e.getErrCode(), e.getMessage());
		}
		long uid = userDao.getNextSeqVal();
		user.setUSERID(uid);
		userDao.insert(user);
		return uid;
	}

	@ServiceTransaction
	@Override
	public void createFriend(Long userId, ExpFriend friend)
			throws ServiceException {
		long fid = friendSrvc.addFriend(friend);// 此方式也声明了事务
		ExpUfRelation uf = new ExpUfRelation();
		// uf.setFriendid(10057l);
		uf.setFriendid(fid);
		uf.setUserid(userId);
		ufRelationDao.insert(uf);
	}

	@Override
	public List<ExpFriend> getFriends(Long userId) throws ServiceException {
		return ufRelationDao.queryFriendsByUserId(userId);
	}

	@ServiceTransaction
	@Override
	public void deleteFriend(long userid, long friendId)
			throws ServiceException {
		ufRelationDao.delete(friendId, userid);
		friendDao.delete(friendId);
	}

}
