package demo.persistence.imp;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import demo.valueobject.ExpUser;

public class PsTestUserTest {

	@Test
	public void test() {
		PsExpUser ps = new PsExpUser();
		ExpUser user = new ExpUser();
		user.setBIRTHDAY(new java.sql.Date(System.currentTimeMillis()));
		user.setEMAIL("yuhaodong@gmail.com");
		user.setPASSWD("888888");
		user.setSEX(-1);
		user.setUSERID(1002l);
		user.setUSERNAME("Tom");
		ps.insert(user);
		assertTrue(true);
		
	}

}
