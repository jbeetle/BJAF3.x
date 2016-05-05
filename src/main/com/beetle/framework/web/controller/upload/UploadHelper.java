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
package com.beetle.framework.web.controller.upload;

import java.io.File;

/**
 * <p>
 * Title: BeetleWeb
 * </p>
 * 
 * <p>
 * Description: 文件上传助手类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company: 甲壳虫软件
 * </p>
 * 
 * @author 余浩东(hdyu@beetlesoft.net)
 * @version 1.0
 */
public class UploadHelper {
	/**
	 * 保存传过来的文件到服务器上
	 * 
	 * 
	 * @param fileObj
	 *            文件对象
	 * @param path
	 *            服务器存放路径，文件名不变
	 * @return 返回此文件在服务器上的物理路径（含文件名称），如果为null，则代表保存失败
	 * 
	 */
	public static String saveFile(FileObj fileObj, String path) {
		String fileNameWithPath = fileObj.getFileName();
		if (fileNameWithPath != null) {
			int t = fileNameWithPath.lastIndexOf("\\");
			String fileName;
			if (t == -1) {
				t = fileNameWithPath.lastIndexOf("/");
			}
			fileName = fileNameWithPath.substring(t + 1);
			if (!path.endsWith("/") || !path.endsWith("\\")) {
				path = path + "/";
			}
			try {
				String fullpathfilename = path + fileName;
				fileObj.write(new File(fullpathfilename));
				return fullpathfilename;
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * 保存传过来的文件到服务器上
	 * 
	 * 
	 * @param fileObj
	 *            文件对象
	 * @param path
	 *            服务器存放路径
	 * 
	 * @param fileName
	 *            新的文件名
	 */
	public static void saveFile(FileObj fileObj, String path, String fileName) {
		if (!path.endsWith("/") || !path.endsWith("\\")) {
			path = path + "/";
		}
		try {
			fileObj.write(new File(path + fileName));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	/*
	 * public static boolean saveFileToRemoteServer(FileObj fileObj, String
	 * remoteServerUrl, String remoteServerPath, String remoteServerfileName) {
	 * try { InputStreamBody isb = new InputStreamBody(fileObj.getInputStream(),
	 * MIME.ENC_BINARY, fileObj.getFileName()); StringBody rsp = new
	 * StringBody(remoteServerPath); StringBody rsf = new
	 * StringBody(remoteServerfileName); MultipartEntity reqEntity = new
	 * MultipartEntity(); reqEntity.addPart("file", isb);
	 * reqEntity.addPart("remoteServerPath", rsp);
	 * reqEntity.addPart("remoteServerfileName", rsf); HttpClient httpclient =
	 * new DefaultHttpClient(); HttpPost httppost = new
	 * HttpPost(remoteServerUrl); httppost.setEntity(reqEntity); HttpResponse
	 * response = httpclient.execute(httppost); if (HttpStatus.SC_OK ==
	 * response.getStatusLine().getStatusCode()) { return true; } return false;
	 * } catch (Exception e) { return false; } finally { } }
	 */
}
