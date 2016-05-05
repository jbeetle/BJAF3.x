package beetle.generate.conf;

import java.util.Map;

import com.beetle.framework.AppProperties;
import com.beetle.framework.util.file.XMLReader;

public class Type {

	private final Map<?, ?> typeMap;
	private static final Type type = new Type();
	private final Map<?, ?> sqltypeMap;

	private Type() {
		Configuration cfg = Configuration.getInstance();
		String dbType = cfg.getValue("db.dbType");
		String xmlpath =AppProperties.getAppHome()+ "genCodeConf.xml";
		typeMap = XMLReader.getProperties(xmlpath, "mappings." + dbType,
				"type", "java");
		sqltypeMap = XMLReader.getProperties(xmlpath, "mappings.standard",
				"javatype", "sqltype");
	}

	public static Type getInstance() {
		return type;
	}

	public String getValue(String key) {
		String a = (String) typeMap.get(key);
		if (a == null) {
			a = "String";
		}
		return a;
		// return typeMap.get(key).toString() == null ? "String" :
		// typeMap.get(key).toString();
	}

	public String getSqlType(String javaType) {

		String r = (String) sqltypeMap.get(javaType);
		if (r == null) {
			r = "unknown_type";
		}
		return r;
	}

	public static void main(String[] args) {
		Type t = new Type();
		System.out.println(t.sqltypeMap);
		System.out.println(t.typeMap);
		System.out.println(t.getValue("VARCHAR"));
	}

}
