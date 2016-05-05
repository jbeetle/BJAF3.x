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
package com.beetle.framework.resource.container;

/**
 * <p>Title: 框架设计</p>
 * <p>Description: 系统环境JNDI管理类</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: 甲壳虫科技</p>
 * @author 余浩东

 * @version 1.0
 */

import com.beetle.framework.AppContext;
import com.beetle.framework.AppProperties;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.resource.define.CfgFileInfo;
import com.beetle.framework.util.OtherUtil;
import com.beetle.framework.util.ResourceLoader;
import com.beetle.framework.util.file.XMLReader;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ContainerConfig {
    /**
     * 根据属性id返回属性值
     *
     * @param 属性id
     * @return 属性值
     */
    private final static Map<String, Map<?, ?>> containerTable = new HashMap<String, Map<?, ?>>();

    private static String sysconfigFileName = AppProperties.getAppHome()
            + "ContainerConfig.xml";

    /**
     * 获取容器相应的值
     *
     * @param tagname --容器名称
     * @param key     --值的名称
     * @return
     */
    public static String getContainValue(String tagname, String key) {
        if (!containerTable.containsKey(tagname)) {
            loadContainerTable(tagname);
        }
        Map<?, ?> m = (Map<?, ?>) containerTable.get(tagname);
        if (m == null) {
            return null;
        }
        return (String) m.get(key);
    }

    /**
     * 获取容器定义的组的值
     *
     * @param tagname --容器名称
     * @return
     */
    public static String getGroupNames(String tagname) {
        return getContainValue(tagname, "GROUP_NAMES");
    }

    public static void manualSetSysConfigFileName(String filename) {
        sysconfigFileName = filename;
    }

    /**
     * 设置容器属性值
     *
     * @param tagname --容器名称
     * @param key     --属性名称
     * @throws Exception
     */
    public static void setContainValue(String tagname, String key, String value)
            throws Exception {
        Document doc = XMLReader.getXmlDoc(sysconfigFileName);
        Node node = doc.selectSingleNode(XMLReader.convertPath(tagname));
        if (node != null) {
            @SuppressWarnings("rawtypes")
            Iterator it = node.selectNodes("item").iterator();
            while (it.hasNext()) {
                Element e = (Element) it.next();
                String id = e.valueOf("@name");
                if (id != null && id.equals(key)) {
                    e.addAttribute("value", value);
                    break;
                }
            }
        }
        File f = new File(sysconfigFileName);
        if (f.exists()) {
            OutputFormat format = OutputFormat.createPrettyPrint();
            FileOutputStream fos = new FileOutputStream(f);
            XMLWriter writer = new XMLWriter(fos, format);
            writer.write(doc);
            writer.close();
        } else {
            AppLogger.getInstance(ContainerConfig.class).error(
                    "不支持jar包内xml文件修改");
        }
    }

    private static void markCfgInfo(File f, String filename) {
        String smfn = OtherUtil.removePath(filename);
        AppContext ctx = AppContext.getInstance();
        try {
            if (ctx.lookup(smfn) == null) {
                CfgFileInfo cfi = new CfgFileInfo();
                cfi.setFilename(smfn);
                cfi.setLastFileModifiedTime(f.lastModified());
                cfi.setLastReadTime(System.currentTimeMillis());
                cfi.setModifyCount(0);
                cfi.setPath(filename);
                ctx.bind(smfn, cfi);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重置此文件
     */
    public static void resetSysConfig() {
        synchronized (containerTable) {
            containerTable.clear();
            List<String> l = getAllTagNameOfFile();
            for (int i = 0; i < l.size(); i++) {
                String s = (String) l.get(i);
                loadContainerTable(s);
            }
            l.clear();
        }
    }

    /**
     * 获取此配置文件所有的标签名称
     *
     * @return
     */
    public static List<String> getAllTagNameOfFile() {
        List<String> names = new ArrayList<String>();
        Document doc;
        try {
            doc = XMLReader.getXmlDoc(sysconfigFileName);
        } catch (Exception e) {
            try {
                doc = XMLReader.getXmlDoc("SysConfig.xml");
            } catch (Exception e1) {
                doc = null;
                e1.printStackTrace();
            }
        }
        if (doc != null) {
            List<?> nodeList = doc.selectNodes("/Config/Containers/*");
            for (int i = 0; i < nodeList.size(); i++) {
                Node node = (Node) nodeList.get(i);
                names.add(node.getName());
            }
            nodeList.clear();
        }
        return names;
    }

    private static synchronized void loadContainerTable(String tagName) {
        Map<?, ?> m = null;
        File f;
        File f2;
        f = new File(sysconfigFileName);
        if (f.exists()) {
            markCfgInfo(f, sysconfigFileName);
            m = XMLReader.getProperties(sysconfigFileName,
                    "Config.Containers." + tagName, "name", "value");
            AppLogger.getInstance(ContainerConfig.class).info(
                    "from file:[" + f.getPath() + "]");
        } else {
            f2 = new File("SysConfig.xml");
            if (f2.exists()) {// 支持没有config的相对路径

                markCfgInfo(f2, "SysConfig.xml");
                m = XMLReader.getProperties("SysConfig.xml",
                        "Config.Containers." + tagName, "name", "value");
                AppLogger.getInstance(ContainerConfig.class).info(
                        "from file:[" + f2.getPath() + "]");
            } else {
                try {
                    m = XMLReader.getProperties(
                            ResourceLoader.getResAsStream(sysconfigFileName),
                            "Config.Containers." + tagName, "name", "value");
                    AppLogger.getInstance(ContainerConfig.class).info(
                            "from resourceloader:["
                                    + ResourceLoader.getClassLoader()
                                    .toString() + "]");
                } catch (IOException e) {
                    try {
                        m = XMLReader
                                .getProperties(ResourceLoader
                                        .getResAsStream("SysConfig.xml"),
                                        "Config.Containers." + tagName, "name",
                                        "value");
                        AppLogger.getInstance(ContainerConfig.class).info(
                                "from resourceloader:["
                                        + ResourceLoader.getClassLoader()
                                        .toString() + "]");
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }
        if (m != null && !m.isEmpty()) {
            containerTable.put(tagName, m);
            // m.clear();
        }
        f = null;
        f2 = null;
    }

    // to test
    public static void main(String arg[]) throws Exception {
        // System.out.println(getContainValue("default", "URL_PKG_PREFIXES"));
        // setContainValue("xxx_name", "GROUP_NAMES", "cxcx");
        resetSysConfig();
        System.out.println(containerTable.size());
    }
}
