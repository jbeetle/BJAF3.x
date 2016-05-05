/*
 * BJAF - Beetle J2EE Application Framework
 * �׿ǳ�J2EE��ҵӦ�ÿ������
 * ��Ȩ����2003-2009 ��ƶ� (www.beetlesoft.net)
 * 
 * ����һ����ѿ�Դ�������������ڡ��׿ǳ�J2EEӦ�ÿ�������ȨЭ�顷
 *
 *   ��GNU Lesser General Public License v3.0��
 *<http://www.gnu.org/licenses/lgpl-3.0.txt/>�ºϷ�ʹ�á��޸Ļ����·�����
 *
 * ��л��ʹ�á��ƹ㱾��ܣ����н�������⣬��ӭ�������ϵ��
 * �ʼ��� <yuhaodong@gmail.com/>.
 */
package com.beetle.framework.web.controller;

import com.beetle.framework.web.common.CommonUtil;
import com.beetle.framework.web.controller.draw.DrawFactory;
import com.beetle.framework.web.controller.draw.DrawInfo;
import com.beetle.framework.web.controller.draw.IDraw;
import org.jfree.chart.ChartUtilities;

import javax.servlet.ServletException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>
 * Title: BeetleWeb
 * </p>
 * 
 * <p>
 * Description: MVC Web Framework
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company: �׿ǳ����
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
final public class DrawController extends AbnormalViewControlerImp {
	public DrawController() {
		this.setCacheSeconds(-1);
	}

	private void drawing(OutputStream out, DrawInfo drawInfo)
			throws IOException {
		if (drawInfo.getPlusObj() != null && drawInfo.getQuality() == -1) {
			BufferedImage image = (BufferedImage) drawInfo.getPlusObj();
			ChartUtilities.writeBufferedImageAsJPEG(out, image);
		} else {
			ChartUtilities.writeChartAsJPEG(out, drawInfo.getQuality(),
					drawInfo.getChart(), drawInfo.getWidth(),
					drawInfo.getHeight(), drawInfo.getInfo());
		}
		drawInfo = null;
	}

	/**
	 * performX
	 * 
	 * @param webInput
	 *            WebInput
	 * @param outputStream
	 *            OutputStream
	 * @throws ServletException
	 * @todo Implement this
	 *       com.beetle.framework.web.controller.AbnormalViewControlerImp method
	 */
	public void performX(WebInput webInput, OutputStream outputStream)
			throws ControllerException {
		this.setContentType("image/jpeg");
		// response.setHeader("Pragma", "No-cache");
		// response.setHeader("Cache-Control", "no-cache");
		// response.setDateHeader("Expires", 0);
		try {
			IDraw draw = (IDraw) webInput.getRequest().getAttribute(
					"DRAW_CTRL_IOBJ");
			if (draw == null) {
				draw = DrawFactory.getDrawInstance(
						webInput.getControllerName(),
						this.getServletContext(),
						(String) webInput.getRequest().getAttribute(
								CommonUtil.controllerimpclassname));
			}
			drawing(outputStream, draw.draw(webInput));
		} catch (Exception ex) {
			throw new ControllerException("errCode[-1007]:" + ex.getMessage(),
					ex);
		}
	}
}
