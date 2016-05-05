package com.beetle.WebDemo.presentation;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import com.beetle.framework.web.controller.ControllerException;
import com.beetle.framework.web.controller.WebInput;
import com.beetle.framework.web.controller.draw.DrawInfo;
import com.beetle.framework.web.controller.draw.IDraw;

public class DrawPieController implements IDraw {

	public DrawInfo draw(WebInput wi) throws ControllerException {
		int width = wi.getParameterAsInteger("width");
		int height = wi.getParameterAsInteger("height");
		DefaultPieDataset pie = new DefaultPieDataset();
		pie.setValue("Struts", 500);
		pie.setValue("WebWork", 300);
		pie.setValue("Spring MVC", 100);
		pie.setValue("JSF", 50);
		pie.setValue("Beetle MVC", 1200);
		JFreeChart chart = ChartFactory.createPieChart3D("web框架市场占有率", pie, true,
				false, false);
		return new DrawInfo(100, chart, width, height, null);
	}

}
