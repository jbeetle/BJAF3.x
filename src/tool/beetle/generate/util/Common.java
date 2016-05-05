package beetle.generate.util;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import beetle.generate.conf.Configuration;

public class Common {
	public static String genTableClassName(String className) {
		String ff = Configuration.getInstance().getValue("vo.tb.prefix");
		if (ff != null && ff.equalsIgnoreCase("true")) {
			return "Tb" + Common.fisrtCharToUpCase(className);
		}
		return Common.fisrtCharToUpCase(className);
	}

	public static ArrayList rsToArrayList(ResultSet rs) {
		ArrayList rows = new ArrayList();
		try {
			java.sql.ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				HashMap row = new HashMap();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {

					String cname = rsmd.getColumnName(i);
					String cvalue = rs.getString(i);
					row.put(cname, cvalue);
					// System.out.println(cname+"  "+cvalue);
				}
				rows.add(row);
			}

		} catch (Exception e) {

		}

		return rows;
	}

	public static String fisrtCharToUpCase2(String s) {
		if (s != null && s.indexOf("_") >= 0) {
			s = s.replaceAll("_", "");
		}

		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}

	public static String fisrtCharToLowerCase(String s) {
		s = s.toLowerCase();
		StringTokenizer st = new StringTokenizer(s, "_");
		StringBuffer sb = new StringBuffer();
		while (st.hasMoreTokens()) {
			String a = st.nextToken();
			a = a.substring(0, 1).toUpperCase() + a.substring(1).toLowerCase();
			sb.append(a);
		}
		st = null;
		String t = sb.toString();
		t = t.substring(0, 1).toLowerCase() + t.substring(1);
		return t;
	}

	public static String fisrtCharToLowerCase2(String s) {
		s = s.substring(0, 1).toLowerCase() + s.substring(1);
		return s;
	}

	public static String fisrtCharToUpCase3(String s) {
		s = s.substring(0, 1).toUpperCase() + s.substring(1);
		return s;
	}

	public static String fisrtCharToUpCase(String s) {
		// s = s.toLowerCase();
		StringTokenizer st = new StringTokenizer(s, "_");
		StringBuffer sb = new StringBuffer();
		while (st.hasMoreTokens()) {
			String a = st.nextToken();
			a = a.substring(0, 1).toUpperCase() + a.substring(1);
			sb.append(a);
		}
		st = null;
		return sb.toString();
	}

	public static String delAcross(String s) {
		if (s != null && s.indexOf("_") >= 0) {
			s = s.replaceAll("_", "");
		}
		return s;
	}

	public static String CompressType(String javaType) {
		return javaType.substring(javaType.lastIndexOf(".") + 1);
	}

	public static void printContent(List list) {

		Map row = new HashMap();
		for (int i = 0; i < list.size(); i++) {
			row = (HashMap) list.get(i);
			Iterator it = row.keySet().iterator();
			while (it.hasNext()) {
				Object obj = it.next();
				System.out.print(obj + "(" + row.get(obj) + "),");
			}
			System.out.println("");
		}

	}

	public static void printContent(HashMap hashMap) {

		Iterator it = hashMap.keySet().iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			System.out.print(obj + "(" + hashMap.get(obj) + "),");
		}
		System.out.println("");

	}

	public static void main(String[] args) {
		System.out.println(fisrtCharToLowerCase("ac_Account_flag"));
	}

}
