/*
 * BJAF - Beetle J2EE Application Framework
 * 甲壳虫J2EE企业应用开发框架
 * 版权所有2003-2015 余浩东 (www.beetlesoft.net)
 * 
 * 这是一个免费开源的软件，您必须在
 *<http://www.apache.org/licenses/LICENSE-2.0>
 *协议下合法使用、修改或重新发布。
 *
 * 感谢您使用、推广本框架，若有建议或问题，欢迎您和我联系。
 * 邮件： <yuhaodong@gmail.com/>.
 */
package com.beetle.framework.util.file;

import com.beetle.framework.util.ResourceLoader;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class XMLReader {
	public final static String convertPath(String aStr) {
		return "//".concat(aStr.replace('.', '/'));
	}

	/*
	 * Key--Value
	 */
	public static Map<String, String> getProperties(String xmlFileName,
			String itemPath, String keyName, String valueName) {
		return getProperties(xmlFileName, itemPath, "item", keyName, valueName);
	}

	public static Map<String, String> getProperties(
			InputStream xmlFileInputStream, String itemPath, String keyName,
			String valueName) {
		return getProperties(xmlFileInputStream, itemPath, "item", keyName,
				valueName);
	}

	/**
	 * 获取xml文件的document
	 * 先从目录找，没有从资源里面找
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public final static Document getXmlDoc(String filename) throws Exception {
		SAXReader reader = new SAXReader();
		File f = new File(filename);
		Document doc;
		if (f.exists()) {
			doc = reader.read(f);
		} else {
			InputStream is = ResourceLoader.getResAsStream(filename);
			doc = reader.read(is);
		}
		return doc;
	}

	/**
	 * 
	 * @param xmlFileInputStream
	 * @param itemPath
	 * @param ElementName
	 * @param keyName
	 * @param valueName
	 * @return
	 */
	public static Map<String, String> getProperties(
			InputStream xmlFileInputStream, String itemPath,
			String ElementName, String keyName, String valueName) {
		Map<String, String> map = new HashMap<String, String>();
		if (xmlFileInputStream == null) {
			// System.out.println("WARN:the resource do not exist");
			return map;
		}
		SAXReader reader = new SAXReader();
		Document doc = null;
		try {
			doc = reader.read(xmlFileInputStream);
			// Document doc = reader.read(new File(xmlFileName));
			Node node = doc.selectSingleNode(convertPath(itemPath));
			if (node != null) {
				Iterator<?> it = node.selectNodes(ElementName).iterator();
				while (it.hasNext()) {
					Element e = (Element) it.next();
					map.put(e.valueOf("@" + keyName),
							e.valueOf("@" + valueName));
				}
			} else {
				// throw new com.beetle.framework.AppRuntimeException(
				// "根据输入路径，不能找到相应数据，请检查输入路径和文件的格式是否正确!");
				// System.out.println("WARN:Can't find the paht[" + itemPath
				// + "],please check it!");
				// throw new RuntimeException("{" + itemPath +
				// "}does not exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (doc != null) {
				doc.clearContent();
			}
			reader = null;
		}
		// xmlFileInputStream.close();
		return map;
	}

	public static Map<String, String> getProperties(String xmlFileName,
			String itemPath, String ElementName, String keyName,
			String valueName) {
		Map<String, String> map = new HashMap<String, String>();
		SAXReader reader = new SAXReader();
		Document doc = null;
		try {
			//
			// InputStream
			// in=ClassLoader.getSystemResourceAsStream(xmlFileName);
			// Document doc=reader.read(in);
			//
			doc = reader.read(new File(xmlFileName));
			Node node = doc.selectSingleNode(convertPath(itemPath));
			if (node != null) {
				Iterator<?> it = node.selectNodes(ElementName).iterator();
				while (it.hasNext()) {
					Element e = (Element) it.next();
					map.put(e.valueOf("@" + keyName),
							e.valueOf("@" + valueName));
				}
			} else {
				// throw new com.beetle.framework.AppRuntimeException(
				// "根据输入路径，不能找到相应数据，请检查输入路径和文件的格式是否正确!");
				System.out.println("WARN:Can't find the paht[" + itemPath
						+ "],please check it!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (doc != null) {
				doc.clearContent();
			}
			reader = null;
		}
		return map;
	}

	public static String getTagContent(String xmlFileName, String TagPath) {
		String a = "";
		SAXReader reader = new SAXReader();
		Document doc = null;
		try {
			doc = reader.read(new File(xmlFileName));
			Node node = doc.selectSingleNode(convertPath(TagPath));
			if (node != null) {
				a = node.getText();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (doc != null) {
				doc.clearContent();
			}
			reader = null;
		}
		return a;
	}

	/**
	 * 根据输入资源流和路径获取其对应的值，如果资源不存在返回为“”字符
	 * 
	 * 
	 * @param xmlFileInputStream
	 * @param TagPath
	 * @return
	 */
	public static String getTagContent(InputStream xmlFileInputStream,
			String TagPath) {
		String a = "";
		if (xmlFileInputStream == null) {
			// System.out.println("WARN:the resource do not exist");
			return a;
		}
		SAXReader reader = new SAXReader();
		Document doc = null;
		try {
			doc = reader.read(xmlFileInputStream);
			Node node = doc.selectSingleNode(convertPath(TagPath));
			if (node != null) {
				a = node.getText();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (doc != null) {
				doc.clearContent();
			}
			reader = null;
		}
		return a;
	}

	private final static Object writeLock = new Object();

	public static void setTagContent(String xmlFileName, String TagPath,
			String value) {
		synchronized (writeLock) {
			SAXReader reader = new SAXReader();
			XMLWriter writer = null;
			try {
				Document doc = reader.read(new File(xmlFileName));
				Node node = doc.selectSingleNode(convertPath(TagPath));
				if (node != null) {
					node.setText(value);
				}
				writer = new XMLWriter(new FileWriter(xmlFileName));
				writer.write(doc);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				reader = null;
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}

	public static void setProperties(String xmlFileName, String itemPath,
			String ElementName, String keyName, String valueName, String key,
			String value) {
		// 这里锁粒度较粗， 锁最好是细化到文件，不同文件操作，并发性能不好，
		// 但是这没有所谓，因为我们这里解决的配置文件读取，频发写配置本身就是一个垃圾设计。
		synchronized (writeLock) {
			SAXReader reader = new SAXReader();
			XMLWriter writer = null;
			try {
				Document doc = reader.read(new File(xmlFileName));
				Node node = doc.selectSingleNode(convertPath(itemPath));
				if (node != null) {
					Iterator<?> it = node.selectNodes(ElementName).iterator();
					while (it.hasNext()) {
						Element e = (Element) it.next();
						if (e.attributeValue(keyName).equals(key)) {
							e.addAttribute(valueName, value);
							break;
						}
					}
				}
				writer = new XMLWriter(new FileWriter(xmlFileName));
				writer.write(doc);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
				reader = null;
			}
		}
	}

	
}
