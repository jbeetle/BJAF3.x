package demo.XXXApp.service.impl;

import com.beetle.framework.persistence.dao.DaoFactory;

import demo.XXXApp.persistence.dao.IFriendDao;
import demo.XXXApp.persistence.dao.IUserDao;
import demo.XXXApp.persistence.impl.PsFriend;
import demo.XXXApp.persistence.impl.PsUser;

public class DaoService {
	protected static final IUserDao userDao = DaoFactory
			.getDaoObject(PsUser.class);
	protected static final IFriendDao friendDao = DaoFactory
			.getDaoObject(PsFriend.class);
}
