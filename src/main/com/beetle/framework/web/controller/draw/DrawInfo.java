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
package com.beetle.framework.web.controller.draw;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;

/**
 * <p>
 * Title: BeetleSoft Framework
 * </p>
 * 
 * <p>
 * Description: ͼ��������Ϣ��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * 
 * <p>
 * Company: �׿ǳ����
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
final public class DrawInfo {
	private float quality;

	private JFreeChart chart;

	private int width;

	private int height;

	private ChartRenderingInfo info;

	private Object plusObj; // ������Ϣ����

	/**
	 * DrawInfo ��ο���http://www.jfree.org/jfreechart/index.php
	 * 
	 * @param quality
	 *            ͼ����ֵfloat
	 * @param chart
	 *            JFreeChart--����
	 * @param width
	 *            ͼ��Ŀ��
	 * @param height
	 *            ͼ�εĸ߶�
	 * @param info
	 *            ChartRenderingInfo������
	 */
	public DrawInfo(float quality, JFreeChart chart, int width, int height,
			ChartRenderingInfo info) {
		this.quality = quality;
		this.chart = chart;
		this.width = width;
		this.height = height;
		this.info = info;
	}

	public JFreeChart getChart() {
		return chart;
	}

	public int getHeight() {
		return height;
	}

	public ChartRenderingInfo getInfo() {
		return info;
	}

	public float getQuality() {
		return quality;
	}

	public int getWidth() {
		return width;
	}

	public Object getPlusObj() {
		return plusObj;
	}

	public void setPlusObj(Object plusObj) {
		this.plusObj = plusObj;
	}

	private void clear() {
		chart = null;
		info = null;
		plusObj = null;
	}

	protected void finalize() throws Throwable {
		clear();
		super.finalize();
	}
}
