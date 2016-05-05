package demo.business.validate;

import demo.valueobject.ExpUser;

public class BussineValidator {
	public static void userValidate(ExpUser user) throws ValidateException {
		if (user == null) {
			throw new ValidateException(-1001, "user can't be null");
		}
		if (user.getUSERNAME() == null) {
			throw new ValidateException(-1002, "username must be set");
		}
	}
}
