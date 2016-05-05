package beetle.generate;

import beetle.generate.conf.Type;
import beetle.generate.genbean.GenAll;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company: 
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class GenPersistenceCode { 
	public static void main(String[] args) {
		GenAll genAll = new GenAll();
		Type.getInstance();
		genAll.genVOs();
		genAll.genDaos();
		genAll.genImps2();
		genAll.genConfigFile();
	}
}
