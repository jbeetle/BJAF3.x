package test;

import com.beetle.framework.resource.dic.DIContainer;

import demo.business.IUserManagerService;
import demo.valueobject.ExpFriend;
import demo.valueobject.ExpUser;

public class TestMe {

	/**
	 * @param args
	 */
	public static void main_bak(String[] args) {
		IUserManagerService userSrvc = DIContainer.getInstance().retrieve(
				IUserManagerService.class);
		// userSrvc.deleteFriend(1001l, 10060l);
		System.out.println(userSrvc.getFriends(1001l));
	}

	public static void main(String[] args) {
		IUserManagerService userSrvc = DIContainer.getInstance().retrieve(
				IUserManagerService.class);
		ExpUser user = new ExpUser();
		user.setBIRTHDAY(new java.sql.Date(System.currentTimeMillis()));
		user.setEMAIL("henryyu@163.com");
		user.setPASSWD("123456");
		user.setSEX(1);
		user.setUSERNAME("yhd" + System.currentTimeMillis());
		long uid = userSrvc.createUser(user);
		ExpFriend friend = new ExpFriend();
		friend.setADDRESS("xxxx");
		friend.setEMAIL("tom@qq.com");
		friend.setFRIENDNAME("Tom Yu");
		friend.setPHONE("13567676766");
		userSrvc.createFriend(uid, friend);
		System.out.println(userSrvc.getFriends(uid));
	}
}
