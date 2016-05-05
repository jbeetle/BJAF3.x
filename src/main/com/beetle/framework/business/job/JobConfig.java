package com.beetle.framework.business.job;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.beetle.framework.AppProperties;
import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.util.ConvertUtil;
import com.beetle.framework.util.ResourceLoader;
import com.beetle.framework.util.file.DirUtil;
import com.beetle.framework.util.thread.ThreadImp;

public final class JobConfig {
	private final static Map<String, JobDef> scache = new ConcurrentHashMap<String, JobDef>();
	private final static AppLogger logger = AppLogger
			.getInstance(JobConfig.class);
	private final static String jobfilename = AppProperties.getAppHome()
			+ "JobConfig.xml";
	private static long jobfilenameLastTime = 0;

	private JobConfig() {
	}

	static {
		load();
		jobfilenameLastTime = DirUtil.getFileLastModified(jobfilename);
		if (jobfilenameLastTime <= 0) {
			logger.warn(
					"The file[{}] does not exist or don't have permission to access!",
					jobfilename);
		} else {
			new FileMonitor("BJAF_job_FileMonitor", AppProperties.getAsInt(
					"job_FileMonitor_interval", 1000 * 30)).startAsDaemon();
		}
	}

	private static class FileMonitor extends ThreadImp {

		public FileMonitor(String threadName, long interval) {
			super(threadName, interval);
		}

		@Override
		protected void routine() throws Throwable {
			long lastUpdateTime = DirUtil.getFileLastModified(jobfilename);
			if (lastUpdateTime <= 0) {
				logger.warn(
						"The file[{}] does not exist or don't have permission to access!",
						jobfilename);
				return;
			}
			if (jobfilenameLastTime != lastUpdateTime) {
				jobfilenameLastTime = lastUpdateTime;
				load();
				logger.info("reload the {} file OK!", jobfilename);
			}
		}

	}

	private synchronized static void load() {
		scache.clear();
		File f = new File(jobfilename);
		if (f.exists()) {
			loadFromConfig(f);
			logger.info("load services from file[{}]", jobfilename);
		} else {
			try {
				loadFromConfig(ResourceLoader.getResAsStream(jobfilename));
				logger.info("load services from resourceloader[{}]",
						jobfilename);
			} catch (IOException e) {
				logger.warn("no [{}] file found,not load service define data",
						jobfilename);
			}
		}
	}

	public static JobDef lookup(String id) {
		return scache.get(id);
	}

	public static List<JobDef> getJobs() {
		List<JobDef> jobs = new ArrayList<JobDef>();
		Iterator<JobDef> it = scache.values().iterator();
		while (it.hasNext()) {
			jobs.add(it.next());
		}
		return jobs;
	}

	public static void register(JobDef job) {
		scache.put(job.getId(), job);
		logger.debug("register job:{}", job);
	}

	private static void gendoc(Document doc) throws ClassNotFoundException {
		Node node = doc.selectSingleNode("jobs");
		if (node != null) {
			Iterator<?> it = node.selectNodes("item").iterator();
			while (it.hasNext()) {
				JobDef job = new JobDef();
				Element e = (Element) it.next();
				String id = e.valueOf("@id");
				String imp = e.valueOf("@implement");
				String enabled = e.valueOf("@enabled");
				String cron = e.valueOf("@cron");
				String timeout = e.valueOf("@timeout");
				String starttime = e.valueOf("@starttime");
				job.setId(id);
				job.setImplement(imp);
				job.setEnabled(Boolean.parseBoolean(enabled.trim()));
				job.setCron(cron);
				if (timeout != null && timeout.trim().length() > 0) {
					job.setTimeout(Long.parseLong(timeout));
				}
				if (starttime != null && starttime.trim().length() > 0) {
					job.setStarttime(ConvertUtil.toDateByFormat(starttime,
							"yyyy-MM-dd HH:mm:ss").getTime());
				} else {
					job.setStarttime(System.currentTimeMillis());
				}
				register(job);
			}
		}
	}

	private static void loadFromConfig(InputStream xmlFileInputStream) {
		SAXReader reader = new SAXReader();
		Document doc = null;
		try {
			doc = reader.read(xmlFileInputStream);
			gendoc(doc);
		} catch (Exception de) {
			throw new AppRuntimeException(de);
		} finally {
			if (doc != null) {
				doc.clearContent();
				doc = null;
			}
			reader = null;
		}
	}

	private static void loadFromConfig(File f) {
		SAXReader reader = new SAXReader();
		Document doc = null;
		try {
			doc = reader.read(f);
			gendoc(doc);
		} catch (Exception de) {
			throw new AppRuntimeException(de);
		} finally {
			if (doc != null) {
				doc.clearContent();
				doc = null;
			}
			reader = null;
		}
	}
}
