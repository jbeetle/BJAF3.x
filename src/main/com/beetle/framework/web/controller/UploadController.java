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
package com.beetle.framework.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;

import com.beetle.framework.log.AppLogger;
import com.beetle.framework.web.common.CommonUtil;
import com.beetle.framework.web.common.WebConst;
import com.beetle.framework.web.controller.upload.FileObj;
import com.beetle.framework.web.controller.upload.IUpload;
import com.beetle.framework.web.controller.upload.UploadFactory;
import com.beetle.framework.web.controller.upload.UploadForm;
import com.beetle.framework.web.view.ModelData;
import com.beetle.framework.web.view.View;

/**
 * <p>
 * Title: BeetleWeb
 * </p>
 * 
 * <p>
 * Description: 文件上传控制器
 * 
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company: 甲壳虫软件
 * 
 * </p>
 * 
 * @author 余浩东(hdyu@beetlesoft.net)
 * @version 1.0
 */
public class UploadController extends ControllerImp {
	private static final Logger logger = AppLogger.getLogger(UploadController.class);

	public UploadController() {
		super();
		this.setCacheSeconds(0);
		this.setInstanceCacheFlag(false);
	}

	/**
	 * execute
	 * 
	 * @param webInput
	 *            WebInput
	 * @return Model
	 * @throws ServletException
	 * @todo Implement this com.beetle.framework.web.controller.ControllerImp
	 *       method
	 */
	public View perform(WebInput webInput) throws ControllerException {
		HttpServletRequest request = webInput.getRequest();
		return doupload(webInput, request);
	}

	/**
	 * use the overdue upload package
	 * 
	 * @param webInput
	 * @param request
	 * @return
	 * @throws ControllerException
	 */
	private View doupload(WebInput webInput, HttpServletRequest request) throws ControllerException {
		UploadForm fp = null;
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload sfu = new ServletFileUpload(factory);
		List<?> fileItems = null;
		boolean openApiCase = false;
		try {
			IUpload upload = (IUpload) webInput.getRequest().getAttribute("UPLOAD_CTRL_IOBJ");
			if (upload == null) {
				logger.debug("get upload from :{}", webInput.getControllerName());
				String ctrlimpName = (String) webInput.getRequest().getAttribute(CommonUtil.controllerimpclassname);
				if (ctrlimpName != null)
					upload = UploadFactory.getUploadInstance(webInput.getControllerName(), ctrlimpName); // 2007-03-21
			}
			if (upload == null) {
				String uploadclass = webInput.getParameter("$upload");
				if (uploadclass == null || uploadclass.trim().length() == 0) {
					throw new ControllerException("upload dealer can't not found!");
				}
				openApiCase = true;
				String uploadclass_ = ControllerFactory.composeClassImpName(webInput.getRequest(), uploadclass);
				logger.debug("uploadclass:{}", uploadclass);
				logger.debug("uploadclass_:{}", uploadclass_);
				upload = UploadFactory.getUploadInstance(uploadclass, uploadclass_);
			}
			logger.debug("IUpload:{}", upload);
			long sizeMax = webInput.getParameterAsLong("sizeMax", 0);
			if (sizeMax == 0) {
				sfu.setSizeMax(IUpload.sizeMax);
			} else {
				sfu.setSizeMax(sizeMax);
			}
			int sizeThreshold = webInput.getParameterAsInteger("sizeThreshold", 0);
			if (sizeThreshold == 0) {
				factory.setSizeThreshold(IUpload.sizeThreshold);
			} else {
				factory.setSizeThreshold(sizeThreshold);
			}
			Map<String, String> fieldMap = new HashMap<String, String>();
			List<FileObj> fileList = new ArrayList<FileObj>();
			fileItems = sfu.parseRequest(request);
			Iterator<?> i = fileItems.iterator();
			while (i.hasNext()) {
				FileItem fi = (FileItem) i.next();
				if (fi.isFormField()) {
					fieldMap.put(fi.getFieldName(), fi.getString());
				} else {
					fileList.add(new FileObj(fi));
				}
			}
			fp = new UploadForm(fileList, fieldMap, request, webInput.getResponse());
			View view = upload.processUpload(fp);
			if (view.getViewname() == null || view.getViewname().trim().equals("")) {
				// view.setViewName(AbnormalViewControlerImp.abnormalViewName);
				//
				if (openApiCase) {
					return view;
				}
				UpService us = new UpService(view);
				return us.perform(webInput);
			}
			return view;
		} catch (Exception ex) {
			logger.error("upload", ex);
			throw new ControllerException(WebConst.WEB_EXCEPTION_CODE_UPLOAD, ex);
		} finally {
			if (fileItems != null) {
				fileItems.clear();
			}
			if (fp != null) {
				fp.clear();
			}
			sfu = null;
		}
	}

	private static class UpService extends WebServiceController {
		private View vw;

		public UpService(View vw) {
			super();
			this.vw = vw;
		}

		@Override
		public ModelData defaultAction(WebInput webInput) throws ControllerException {
			return vw.getMd();
		}

	}
}
