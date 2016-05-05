/*
 * BJAF - Beetle J2EE Application Framework
 * �׿ǳ�J2EE��ҵӦ�ÿ������
 * ��Ȩ����2003-2009 ��ƶ� (www.beetlesoft.net)
 * 
 * ����һ����ѿ�Դ�������������ڡ��׿ǳ�J2EEӦ�ÿ�������ȨЭ�顷
 *
 *   ��GNU Lesser General Public License v3.0��
 *<http://www.gnu.org/licenses/lgpl-3.0.txt/>�ºϷ�ʹ�á��޸Ļ����·�����
 *
 * ��л��ʹ�á��ƹ㱾��ܣ����н�������⣬��ӭ�������ϵ��
 * �ʼ��� <yuhaodong@gmail.com/>.
 */
package com.beetle.framework.util.file;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class IniFile {

	public IniFile() {

	}

	public boolean open(String inifilename) {
		file = new File(inifilename);
		if (!file.exists()) {
			return false;
		}
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			ini = new Properties();
			ini.load(is);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private Properties ini = null;
	private File file;
	private boolean saveflag = false;

	public Properties getIniProperties() {
		return ini;
	}

	public String getValue(String key) {
		if (!ini.containsKey(key)) {
			return "";
		}
		return ini.getProperty(key);
	}

	public void setValue(String key, String value) {
		ini.setProperty(key, value);
		this.saveflag = true;
	}

	@SuppressWarnings("rawtypes")
	public void close() {
		if (!saveflag) {
			if (ini != null) {
				ini.clear();
			}
		} else {
			try {
				FileWriter fw = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(fw);
				Set s = ini.entrySet();
				Iterator it = s.iterator();
				while (it.hasNext()) {
					Map.Entry kv = (Map.Entry) it.next();
					bw.write(kv.getKey() + "=" + kv.getValue());
					bw.newLine();
				}
				bw.close();
				fw.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				ini.clear();
			}
		}
	}
}
