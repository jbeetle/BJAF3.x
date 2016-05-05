package com.beetle.framework.util.structure;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import com.beetle.framework.AppRuntimeException;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class DocumentTemplate {
	private final Configuration cf;

	public DocumentTemplate(javax.servlet.ServletContext servletContext,
			String pathPrefix) {
		cf = new Configuration();
		try {
			cf.setServletContextForTemplateLoading(servletContext, pathPrefix);
		} catch (Exception e) {
			throw new AppRuntimeException(e);
		}
	}

	public DocumentTemplate(String directoryForTemplateLoading) {
		cf = new Configuration();
		try {
			cf.setDirectoryForTemplateLoading(new File(
					directoryForTemplateLoading));
		} catch (IOException e) {
			throw new AppRuntimeException(e);
		}
	}

	public DocumentTemplate(Class<?> clazz, String pathPrefix) {
		cf = new Configuration();
		try {
			cf.setClassForTemplateLoading(clazz, pathPrefix);
		} catch (Exception e) {
			throw new AppRuntimeException(e);
		}
	}

	public void setEncoding(String encoding) {
		cf.setDefaultEncoding(encoding);
	}

	public void process(Map<?, ?> rootMap, String fileNameOfTemplate,
			Writer writer) {
		Template tpl;
		try {
			tpl = cf.getTemplate(fileNameOfTemplate);
			tpl.process(rootMap, writer);
		} catch (Exception e) {
			throw new AppRuntimeException(e);
		}
	}

	public String process(Map<?, ?> rootMap, String fileNameOfTemplate) {
		StringWriter sw = new StringWriter();
		try {
			Template tpl = cf.getTemplate(fileNameOfTemplate);
			tpl.process(rootMap, sw);
			return sw.toString();
		} catch (Exception e) {
			throw new AppRuntimeException(e);
		} finally {
			sw.flush();
			try {
				sw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void clearCache() {
		cf.clearTemplateCache();
	}
}
