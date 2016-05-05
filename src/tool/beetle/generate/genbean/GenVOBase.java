package beetle.generate.genbean;

import beetle.generate.conf.Configuration;

public class GenVOBase {
	private StringBuffer sb;

	public GenVOBase() {
		Configuration cfg = Configuration.getInstance();
		String packageName = cfg.getValue("java.package");
		sb = new StringBuffer();
		sb.append("package ").append(packageName).append(".valueobject").append(";");
		sb.append("\n\n");
		sb.append("import java.io.*;");
		sb.append("\n\n");
		sb.append("public class VOBase implements Serializable {\n");
		sb.append("    private static final long serialVersionUID = 1L;\n");
		sb.append("\n\n");
		sb.append("}");
	}

	public String getSb() {
		return sb.toString();
	}

}
