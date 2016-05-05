package demo.business;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.beetle.framework.business.service.ServiceFactory;

import demo.valueobject.ExpUser;

public class IUserManagerServiceTest {

	@Test
	public void testCreateUser() {
		ExpUser user = new ExpUser();
		user.setBIRTHDAY(new java.sql.Date(System.currentTimeMillis()));
		user.setEMAIL("yuhaodong@gmail.com");
		user.setPASSWD("888888");
		user.setSEX(-1);
		user.setUSERNAME("余浩东");
		IUserManagerService userSrvc = ServiceFactory
				.serviceLookup(IUserManagerService.class);
		userSrvc.createUser(user);
		assertTrue(true);
	}

	public static void main(String arg[]) {
		try {
			new IUserManagerServiceTest().testCreateUser();
		} catch (ServiceException se) {
			// System.out.println(se.getErrCode());
			se.printStackTrace();
		}
	}
}
