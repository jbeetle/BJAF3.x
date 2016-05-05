package test;

import com.beetle.framework.persistence.dao.DaoFactory;
import com.beetle.framework.persistence.seq.SeqOperator;
import com.beetle.framework.resource.mask.DefaultPassworkMask;
import com.beetle.framework.util.UUIDGenerator;

import demo.XXXApp.common.Const;
import demo.XXXApp.persistence.dao.IUserDao;

public class TestMe {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println(SeqOperator.nextSequenceNum(
				Const.SYSDATASOURCE_DEFAULT, "xxx_userid_seq"));
		System.out.println(UUIDGenerator.generatePrefixHostUUID());
	}

	public static void mainx(String[] args) {
		IUserDao user = (IUserDao) DaoFactory.getDaoObject("IUserDao");
		System.out.println(user.select(10000l));
	}

	public static void main_(String[] args) {
		DefaultPassworkMask mask = new DefaultPassworkMask();
		System.out.println(mask.encode("yhd@1976"));
		long userid = SeqOperator.nextSequenceNum(Const.SYSDATASOURCE_DEFAULT,
				"xxx_userid_seq");
		long friendid = SeqOperator.nextSequenceNum(
				Const.SYSDATASOURCE_DEFAULT, "xxx_friendid_seq");
	}
}
