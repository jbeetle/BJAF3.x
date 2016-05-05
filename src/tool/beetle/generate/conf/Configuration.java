package beetle.generate.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import com.beetle.framework.AppProperties;

public class Configuration {

	private Properties properties;
	private static Configuration cfg = null; // = new Configuration();

	private Configuration() {
		properties = new Properties();
		InputStream is = null;
		try {
			File f = new File(AppProperties.getAppHome()
					+ "genCodeConf.properties");
			is = new FileInputStream(f);
			// is = getClass().getResourceAsStream("config/conf.properties");
			properties.load(is);
		} catch (Exception ex) {
			System.out
					.println("Can't read the properties file,pleas check the path");
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (Exception ex) {
			}
		}
	}

	public static Configuration getInstance() {
		if (cfg == null) {
			cfg = new Configuration();
		}
		return cfg;
	}

	public String getValue(String key) {
		return properties.getProperty(key);
	}

	public static void main(String[] args) {
		Configuration cfg = Configuration.getInstance();
		System.out.println(cfg.getValue("db.dbType"));
		System.out.println(cfg.getValue("db.driver"));
		System.out.println(cfg.getValue("db.url"));
		System.out.println(cfg.getValue("db.user"));
		System.out.println(cfg.getValue("db.password"));
	}

}
