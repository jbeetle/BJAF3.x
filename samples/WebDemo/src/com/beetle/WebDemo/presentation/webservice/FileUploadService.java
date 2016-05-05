package com.beetle.WebDemo.presentation.webservice;

import com.beetle.WebDemo.common.Const;
import com.beetle.framework.web.controller.ControllerException;
import com.beetle.framework.web.controller.upload.FileObj;
import com.beetle.framework.web.controller.upload.IUpload;
import com.beetle.framework.web.controller.upload.UploadForm;
import com.beetle.framework.web.controller.upload.UploadHelper;
import com.beetle.framework.web.view.ModelData;
import com.beetle.framework.web.view.View;

public class FileUploadService implements IUpload {

	@Override
	public View processUpload(UploadForm upForm) throws ControllerException {
		View view = null;
		String newFileName = upForm.getFieldValue("fileName");
		FileObj fileObj = upForm.getFileObj(0);
		if (fileObj == null || fileObj.getFileName().equals("")) {
			ModelData vd = new ModelData();
			vd.put(Const.WEB_FORWARD_URL, "file.html");
			vd.put(Const.WEB_RETURN_MSG, "文件输入不能为空，请重新输入，谢谢！");
			view = new View("InfoView", vd); // 返回InfoView视图
		} else {
			ModelData vd = new ModelData();
			vd.put(Const.WEB_FORWARD_URL, "file.html");
			StringBuffer sb = new StringBuffer();
			sb.append(fileObj.getFileName());
			if (newFileName != null && !newFileName.equals("")) {
				sb.append("改成新的名字：" + newFileName);
				UploadHelper.saveFile(fileObj, Const.UPLOAD_FILE_SAVE_PATHE,
						newFileName);
			} else {
				UploadHelper.saveFile(fileObj, Const.UPLOAD_FILE_SAVE_PATHE);
			}
			sb.append("文件上传成功！");
			sb.append("保存在服务器的位置为：");
			sb.append(Const.UPLOAD_FILE_SAVE_PATHE);
			vd.put(Const.WEB_RETURN_MSG, sb.toString());
			vd.asJSON();
			view = new View("", vd); // 返回InfoView视图
		}
		return view;
	}

}
