package com.dl.tool;

import com.aspose.cells.License;
import com.aspose.cells.PdfSaveOptions;
import com.aspose.cells.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * @version 1.0 说明 :
 */
public class Excel2Pdf {

	public static void main(String[] args) {

		String sourceFilePath="L:/testPDF/test.xls";//可生成PDF 没问题
		String desFilePath="L:\\testPDF\\PDF\\test.pdf";

		excel2pdf(sourceFilePath, desFilePath);

	}

	public static boolean getLicense() {
		boolean result = false;
		try {
			InputStream is = Word2Pdf.class.getClassLoader()
					.getResourceAsStream("\\license.xml");
			License aposeLic = new License();
			aposeLic.setLicense(is);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void excel2pdf(String sourceFilePath, String desFilePath) {
		if (!getLicense()) { // 验证License 若不验证则转化出的pdf文档会有水印产生
			return;
		}
		try {
			File pdfFile = new File(desFilePath);// 输出路径
			Workbook wb = new Workbook(sourceFilePath);// 原始excel路径
			FileOutputStream fileOS = new FileOutputStream(pdfFile);
			PdfSaveOptions pdfSaveOptions = new PdfSaveOptions();
			pdfSaveOptions.setOnePagePerSheet(true);//把内容放在一张PDF 页面上；

			wb.save(fileOS, pdfSaveOptions);//  只放一张纸；我的专为横向了
			fileOS.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
