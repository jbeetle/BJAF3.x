package beetle.generate.genbean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import beetle.generate.conf.Configuration;
import beetle.generate.conf.Type;
import beetle.generate.db.DBOperate;
import beetle.generate.util.Common;

public class GenImp {

	private StringBuffer sb;
	private String packageName;
	private String className;
	private String exception;
	private HashMap attributes;
	private List fields;
	private List fieldTypes;

	public GenImp(String packageName, String className, HashMap attributes) {
		this.packageName = packageName;
		this.className = className;
		this.attributes = attributes;
		this.fields = obtainFields(this.attributes);
		this.fieldTypes = obtainFieldTypes(this.attributes);
		this.sb = new StringBuffer();
		Configuration cfg = Configuration.getInstance();
		this.exception = cfg.getValue("java.exceptionName");
		genClass();
	}

	/* �����ͷ���� */
	public void genClassHeader() {
		sb.append("package ").append(packageName).append(".persistence.imp").append(";");
		sb.append("\n\n");
		sb.append("import java.util.*;");
		sb.append("\n\n");
		sb.append("import ").append(packageName).append(".dto.*;");
		sb.append("\n");
		sb.append("import ").append(packageName).append(".persistence.*;");
		sb.append("\n");
		sb.append("import ").append(packageName).append(".persistence.dao.*;");
		sb.append("\n");
		sb.append("import com.beetle.framework.persistence.db.jdbc.operator.*;");
		sb.append("\n\n");

	}

	/*
	 * public String obtainFields() { StringBuffer fields = new StringBuffer();
	 * Iterator it = attributes.keySet().iterator(); Object obj; while
	 * (it.hasNext()) { obj = it.next(); if (!(obj.toString().equals("tabName"))
	 * && !(obj.toString().equals("primaryKey"))) {
	 * fields.append(",").append(obj.toString()); } } return
	 * fields.toString().substring(1); }
	 */

	/* �ӱ�����Ϣ��map����ȡ�ֶη���list */
	public List obtainFields(HashMap hashMap) {
		List list = new ArrayList();
		Iterator it = hashMap.keySet().iterator();
		Object obj;
		while (it.hasNext()) {
			obj = it.next();
			if (!(obj.toString().equals("tabName")) && !(obj.toString().equals("primaryKey"))) {
				list.add(obj.toString());
			}
		}
		return list;
	}

	/* �ӱ�����Ϣ��map����ȡ�ֶ����ͷ���list */
	public List obtainFieldTypes(HashMap hashMap) {
		List list = new ArrayList();
		Iterator it = hashMap.keySet().iterator();
		Object obj;
		while (it.hasNext()) {
			obj = it.next();
			if (!(obj.toString().equals("tabName")) && !(obj.toString().equals("primaryKey"))) {
				list.add(hashMap.get(obj).toString());
			}
		}
		return list;
	}

	/* ���ʵ�����sql,���Ǹ���ľ�̬��Ա����,��������ɾ���顢�� */
	public void genAttributes() { //
		sb.append("\n");
		/////////////////
		/*
		 * sb.append("    private final static String ").append((className +
		 * "_TB_FIELDS").toUpperCase()).append(" = \""); for (int i = 0; i <
		 * fields.size(); i++) { sb.append(fields.get(i)); if (i < fields.size()
		 * - 1) { sb.append(","); } } sb.append("\";"); sb.append("\n");
		 */
		////////////////// query
		sb.append("    private final static String get").append(Common.fisrtCharToUpCase(className))
				.append("_SQL = \"select ");
		for (int i = 0; i < fields.size(); i++) {
			sb.append(fields.get(i));
			if (i < fields.size() - 1) {
				sb.append(",");
			}
		}
		sb.append(" from ").append(className).append(" where ").append(this.attributes.get("primaryKey"))
				.append("=?\";");
		sb.append("\n");

		////////////////// queryList
		sb.append("    private final static String get").append(Common.fisrtCharToUpCase(className))
				.append("_SQL2 = \"select ");
		for (int i = 0; i < fields.size(); i++) {
			sb.append(fields.get(i));
			if (i < fields.size() - 1) {
				sb.append(",");
			}
		}
		sb.append(" from ").append(className).append("\";");
		sb.append("\n");

		//////////////////////// insert
		sb.append("    private final static String insert").append(Common.fisrtCharToUpCase(className))
				.append("_SQL = \"insert into ").append(className).append("(");
		for (int i = 0; i < fields.size(); i++) {
			sb.append(fields.get(i));
			if (i < fields.size() - 1) {
				sb.append(",");
			}
		}
		sb.append(")values(");
		for (int i = 0; i < fields.size(); i++) {
			sb.append("?");
			if (i < fields.size() - 1) {
				sb.append(",");
			}
		}
		sb.append(")\";");
		sb.append("\n");

		//////////////////////// update
		sb.append("    private final static String update").append(Common.fisrtCharToUpCase(className))
				.append("_SQL = \"update ").append(className).append(" set ");
		for (int i = 0; i < fields.size(); i++) {
			if (!(fields.get(i).equals(attributes.get("primaryKey")))) {
				sb.append(fields.get(i)).append("=?");
				if (i < fields.size() - 1) {
					sb.append(",");
				}
			}

		}
		sb.append(" where ").append(this.attributes.get("primaryKey")).append("=?\";");
		sb.append("\n");

		//////////////////////// delete
		sb.append("    private final static String delete").append(Common.fisrtCharToUpCase(className))
				.append("_SQL = \"delete from ").append(className).append(" where ")
				.append(attributes.get("primaryKey")).append("=?\";");
		sb.append("\n");
	}

	/* ���ʵ����Ĺ��캯�� */
	public void genConstructor() {
		sb.append("\n");
		sb.append("    public Ps").append(Common.fisrtCharToUpCase(className)).append("(){");
		sb.append("\n");
		sb.append("    }");
		sb.append("\n\n");
	}

	/* ���ʵ����Ĳ�ѯ�������� */
	public void genQueryMethod() {
		Type type = Type.getInstance();
		sb.append("    public ").append(Common.fisrtCharToUpCase(className)).append(" get")
				.append(Common.fisrtCharToUpCase(className)).append("(")
				.append(attributes.get(attributes.get("primaryKey"))).append(" id) throws ").append(exception)
				.append(" {");
		sb.append("\n");
		sb.append("        ").append(Common.fisrtCharToUpCase(className)).append(" ")
				.append(Common.delAcross(className)).append(" = null;");
		sb.append("\n");
		sb.append("        ").append("QueryOperator query = new QueryOperator();");
		sb.append("\n");
		sb.append("        ").append("query.setDataSourceName(\"SYSDATASOURCE_DEFAULT\");");
		sb.append("\n");
		sb.append("        ").append("query.setSql(get").append(Common.fisrtCharToUpCase(className)).append("_SQL);");
		sb.append("\n");
		sb.append("        ").append("query.addParameter(new SqlParameter(SqlType.")
				.append(type.getSqlType(attributes.get(attributes.get("primaryKey")).toString())).append(", id));");
		sb.append("\n");
		sb.append("        ").append("try {");
		sb.append("\n");
		sb.append("            ").append("query.access();");
		sb.append("\n");
		sb.append("            ").append("RsDataSet rs = new RsDataSet(query.getSqlResultSet());");
		sb.append("\n");
		sb.append("            ").append("if (rs.rowCount > 0) {");
		sb.append("\n");
		sb.append("                ").append(Common.delAcross(className)).append(" = new ")
				.append(Common.fisrtCharToUpCase(className)).append("();");
		sb.append("\n");
		for (int i = 0; i < fields.size(); i++) {

			sb.append("                ").append(Common.delAcross(className)).append(".set")
					.append(Common.fisrtCharToUpCase(fields.get(i).toString())).append("(rs.getFieldValueAs")
					.append(fillerType(fieldTypes.get(i))).append("(\"").append(fields.get(i)).append("\"));");
			sb.append("\n");
		}
		sb.append("                ").append("rs.clearAll();");
		sb.append("\n");
		sb.append("            ").append("}");
		sb.append("\n");
		sb.append("        ").append("} catch (DBOperatorException   dbe) {");
		sb.append("\n");
		sb.append("            ").append("throw new ").append(exception).append("(dbe);");
		sb.append("\n");
		sb.append("        ").append("}");
		sb.append("\n");
		sb.append("        ").append("return ").append(Common.delAcross(className)).append(";");
		sb.append("\n");
		sb.append("    ").append("}");
		sb.append("\n\n");
	}

	/* ���ʵ����Ĳ�ѯ�б?������ */
	public void genQueryListMethod() {
		sb.append("    public List get").append(Common.fisrtCharToUpCase(className)).append("List() throws ")
				.append(exception).append(" {");
		sb.append("\n");
		sb.append("        ").append("List list = new LinkedList();");
		sb.append("\n");
		sb.append("        ").append("QueryOperator query = new QueryOperator();");
		sb.append("\n");
		sb.append("        ").append("query.setDataSourceName(\"SYSDATASOURCE_DEFAULT\");");
		sb.append("\n");
		sb.append("        ").append("query.setSql(get").append(Common.fisrtCharToUpCase(className)).append("_SQL2);");
		sb.append("\n");
		sb.append("        ").append("try {");
		sb.append("\n");
		sb.append("            ").append("query.access();");
		sb.append("\n");
		sb.append("            ").append("RsDataSet rs = new RsDataSet(query.getSqlResultSet());");
		sb.append("\n");
		sb.append("            ").append("if (rs.rowCount > 0) {");
		sb.append("\n");
		sb.append("                ").append("for (int i = 0; i < rs.rowCount; i++) {");
		sb.append("\n");
		sb.append("                    ").append(Common.fisrtCharToUpCase(className)).append(" ")
				.append(Common.delAcross(className)).append(" = new ").append(Common.fisrtCharToUpCase(className))
				.append("();");
		sb.append("\n");
		for (int i = 0; i < fields.size(); i++) {

			sb.append("                    ").append(Common.delAcross(className)).append(".set")
					.append(Common.fisrtCharToUpCase(fields.get(i).toString())).append("(rs.getFieldValueAs")
					.append(fillerType(fieldTypes.get(i))).append("(\"").append(fields.get(i)).append("\"));");
			sb.append("\n");
		}
		sb.append("                    ").append("list.add(").append(Common.delAcross(className)).append(");");
		sb.append("\n");
		sb.append("                    ").append("rs.next();");
		sb.append("\n");
		sb.append("                ").append("}");
		sb.append("\n");
		sb.append("                ").append("rs.clearAll();");
		sb.append("\n");
		sb.append("            ").append("}");
		sb.append("\n");
		sb.append("        ").append("} catch (DBOperatorException   dbe) {");
		sb.append("\n");
		sb.append("            ").append("throw new ").append(exception).append("(dbe);");
		sb.append("\n");
		sb.append("        ").append("}");
		sb.append("\n");
		sb.append("        ").append("return list;");
		sb.append("\n");
		sb.append("    ").append("}");
		sb.append("\n\n");
	}

	/* ���ʵ����Ĳ��뷽������ */
	public void genInsertMethod() {
		Type type = Type.getInstance();
		sb.append("    public void insert").append(Common.fisrtCharToUpCase(className)).append("(")
				.append(Common.fisrtCharToUpCase(className)).append(" ").append(Common.delAcross(className))
				.append(") throws ").append(exception).append(" {");
		sb.append("\n");
		sb.append("        ").append("UpdateOperator update = new UpdateOperator();");
		sb.append("\n");
		sb.append("        ").append("update.setDataSourceName(\"SYSDATASOURCE_DEFAULT\");");
		sb.append("\n");
		sb.append("        ").append("update.setSql(insert").append(Common.fisrtCharToUpCase(className))
				.append("_SQL);");
		sb.append("\n");
		for (int i = 0; i < fields.size(); i++) {
			sb.append("        ").append("update.addParameter(new SqlParameter(SqlType.")
					.append(type.getSqlType(fieldTypes.get(i).toString())).append(", ")
					.append(Common.delAcross(className)).append(".get")
					.append(Common.fisrtCharToUpCase(fields.get(i).toString())).append("()));");
			sb.append("\n");
		}
		sb.append("        ").append("try {");
		sb.append("\n");
		sb.append("            ").append("update.access();");
		sb.append("\n");
		sb.append("        ").append("} catch (DBOperatorException   dbe) {");
		sb.append("\n");
		sb.append("            ").append("throw new ").append(exception).append("(dbe);");
		sb.append("\n");
		sb.append("        ").append("}");
		sb.append("\n");
		sb.append("    ").append("}");
		sb.append("\n\n");
	}

	/* ���ʵ������޸ķ������� */
	public void genUpdateMethod() {
		Type type = Type.getInstance();
		sb.append("    public void update").append(Common.fisrtCharToUpCase(className)).append("(")
				.append(Common.fisrtCharToUpCase(className)).append(" ").append(Common.delAcross(className))
				.append(") throws ").append(exception).append(" {");
		sb.append("\n");
		sb.append("        ").append("UpdateOperator update = new UpdateOperator();");
		sb.append("\n");
		sb.append("        ").append("update.setDataSourceName(\"SYSDATASOURCE_DEFAULT\");");
		sb.append("\n");
		sb.append("        ").append("update.setSql(update").append(Common.fisrtCharToUpCase(className))
				.append("_SQL);");
		sb.append("\n");
		for (int i = 0; i < fields.size(); i++) {
			if (!(fields.get(i).equals(attributes.get("primaryKey")))) {
				sb.append("        ").append("update.addParameter(new SqlParameter(SqlType.")
						.append(type.getSqlType(fieldTypes.get(i).toString())).append(", ")
						.append(Common.delAcross(className)).append(".get")
						.append(Common.fisrtCharToUpCase(fields.get(i).toString())).append("()));");
				sb.append("\n");
			}
		}
		sb.append("        ").append("update.addParameter(new SqlParameter(SqlType.VARCHAR, ")
				.append(Common.delAcross(className)).append(".get")
				.append(Common.fisrtCharToUpCase(attributes.get("primaryKey").toString())).append("()));");
		sb.append("\n");

		sb.append("        ").append("try {");
		sb.append("\n");
		sb.append("            ").append("update.access();");
		sb.append("\n");
		sb.append("        ").append("} catch (DBOperatorException   dbe) {");
		sb.append("\n");
		sb.append("            ").append("throw new ").append(exception).append("(dbe);");
		sb.append("\n");
		sb.append("        ").append("}");
		sb.append("\n");
		sb.append("    ").append("}");
		sb.append("\n\n");
	}

	/* ���ʵ�����ɾ����� */
	public void genDeleteMethod() {
		Type type = Type.getInstance();
		sb.append("    public void delete").append(Common.fisrtCharToUpCase(className)).append("(")
				.append(attributes.get(attributes.get("primaryKey"))).append(" id) throws ").append(exception)
				.append(" {");
		sb.append("\n");
		sb.append("        ").append("UpdateOperator update = new UpdateOperator();");
		sb.append("\n");
		sb.append("        ").append("update.setDataSourceName(\"SYSDATASOURCE_DEFAULT\");");
		sb.append("\n");
		sb.append("        ").append("update.setSql(delete").append(Common.fisrtCharToUpCase(className))
				.append("_SQL);");
		sb.append("\n");
		sb.append("        ").append("update.addParameter(new SqlParameter(SqlType.")
				.append(type.getSqlType(attributes.get(attributes.get("primaryKey")).toString())).append(", id));");
		sb.append("\n");
		sb.append("        ").append("try {");
		sb.append("\n");
		sb.append("            ").append("update.access();");
		sb.append("\n");
		sb.append("        ").append("} catch (DBOperatorException   dbe) {");
		sb.append("\n");
		sb.append("            ").append("throw new ").append(exception).append("(dbe);");
		sb.append("\n");
		sb.append("        ").append("}");
		sb.append("\n");
		sb.append("    ").append("}");
		sb.append("\n\n");
	}

	public void genClassBody() {
		sb.append("public class Ps").append(Common.fisrtCharToUpCase(className)).append(" implements I")
				.append(Common.fisrtCharToUpCase(className)).append("Dao{");
		sb.append("\n");
		genAttributes();
		genConstructor();
		genQueryMethod();
		genQueryListMethod();
		genInsertMethod();
		genUpdateMethod();
		genDeleteMethod();
		sb.append("}");
	}

	public void genClass() {
		this.genClassHeader();
		this.genClassBody();
	}

	public String getSb() {
		return this.sb.toString();
	}

	private static String fillerType(Object fileType) {
		String a = fileType.toString();
		if (a.indexOf('.') == -1) {
			return a;
		} else {
			int i = a.lastIndexOf('.');
			return a.substring(i + 1);
		}
	}

	public static void main(String[] args) {
		// System.out.println(fillerType("java.sql.Date"));
		DBOperate dbOperate = new DBOperate();
		HashMap tbFields = dbOperate.getTbFields("t_survey");
		GenImp genImp = new GenImp("packageName", "t_survey", tbFields);
		System.out.println(genImp.getSb());

		/*
		 * Configuration cfg = Configuration.getInstance(); String packageName =
		 * cfg.getValue("java.package"); String tabName; String fileName; String
		 * dir = cfg.getValue("java.outPath") + "persistance\\imp\\";
		 * FileOperate f = new FileOperate(); DBOperate dbOperate = new
		 * DBOperate(); ArrayList arrayList = dbOperate.getAllTbFields(); for
		 * (int i = 0; i < arrayList.size(); i++) { System.out.println(i);
		 * HashMap tbFields = (HashMap) arrayList.get(i); tabName =
		 * tbFields.get("tabName").toString(); GenImp genImp = new
		 * GenImp(packageName, tabName, tbFields);
		 * 
		 * fileName = "Ps" + Common.fisrtCharToUpCase(tabName) + ".java";
		 * f.buildFile(genImp.getSb(), dir, fileName);
		 * System.out.println(genImp.getSb()); }
		 */

	}
}
