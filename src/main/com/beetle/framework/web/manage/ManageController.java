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
package com.beetle.framework.web.manage;

import com.beetle.framework.web.controller.AbnormalViewControlerImp;
import com.beetle.framework.web.controller.ControllerException;
import com.beetle.framework.web.controller.ControllerFactory;
import com.beetle.framework.web.controller.WebInput;
import com.beetle.framework.web.view.ViewFactory;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ManageController extends AbnormalViewControlerImp {

	public void performX(WebInput wi, OutputStream os)
			throws ControllerException {
		PrintWriter out = new PrintWriter(os);
		int queryflag = wi.getParameterAsInteger("query");
		if (queryflag == 1) {// 查询当前系统控制器列表

			showControllers(out);
		} else if (queryflag == 2) {// 查询当前系统的视图列表

			showViews(out);
		} else if (queryflag == 3) {// 查询当前系统控制器所关联视图类表
			showContrllerViews(out);
		} else {
			showCommand(out);
		}
		out.close();
		out.flush();
	}

	private void showContrllerViews(PrintWriter out) {
		Map<String, HashSet<String>> cv = ControllerFactory.getCtrlViewMap();
		Map<String, String> v = ViewFactory.getAllViews();
		Map<String, String> c = ControllerFactory.getAllControllers();
		out.println("<div align='center'><font size='4' face='Geneva, Arial, Helvetica, sans-serif'><strong>all controller-views relationship in runtime</strong></font></div>");
		out.println("<table width='85%' border='0' align='center'>");
		out.println("<tr bgcolor='#333333'>");
		out.println("<td width='30%'><strong><font color='#FFFFFF'>ControllerName</font></strong></td>");
		out.println("<td width='57%'><strong><font color='#FFFFFF'>ImplemnetClass&Views</font></strong></td>");
		out.println("<td width='13%'><strong><font color='#FFFFFF'><a href='#'>export</a></font></strong></td>");
		out.println("</tr>");
		//
		Set<?> s = cv.entrySet();
		Iterator<?> it = s.iterator();
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry kv = (Map.Entry) it.next();
			String classname = (String) c.get(kv.getKey());
			out.println("<tr>");
			out.println("<td><font size='2' face='Verdana, Arial, Helvetica, sans-serif'>"
					+ kv.getKey() + "</font></td>");
			out.println("<td colspan='2'>");
			out.println("<table width='100%' border='0' align='left'>");
			out.println("<tr bgcolor='#CCCCCC'><td colspan='2'>" + classname
					+ "</td></tr>");
			// out.println("<tr bgcolor='#333333'> ");
			// out
			// .println("<td width='30%'><font color='#FFFFFF'>ViewName</font></td>");
			// out
			// .println("<td width='70%'><font color='#FFFFFF'>RealURL</font></td>");
			// out.println("</tr>");
			//
			HashSet<?> hs = (HashSet<?>) kv.getValue();
			Iterator<?> it2 = hs.iterator();
			boolean f = true;
			while (it2.hasNext()) {
				String viewname = (String) it2.next();
				String url = (String) v.get(viewname);
				if (f) {
					out.println("<tr>");
					out.println("<td>" + viewname + "</td>");
					out.println("<td>" + url + "</td>");
					out.println("</tr>");
					f = false;
				} else {
					out.println("<tr bgcolor='#CCCCCC'>");
					out.println("<td>" + viewname + "</td>");
					out.println("<td>" + url + "</td>");
					out.println("</tr>");
					f = true;
				}
			}
			//
			out.println("</table>");
			out.println("</td>");
			out.println("</tr>");
			out.println("<tr><td colspan='3'><hr noshade></td></tr>");
		}
		//

		out.println("<tr bgcolor='#333333'> ");
		out.println("<td colspan='3'><div align='right'><font color='#FFFFFF'>total:"
				+ cv.size() + "</font></div></td>");
		out.println("</tr>");
		out.println("<tr bgcolor='#FFFFFF'>");
		out.println("<td colspan='3'> <div align='center'><a href='$framework.web.manage.ManageController.ctrl'>Back</a></div></td>");
		out.println("</tr>");
		out.println("</table>");
		v.clear();
		c.clear();
	}

	private void showViews(PrintWriter out) {
		out.println("<div align='center'><font size='4' face='Geneva, Arial, Helvetica, sans-serif'><strong>all views</strong></font></div>");
		out.println("<table width='85%' border='0' align='center'>");
		out.println("<tr bgcolor='#333333'>");
		out.println("<td width='30%'><strong><font color='#FFFFFF'>ViewName</font></strong></td>");
		out.println("<td width='57%'><strong><font color='#FFFFFF'>RealURL</font></strong></td>");
		out.println("<td width='13%'><strong><font color='#FFFFFF'><a href='#'>export</a></font></strong></td>");
		out.println("</tr>");
		Map<?, ?> m = ViewFactory.getAllViews();
		Set<?> s = m.entrySet();
		Iterator<?> it = s.iterator();
		boolean f = true;
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry kv = (Map.Entry) it.next();
			if (f) {
				out.println("<tr>");
				out.println("<td><font size='2' face='Verdana, Arial, Helvetica, sans-serif'>"
						+ kv.getKey() + "</font></td>");
				out.println("<td colspan='2'><font size='2' face='Verdana, Arial, Helvetica, sans-serif'>"
						+ kv.getValue() + "</font></td>");
				out.println("</tr>");
				f = false;
			} else {
				out.println("<tr bgcolor='#CCCCCC'> ");
				out.println("<td><font size='2' face='Verdana, Arial, Helvetica, sans-serif'>"
						+ kv.getKey() + "</font></td>");
				out.println("<td colspan='2'><font size='2' face='Verdana, Arial, Helvetica, sans-serif'>"
						+ kv.getValue() + "</font></td>");
				out.println("</tr>");
				f = true;
			}
		}
		out.println("<tr bgcolor='#333333'>");
		out.println("<td colspan='3'><div align='right'><font color='#FFFFFF'>total:"
				+ m.size() + "</font></div></td>");
		out.println("</tr>");
		out.println("<tr bgcolor='#FFFFFF'> ");
		out.println("<td colspan='3'> <div align='center'><a href='$framework.web.manage.ManageController.ctrl'>Back</a></div></td>");
		out.println("</tr>");
		out.println("</table>");
		m.clear();
	}

	private void showControllers(PrintWriter out) {
		out.println("<div align='center'><font size='4' face='Geneva, Arial, Helvetica, sans-serif'><strong>all controllers</strong></font></div>");
		out.println("<table width='85%' border='0' align='center'>");
		out.println("<tr bgcolor='#333333'>");
		out.println("<td width='30%'><strong><font color='#FFFFFF'>ControllerName</font></strong></td>");
		out.println("<td width='57%'><strong><font color='#FFFFFF'>ImplemnetClass</font></strong></td>");
		out.println("<td width='13%'><strong><font color='#FFFFFF'><a href='#'>export</a></font></strong></td>");
		out.println("</tr>");
		Map<?, ?> m = ControllerFactory.getAllControllers();
		Set<?> s = m.entrySet();
		Iterator<?> it = s.iterator();
		boolean f = true;
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry kv = (Map.Entry) it.next();
			if (f) {
				out.println("<tr>");
				out.println("<td><font size='2' face='Verdana, Arial, Helvetica, sans-serif'>"
						+ kv.getKey() + "</font></td>");
				out.println("<td colspan='2'><font size='2' face='Verdana, Arial, Helvetica, sans-serif'>"
						+ kv.getValue() + "</font></td>");
				out.println("</tr>");
				f = false;
			} else {
				out.println("<tr bgcolor='#CCCCCC'> ");
				out.println("<td><font size='2' face='Verdana, Arial, Helvetica, sans-serif'>"
						+ kv.getKey() + "</font></td>");
				out.println("<td colspan='2'><font size='2' face='Verdana, Arial, Helvetica, sans-serif'>"
						+ kv.getValue() + "</font></td>");
				out.println("</tr>");
				f = true;
			}
		}
		out.println("<tr bgcolor='#333333'>");
		out.println("<td colspan='3'><div align='right'><font color='#FFFFFF'>total:"
				+ m.size() + "</font></div></td>");
		out.println("</tr>");
		out.println("<tr bgcolor='#FFFFFF'> ");
		out.println("<td colspan='3'> <div align='center'><a href='$framework.web.manage.ManageController.ctrl'>Back</a></div></td>");
		out.println("</tr>");
		out.println("</table>");
		m.clear();
	}

	private void showCommand(PrintWriter out) {
		out.println("<table width='40%' border='0' align='center'>");
		out.println("  <tr> ");
		out.println("    <td colspan='2'><strong>Beetle Web Console：</strong></td>");
		out.println("  </tr>");
		out.println("  <tr bgcolor='#CCCCCC'> ");
		out.println("    <td width='3%'>&oslash;</td>");
		out.println("    <td width='97%'><a href='$framework.web.manage.ManageController.ctrl?query=1'>Show all controllers</a></td>");
		out.println("  </tr>");
		out.println("  <tr> ");
		out.println("    <td>&oslash;</td>");
		out.println("    <td><a href='$framework.web.manage.ManageController.ctrl?query=2'>Show all views</a></td>");
		out.println("  </tr>");
		out.println("  <tr bgcolor='#CCCCCC'> ");
		out.println("    <td>&oslash;</td>");
		out.println("    <td><a href='$framework.web.manage.ManageController.ctrl?query=3'>Show all controller-views relationship in runtime</a></td>");
		out.println("  </tr>");
		out.println("</table>");
	}

}
