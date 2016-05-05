package client;

import com.beetle.framework.business.service.ServiceFactory;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.util.encrypt.AesEncrypt;

import demo.XXXApp.common.dto.Friend;
import demo.XXXApp.common.dto.User;
import demo.XXXApp.service.face.HelloServiceException;
import demo.XXXApp.service.face.IHelloService;

public class ServiceClient {
	static AppLogger logger = AppLogger.getInstance(ServiceClient.class);
	static IHelloService helloService = ServiceFactory.serviceLookup(
			IHelloService.class, true);

	public static void main(String[] args) {
		// System.out.println(helloService.hello("xx"));
		// System.out.println(helloService.hello("xx", 2222));
		try {
			System.out.println(helloService.zoro(0));
		} catch (HelloServiceException e) {
			System.out.println("xx");
			e.printStackTrace();
		}
		// System.out.println(helloService.longCall("xxxx"));
	}

	public static void main9(String[] args) {
		aa();
		for (int i = 0; i < 100; i++) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					while (true) {
						try {

							System.out.println("1-->"
									+ helloService.hello(" "
											+ Thread.currentThread().getId()));

							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		}
		for (int i = 0; i < 100; i++) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					while (true) {
						try {

							System.out.println("2-->"
									+ helloService.findUserById(1001l));

							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		}
	}

	public static void main_bak(String[] args) {
		aa();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 1024 * 1024 * 10; i++) {
			sb.append("a");
		}
		for (;;) {
			System.out.println(helloService.hello(sb.toString()));
		}
		// ServiceProxyFactory.clearAll();// 正常情况下不要调用此方法，除非你的进程关闭（释放资源）

	}

	private static void aa() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public static void main_(String[] args) {
		IHelloService hs = ServiceFactory.serviceLookup(IHelloService.class);
		System.out.println(hs.hello("hello world!"));
		User user = new User();
		user.setBirthday(new java.sql.Date(System.currentTimeMillis()));
		user.setEmail("tom@126.com");
		user.setPasswd(AesEncrypt.encrypt("123456"));
		user.setSex(1);
		user.setUserid(1002l);
		user.setUsername("Tom2");
		Friend friend = new Friend();
		friend.setAddress("shenzhen,china");
		friend.setEmail("henry@gmail.com");
		friend.setFriendid(100l);
		friend.setUserid(user.getUserid());
		friend.setFriendname("Henry");
		friend.setPhone("13501583576");
		hs.CreateUserAndFriend(user, friend);
		User user2 = hs.findUserById(1001l);
		System.out.println(user2);
	}

	public static void main_2(String[] args) {
		IHelloService hs = ServiceFactory.serviceLookup(IHelloService.class);
		for (int i = 1; i < 1000; i++) {
			User user = new User();
			user.setBirthday(new java.sql.Date(System.currentTimeMillis()));
			user.setEmail("tom@126.com");
			user.setPasswd(AesEncrypt.encrypt("123456"));
			user.setSex(1);
			user.setUserid(i);
			user.setUsername("Tom" + i);
			Friend friend = new Friend();
			friend.setAddress("shenzhen,china");
			friend.setEmail("henry@gmail.com");
			friend.setFriendid(i);
			friend.setUserid(user.getUserid());
			friend.setFriendname("Henry");
			friend.setPhone("13501583576");
			hs.CreateUserAndFriend(user, friend);
		}
		ServiceFactory.releaseRpcResources();
	}
}
