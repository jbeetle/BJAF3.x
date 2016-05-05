package demo.XXXApp.service.impl;

import com.beetle.framework.appsrv.plugin.LoaderVO;
import com.beetle.framework.appsrv.plugin.PluginManager;
import com.beetle.framework.resource.dic.def.ServiceTransaction;

import demo.XXXApp.common.dto.Friend;
import demo.XXXApp.common.dto.User;
import demo.XXXApp.plugin.IXXXPlugin;
import demo.XXXApp.service.face.HelloServiceException;
import demo.XXXApp.service.face.IHelloService;

public class HelloServiceImpl extends DaoService implements IHelloService {
	@Override
	public String hello(String word) {
		return "echo:{" + word + "}";
	}

	@Override
	public User findUserById(long id) {
		return userDao.select(id);
	}

	@ServiceTransaction
	@Override
	public void CreateUserAndFriend(User user, Friend friend) {
		friend.setUserid(user.getUserid());
		userDao.insert(user);// 插入用户表
		// System.out.println("user insert ok");
		friendDao.insert(friend);// 插入朋友表
	}

	@Override
	public void callPluginDemo(String moduleId) {
		PluginManager pm = PluginManager.getInstance();
		String jarname = pm.getPluginProperty(moduleId);
		LoaderVO lvo = pm.getLoaderVO(jarname);
		System.out.println("-->");
		System.out.println(lvo);
		IXXXPlugin xp = (IXXXPlugin) lvo.getHandler();
		xp.deal("hi,plugin!");
	}

	@Override
	public String hello(String word, int x) {
		return "echo:{" + word + "," + x + "}";
	}

	@Override
	public long longCall(String word) {
		long x = System.currentTimeMillis();
		System.out.println("word:" + word);
		try {
			Thread.sleep(1000 * 15);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return System.currentTimeMillis() - x;
	}

	@Override
	public int zoro(int x) throws HelloServiceException {
		try {
			return 10 / x;
		} catch (Exception e) {
			throw new HelloServiceException(e);
		}
	}

}
