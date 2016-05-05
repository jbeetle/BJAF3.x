package com.beetle.WebDemo.presentation;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.beetle.framework.web.controller.ControllerException;
import com.beetle.framework.web.controller.WebInput;
import com.beetle.framework.web.controller.document.DocInfo;
import com.beetle.framework.web.controller.document.IDocument;

public class GenExcelController implements IDocument {

	public void createAttribute(WebInput wi, DocInfo di) throws ControllerException{
		// ...
	}

	public void createContent(WebInput wi, DocInfo di)throws ControllerException {
		HSSFWorkbook wb = di.getExcelDocument();
		// 创建HSSFSheet对象
		HSSFSheet sheet = wb.createSheet("sheet0");
		// 创建HSSFRow对象
		HSSFRow row = sheet.createRow((short) 0);
		// 创建HSSFCell对象
		HSSFCell cell = row.createCell((short) 0);
		// 用来处理中文问题
		// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		// 设置单元格的值
		// cell.setCellValue("Hello World! 你好，中文世界");
		String info = wi.getParameter("info");
		HSSFRichTextString rts = new HSSFRichTextString(info);
		cell.setCellValue(rts);
		HSSFCell cell2 = row.createCell((short) 1);
		cell2.setCellValue(new HSSFRichTextString(
				"Beetle Web Framework 页面生成Excel文件演示!"));
	}

	public int getDocumentType() {
		return IDocument.TYPE_MS_EXCEL;
	}

}
