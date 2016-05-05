package beetle.generate.genbean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import beetle.generate.conf.Configuration;
import beetle.generate.conf.Type;
import beetle.generate.db.DBOperate;
import beetle.generate.util.Common;

public class GenImp2 {

	private StringBuffer sb;
	private String packageName;
	private String className; // tname
	private String exception;
	private HashMap attributes;
	private List fields;
	private List fieldTypes;

	public GenImp2(String packageName, String className, HashMap attributes) {
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
		sb.append("import java.util.*;\n");
		sb.append("import java.sql.Date;\n");
		sb.append("import java.sql.Timestamp;\n");
		sb.append("\n\n");
		sb.append("import com.beetle.framework.persistence.access.operator.DBOperatorException;");
		sb.append("\n\n");
		sb.append("import ").append(packageName).append(".dto.*;");
		sb.append("\n");
		sb.append("import ").append(packageName).append(".persistence.*;");
		sb.append("\n");
		//sb.append("import ").append(packageName).append(".persistence.dao.*;");
		//sb.append("\n");
		sb.append("import com.beetle.framework.persistence.access.operator.*;");
		// sb.append("\n");
		// sb.append("import com.beetle.framework.persistence.db.orm.*;");
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
		// ///////////////
		sb.append("    //fieldsNames:[");
		for (int i = 0; i < fields.size(); i++) {
			sb.append(fields.get(i));
			if (i < fields.size() - 1) {
				sb.append(",");
			}
		}
		sb.append("\"]");
		sb.append("\n");
		sb.append("    private TableOperator<" + Common.genTableClassName(className) + "> operator;");
		sb.append("\n");
	}

	/* ���ʵ����Ĺ��캯�� */
	public void genConstructor() {
		sb.append("\n");
		sb.append("    public ").append(Common.fisrtCharToUpCase(className)+"Impl").append("(){");
		sb.append("\n");
		sb.append("        operator = new TableOperator<" + Common.genTableClassName(className)
				+ ">(\"SYSDATASOURCE_DEFAULT\", \"" + className + "\", " + Common.genTableClassName(className)
				+ ".class);");
		sb.append("\n");
		//
		sb.append("     ////针对包含自增字段的表，使用下面的构造函数\n");
		sb.append("        //operator = new TableOperator(\"SYSDATASOURCE_DEFAULT\", \"" + className + "\", "
				+ Common.fisrtCharToUpCase(className) + ".class,\"IDENTITY_FIELD_NAME\");");
		sb.append("\n");

		//
		sb.append("    }");
		sb.append("\n\n");
	}

	/* ���ʵ����Ĳ�ѯ�������� */
	public void genQueryMethod() {
		sb.append("    public ").append(Common.genTableClassName(className)).append(" get").append("(")
				.append(attributes.get(attributes.get("primaryKey"))).append(" id) throws DBOperatorException{");
		sb.append("\n");
		sb.append("		return operator.selectByPrimaryKey(id);");
		sb.append("\n");
		sb.append("    ").append("}");
		sb.append("\n\n");
	}

	/* ���ʵ����Ĳ�ѯ�б?������ */
	public void genQueryListMethod() {
		sb.append("    public List<" + Common.genTableClassName(className) + "> getAll")
				.append("()throws DBOperatorException{");
		sb.append("\n");
		sb.append("		return operator.selectByWhereCondition(\"\", null);");
		sb.append("\n");
		sb.append("     ").append("}");
		sb.append("\n\n");
	}

	/* ���ʵ����Ĳ��뷽������ */
	public void genInsertMethod() {
		sb.append("    public int insert").append("(").append(Common.genTableClassName(className)).append(" ")
				.append(Common.delAcross(className.toLowerCase())).append(") throws DBOperatorException {");
		sb.append("\n");
		sb.append("		return operator.insert(" + Common.delAcross(className.toLowerCase()) + ");");
		sb.append("\n");
		sb.append("    ").append("}");
		sb.append("\n");
	}

	/* ���ʵ������޸ķ������� */
	public void genUpdateMethod() {
		sb.append("    public int update").append("(").append(Common.genTableClassName(className)).append(" ")
				.append(Common.delAcross(className.toLowerCase())).append(") throws DBOperatorException{");
		sb.append("\n");
		sb.append("		return operator.update(" + Common.delAcross(className.toLowerCase()) + ");");
		sb.append("\n");
		sb.append("    ").append("}");
		sb.append("\n");
	}

	/* ���ʵ�����ɾ����� */
	public void genDeleteMethod() {
		sb.append("    public int delete").append("(").append(attributes.get(attributes.get("primaryKey")))
				.append(" id) throws DBOperatorException{");
		sb.append("\n");
		sb.append("		return operator.deleteByPrimaryKey(id);");
		sb.append("\n");
		sb.append("    ").append("}");
		sb.append("\n");
	}

	public void genClassBody() {
		sb.append("public class ").append(Common.fisrtCharToUpCase(className)+"Impl").append(" implements ")
				.append(Common.fisrtCharToUpCase(className)).append("Dao{");
		sb.append("\n");
		genAttributes();
		genConstructor();
		genQueryMethod();
		genQueryListMethod();
		genInsertMethod();
		genUpdateMethod();
		genDeleteMethod();
		sb.append("/*");
		sb.append("\n");
		sb.append("也可以使用QueryOperator查询器进行，例如根据主键查找此记录，实现如下：\n（一般地，我们复杂查询操作使用QueryOperator比较方便）");
		sb.append("\n");
		genQueryMethod_demo();
		sb.append("*/");
		sb.append("\n");
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

	/* ���ʵ����Ĳ�ѯ�������� */

	public void genQueryMethod_demo() {
		Type type = Type.getInstance();
		sb.append("    public ").append(Common.genTableClassName(className)).append(" get")
				.append(Common.fisrtCharToUpCase(className)).append("(")
				.append(attributes.get(attributes.get("primaryKey"))).append(" id)throws DBOperatorException {");
		sb.append("\n");
		sb.append("        ").append(Common.genTableClassName(className)).append(" ")
				.append(Common.delAcross(className)).append(" = null;");
		sb.append("\n");
		sb.append("        ").append("QueryOperator query = new QueryOperator();");
		sb.append("\n");
		sb.append("        ").append("query.setDataSourceName(\"SYSDATASOURCE_DEFAULT\");");
		sb.append("\n");
		sb.append("        ").append(
				"query.setSql(\"select \"+operator.generateFieldsString()+\" from \"+operator.getTableName());");
		sb.append("\n");
		sb.append("        ").append("query.addParameter(new SqlParameter(SqlType.")
				.append(type.getSqlType(attributes.get(attributes.get("primaryKey")).toString())).append(", id));");
		sb.append("\n");
		sb.append("            ").append("query.access();");
		sb.append("\n");
		sb.append("            ").append("RsDataSet rs = new RsDataSet(query.getSqlResultSet());");
		sb.append("\n");
		sb.append("            ").append("if (rs.rowCount > 0) {");
		sb.append("\n");
		sb.append("                ").append(Common.delAcross(className)).append(" = new ")
				.append(Common.genTableClassName(className)).append("();");
		sb.append("\n");
		for (int i = 0; i < fields.size(); i++) {

			sb.append("                ").append(Common.delAcross(className)).append(".set")
					.append(Common.fisrtCharToUpCase3(fields.get(i).toString())).append("(rs.getFieldValueAs")
					.append(fillerType(fieldTypes.get(i))).append("(\"").append(fields.get(i)).append("\"));");
			sb.append("\n");
		}
		sb.append("                ").append("rs.clearAll();");
		sb.append("\n");
		sb.append("            ").append("}");
		sb.append("\n");
		sb.append("        ").append("return ").append(Common.delAcross(className)).append(";");
		sb.append("\n");
		sb.append("    ").append("}");
		sb.append("\n\n");
	}

	public static void main(String[] args) {
		// System.out.println(fillerType("java.sql.Date"));
		DBOperate dbOperate = new DBOperate();
		HashMap tbFields = dbOperate.getTbFields("t_survey");
		GenImp2 genImp = new GenImp2("packageName", "t_survey", tbFields);
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
