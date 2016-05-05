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
import com.beetle.framework.web.controller.document.DocFactory;
import com.beetle.framework.web.controller.document.DocInfo;
import com.beetle.framework.web.controller.document.IDocument;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.ServletException;
import java.io.ByteArrayOutputStream;
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
public class DocumentController extends AbnormalViewControlerImp {
	private static final int INITIAL_SIZE = 4096;

	public DocumentController() {
		this.setCacheSeconds(-1);
	}

	public void performX(WebInput wi, OutputStream out)
			throws ControllerException {
		String path = wi.getControllerName();
		try {
			IDocument doc = DocFactory.getDocInstance(path, this
					.getServletContext(), (String) wi.getRequest()
					.getAttribute(CommonUtil.controllerimpclassname));
			if (doc != null) {
				int doctype = doc.getDocumentType();
				if (doctype == IDocument.TYPE_PDF) {
					createPDF(out, doc, wi);
				} else if (doctype == IDocument.TYPE_MS_EXCEL) {
					createExcel(out, doc, wi);
				} else if (doctype == IDocument.TYPE_MS_WORD) {
					createWord(out, doc, wi);
				}
			} else {
				throw new ControllerException(
						"errCode[-10061]:can't not find the controller[" + path
								+ "]");
			}
		} catch (Exception e) {
			throw new ControllerException("errCode[-1006]:" + e.getMessage(), e);
		}
	}

	/*
	 * private void dealDefault(WebInput wi, OutputStream out, String docType)
	 * throws IOException { if (docType != null && !docType.equals("")) { if
	 * (docType.equalsIgnoreCase(IDocument.PDF)) {
	 * this.setContentType("application/pdf"); } else if
	 * (docType.equalsIgnoreCase(IDocument.MS_EXCEL)) {
	 * this.setContentType("application/vnd.ms-excel"); } else if
	 * (docType.equalsIgnoreCase(IDocument.MS_WORD)) {
	 * this.setContentType("application/vnd.ms-word"); } BufferedInputStream bi
	 * = null; BufferedOutputStream bo = null; try { InputStream in =
	 * this.getServletContext().getResourceAsStream( wi.getServletPath()); bi =
	 * new BufferedInputStream(in); bo = new BufferedOutputStream(out); byte[]
	 * buff = new byte[2048]; int bytesRead; while (-1 != (bytesRead =
	 * bi.read(buff, 0, buff.length))) { bo.write(buff, 0, bytesRead); } } catch
	 * (IOException e) { throw e; } finally { if (bi != null) { bi.close(); } if
	 * (bo != null) { bo.flush(); bo.close(); } } } }
	 */
	private void createWord(OutputStream out, IDocument doc, WebInput wi)
			throws IOException, ServletException {
		this.setContentType("application/vnd.ms-word");
		// ...
	}

	private void createExcel(OutputStream out, IDocument doc, WebInput wi)
			throws IOException, ServletException {
		this.setContentType("application/vnd.ms-excel");
		HSSFWorkbook wb = new HSSFWorkbook();
		DocInfo di = new DocInfo(IDocument.TYPE_MS_EXCEL);
		try {
			di.setExcelDocument(wb);
			doc.createContent(wi, di);
			wb.write(out);
		} catch (Exception e) {
			throw new ServletException(e);
		} finally {
			if (out != null) {
				out.flush();
				out.close();
				out = null;
			}
			di = null;
			doc = null;
			wb = null;
		}
	}

	private void createPDF(OutputStream out, IDocument doc, WebInput wi)
			throws IOException, ServletException {
		this.setContentType("application/pdf");
		ByteArrayOutputStream baos = new ByteArrayOutputStream(INITIAL_SIZE);
		Document document = new Document(PageSize.A4);
		DocInfo di;
		try {
			PdfWriter.getInstance(document, baos);
			di = new DocInfo(IDocument.TYPE_PDF);
			di.setPdfDocument(document);
			// ͨ��ҳ���������ĵ�������Ϣ
			doc.createAttribute(wi, di);
			document.open();
			// �������
			doc.createContent(wi, di);
			document.close();
			this.setContentLength(baos.size());
			baos.writeTo(out);
		} catch (Exception ex1) {
			throw new ServletException(ex1);
		} finally {
			if (out != null) {
				out.flush();
				out.close();
				out = null;
			}
			if (baos != null) {
				baos.close();
				baos = null;
			}
			document = null;
			di = null;
			doc = null;
		}
	}
}
