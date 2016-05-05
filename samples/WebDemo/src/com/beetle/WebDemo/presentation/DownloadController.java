package com.beetle.WebDemo.presentation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.beetle.framework.web.controller.AbnormalViewControlerImp;
import com.beetle.framework.web.controller.ControllerException;
import com.beetle.framework.web.controller.WebInput;

public class DownloadController extends AbnormalViewControlerImp {
	public DownloadController() {
		//this.enableSessionCheck();
		this.setMaxParallelAmount(5);
	}

	public void performX(WebInput wi, OutputStream out)
			throws ControllerException {
		InputStream is = null;
		try {
			is = this.getServletContext()
					.getResourceAsStream("images/logo.zip");
			this.setContentType("application/octet-stream");
			this.addHeader("Content-Disposition",
					"attachment; filename=logo.zip");
			byte[] buff = new byte[2048];
			while (is.read(buff) != -1) {
				out.write(buff);
			}
		} catch (Exception e) {
			throw new ControllerException(e);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				out.flush();
				out.close();
			} catch (IOException e) {
			}
		}
	}
}
