package beetle.generate.genbean;

import java.util.HashMap;
import java.util.Iterator;

import beetle.generate.util.Common;

public class GenVO {
	private StringBuffer sb;
	private String packageName;
	private String className;
	private HashMap attributes;

	public GenVO(String packageName, String className, HashMap attributes) {
		this.packageName = packageName;
		this.className = className;
		this.attributes = attributes;
		this.sb = new StringBuffer();
		genClass();
	}

	public void genClassHeader() { // �����ͷ����
		sb.append("package ").append(packageName).append(".dto").append(";\n");
		sb.append("import java.sql.Date;\n");
		sb.append("import java.sql.Timestamp;\n");
		sb.append("import java.math.BigDecimal;\n");
		sb.append("\n\n");
	}

	public void genAttributes() { // ��������Բ���

		String attributeName = null;
		String attributeType = null;
		// Type type = Type.getInstance();
		Iterator it = attributes.keySet().iterator();
		sb.append("\n");
		sb.append("    private static final long serialVersionUID = 1L;\n");
		while (it.hasNext()) {
			Object obj = it.next();
			if (!(obj.toString().equals("tabName")) && !(obj.toString().equals("primaryKey"))) {
				attributeName = obj.toString().toLowerCase();
				attributeType = attributes.get(obj).toString();
				sb.append("    private ").append(attributeType).append(" ").append(attributeName).append(";");
				sb.append("\n");

			}

		}

	}

	public void genConstructor() { // ����๹�캯��
		sb.append("\n");
		sb.append("    public ").append(Common.genTableClassName(className)).append("(){");
		sb.append("\n");
		sb.append("    }");
		sb.append("\n");
	}

	public void genGetters() { // �����getter����

		String attributeName = null;
		String attributeType = null;
		// Type type = Type.getInstance();
		Iterator it = attributes.keySet().iterator();
		sb.append("\n");
		while (it.hasNext()) {
			Object obj = it.next();
			if (!(obj.toString().equals("tabName")) && !(obj.toString().equals("primaryKey"))) {
				attributeName = obj.toString();
				attributeType = attributes.get(obj).toString();
				sb.append("\n");
				sb.append("    public ").append(attributeType).append(" get")
						.append(Common.fisrtCharToUpCase3(attributeName)).append("(){");
				sb.append("\n");
				sb.append("      return this.").append(attributeName.toLowerCase()).append(";");
				sb.append("\n");
				sb.append("    }");
			}
		}

	}

	public void genSetters() { // �����setter����

		String attributeName = null;
		String attributeType = null;
		// Type type = Type.getInstance();
		Iterator it = attributes.keySet().iterator();
		sb.append("\n");
		while (it.hasNext()) {
			Object obj = it.next();
			if (!(obj.toString().equals("tabName")) && !(obj.toString().equals("primaryKey"))) {
				attributeName = obj.toString();
				attributeType = attributes.get(obj).toString();

				sb.append("\n");
				sb.append("    public void set").append(Common.fisrtCharToUpCase3(attributeName)).append("(")
						.append(attributeType).append(" ").append(attributeName.toLowerCase()).append("){");
				sb.append("\n");
				sb.append("      this.").append(attributeName.toLowerCase()).append(" = ")
						.append(attributeName.toLowerCase()).append(";");
				sb.append("\n");
				sb.append("    }");
			}
		}

	}

	public void genClassBody() { // ��������岿��
		sb.append("public class ").append(Common.genTableClassName(className))
				.append(" implements java.io.Serializable{");
		sb.append("\n");
		genAttributes();
		genConstructor();
		genGetters();
		genSetters();
		sb.append("\n");
		sb.append("}");
	}

	public void genClass() { // �������������
		genClassHeader();
		genClassBody();
	}

	public String getSb() {
		return sb.toString();
	}

}
