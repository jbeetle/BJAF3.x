package com.beetle.framework.appsrv.plugin;

import com.beetle.framework.AppProperties;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.util.ClassUtil;
import com.beetle.framework.util.file.DirUtil;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public final class PluginManager {
	private String pluginDir;

	private final static AppLogger logger = AppLogger
			.getInstance(PluginManager.class);

	private static PluginManager instance = new PluginManager();

	private static class FV {
		public void setLastTime(long lastTime) {
			this.lastTime = lastTime;
		}

		private String filename;

		private long lastTime;

		public FV(String filename, long lastTime) {
			super();
			this.filename = filename;
			this.lastTime = lastTime;
		}

		public String getFilename() {
			return filename;
		}

		public long getLastTime() {
			return lastTime;
		}

	}

	private static Map<String, FV> fvStatus = new HashMap<String, FV>();

	private static Map<String, LoaderVO> loaderCache = new HashMap<String, LoaderVO>();

	private Class<?> face;

	private String configFileName;
	private final Properties pluginIni;
	private final int checkInterval;

	public static PluginManager getInstance() {
		return instance;
	}

	private class DirMonitor extends Thread {
		private String sfname = ".jar";

		public DirMonitor() {
			super();
			this.setDaemon(true);
			this.setName("PluginManager-DirMonitor");
		}

		@Override
		public void run() {
			while (true) {
				try {
					checkFile();
					sleep(checkInterval);
				} catch (Throwable e) {
					logger.error("DirMonitor run err", e);
				}
			}
		}

		private void checkFile() {
			List<String> jarList = DirUtil.getCurrentDirectoryFileNames(
					pluginDir, true, sfname);
			if (!jarList.isEmpty()) {
				for (String fn : jarList) {
					long lastUpdateTime = DirUtil.getFileLastModified(fn);
					if (lastUpdateTime <= 0) {
						logger.error("Err!File["
								+ fn
								+ "] does not exist or don't have permission to acess!");
					} else {
						FV fv = fvStatus.get(fn);
						if (fv == null) {
							fv = new FV(fn, lastUpdateTime);
							fvStatus.put(fn, fv);
							logger.info("found a new plugin[" + fn
									+ "] to updat...");
							loadPlugin(fn, lastUpdateTime);
						} else {
							if (fv.getLastTime() != lastUpdateTime) {// 有变化
								fv.setLastTime(lastUpdateTime);
								logger.info("update the plugin["
										+ fv.getFilename() + "]...");
								loadPlugin(fv.getFilename(), lastUpdateTime);
							}
						}
					}
				}
			}
		}
	}

	private void loadPlugin(String filename, long lasttime) {
		if (logger.isDebugEnabled()) {
			logger.debug("loadPlugin[" + filename + "]");
		}
		if (reloadConfig()) {
			try {
				URL url = new URL("file:" + filename);
				URLClassLoader ul = new URLClassLoader(new URL[] { url });
				String id = DirUtil.removePath(filename).toLowerCase();
				LoaderVO lvo = new LoaderVO();
				lvo.setId(id);
				lvo.setLasttime(lasttime);
				lvo.setLoader(ul);// 先保留
				Class<?>[] ccs = ClassUtil.findImpClass(face, filename, ul);
				if (ccs != null && ccs.length >= 1) {
					Object o = ccs[0].newInstance();
					lvo.setHandler(o);
				} else {
					throw new PluginException("can't found face[" + face
							+ "]'s implements class in the jar[" + filename
							+ "]");
				}
				loaderCache.put(lvo.getId(), lvo);
				logger.debug("lvo:{}", lvo);
				logger.info("plugin[" + filename + "]loaded OK!");
			} catch (Exception e) {
				throw new PluginException("plugin[" + filename
						+ "]load failed!", e);
			}
		} else {
			logger.error("plugin[" + filename + "]load failed!");
		}
	}

	/**
	 * 根据插件的文件名（jar包名称，不带路径）获取加载的插件对象
	 * 
	 * @param filename
	 * @return
	 */
	public LoaderVO getLoaderVO(String filename) {
		return loaderCache.get(filename.toLowerCase());
	}

	private boolean reloadConfig() {
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(new File(configFileName));
			pluginIni.load(stream);
			if (logger.isDebugEnabled()) {
				logger.debug("reload " + configFileName + " OK:{}", pluginIni);
			}
			return true;
		} catch (Exception e) {
			logger.error("reload " + configFileName + " error", e);
			return false;
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (Exception e) {
					logger.error("close failed", e);
				}
			}
		}
	}

	/**
	 * 
	 * 初始化插件管理器
	 * 
	 * @param classFace
	 *            -插件的接口类（或抽象类）
	 */
	public void initialize(Class<?> classFace) {
		this.face = classFace;
		new DirMonitor().start();
		logger.info("PluginManager initialize OK!");
	}

	private PluginManager() {
		this.pluginIni = new Properties();
		this.pluginDir = AppProperties.get("appsrv_plugin_DirPath", "plugin/");
		this.configFileName = AppProperties.get(
				"appsrv_plugin_PropertiesFileName", "plugin/plugin.properties");
		this.checkInterval = AppProperties.getAsInt(
				"appsrv_plugin_checkInterval", 12 * 1000);
	}

	/**
	 * 获取插件信息文件里面的属性
	 * 
	 * @param key
	 * @return
	 */
	public String getPluginProperty(String key) {
		return pluginIni.getProperty(key);
	}

	/**
	 * 清空插件管理资源（回收资源）
	 */
	public void clear() {
		fvStatus.clear();
		loaderCache.clear();
		logger.info("PluginManager quit OK");
	}
}
