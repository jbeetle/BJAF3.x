package demo.XXXApp.service.face;

import demo.XXXApp.common.dto.Friend;
import demo.XXXApp.common.dto.User;

public interface IHelloService {
	String hello(String word);

	String hello(String word, int x);

	User findUserById(long id);

	void CreateUserAndFriend(User user, Friend friend);

	void callPluginDemo(String moduleId);

	long longCall(String word);

	int zoro(int x) throws HelloServiceException;
}
