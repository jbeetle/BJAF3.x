package com.beetle.WebDemo.presentation;

import com.beetle.framework.web.controller.ControllerException;
import com.beetle.framework.web.controller.WebInput;
import com.beetle.framework.web.controller.document.DocInfo;
import com.beetle.framework.web.controller.document.IDocument;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;

public class GenPdfController implements IDocument {

	public void createAttribute(WebInput wi, DocInfo di) throws ControllerException{
		String auther = wi.getParameter("auther");
		Document pdfDoc = di.getPdfDocument();
		pdfDoc.addAuthor(auther);
	}

	public void createContent(WebInput wi, DocInfo di)throws ControllerException {
		Document pdfDoc = di.getPdfDocument();
		try {
			pdfDoc.add(new Paragraph("Hello World!"));
			try {
				BaseFont bf = BaseFont.createFont("STSong-Light",
						"UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
				Font FontChinese = new Font(bf, 12, Font.NORMAL);
				String info=wi.getParameter("info");
				Paragraph p0 = new Paragraph(info, FontChinese);
				pdfDoc.add(p0);
				Paragraph p1 = new Paragraph("Beetle Web Framework 页面生成PDF文件演示!", FontChinese);
				pdfDoc.add(p1);
			} catch (Exception ex1) {
				throw new ControllerException(ex1);
			}
		} catch (DocumentException ex) {
			throw new ControllerException(ex);
		}
	}

	public int getDocumentType() {
		return IDocument.TYPE_PDF;
	}

}
