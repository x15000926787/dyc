package com.dl.tool;


import com.aspose.words.Document;
import com.aspose.words.SaveFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/** 
 * @version 1.0 
 * 说明 :
 */
public class Word2Pdf {
	
	public static void main(String[] args) {

		String sourceFilePath="L:/testPDF/test.xls";//可生成PDF 没问题
		String desFilePath="L:\\testPDF\\PDF\\test.pdf";
		
		word2pdf(sourceFilePath, desFilePath);
		
	}
	
	
	public static boolean getLicense() {
		boolean result = false;
		try {
			InputStream is = Word2Pdf.class.getClassLoader().getResourceAsStream(
					"\\license.xml"); 
			com.aspose.words.License aposeLic = new com.aspose.words.License();
			aposeLic.setLicense(is);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void word2pdf(String sourceFilePath,String desFilePath) {
		if (!getLicense()) { // 验证License 若不验证则转化出的PDP文档会有水印产生
			return;
		}
		try {
			
			File file = new File(desFilePath); // 新建一个空白pdf文档
			FileOutputStream os = new FileOutputStream(file);
			Document doc = new Document(sourceFilePath); // Address是将要被转化的word文档
			doc.save(os, SaveFormat.PDF); // 全面支持DOC, DOCX, OOXML, RTF HTML,
											// OpenDocument, PDF, EPUB, XPS, SWF
											// 相互转换 os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
