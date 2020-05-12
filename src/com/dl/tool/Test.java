package com.dl.tool;

import com.aspose.cells.License;

import java.io.InputStream;

/**
 * 
 * 由于ASPOSE比较吃内存，操作大一点的文件就会堆溢出，所以请先设置好java虚拟机参数：-Xms512m -Xmx512m(参考值)<br>
 * 如有疑问，请在CSDN下载界面留言,或者联系QQ569925980<br>
 * 
 * @author Spark
 *
 */
public class Test {
	/*Aspose.Words license
	https://www.cnblogs.com/andyz168/p/5602398.html

	http://blog.csdn.net/shidouyu/article/details/53534738

	https://www.cnblogs.com/javalism/p/3453751.html

	https://www.cnblogs.com/zmjBlog/p/6432731.html

	http://blog.csdn.net/zxq1406spys/article/details/1873700
		*/
    /**
     * 获取license
     * 
     * @return
     */
	public static boolean getLicense() {
        boolean result = false;
        try {
            InputStream is = Test.class.getClassLoader().getResourceAsStream("\\license.xml");
            License aposeLic = new License();
            aposeLic.setLicense(is);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 支持DOC, DOCX, OOXML, RTF, HTML, OpenDocument, PDF, EPUB, XPS, SWF等相互转换<br>
     * 
     * @param args
     */
    public static void main(String[] args) {
        String s = "^[&].*";
        String s2 = "&666";
        System.out.println(s2.matches(s));
       /* Pattern pattern = Pattern.compile("^[&|#].*");


        Matcher isNum = pattern.matcher(s2); // matcher是全匹配
        if (isNum.matches()) {
            System.out.println("ddd");
        }*/
        // 验证Licensev
        /*if (!getLicense()) {
            return;
        }

        try {
            long old = System.currentTimeMillis();
            String sourceFilePath="/Users/xuxu/Downloads/1期地块2号楼机房巡检月报表.xlsx";//可生成PDF 没问题
    		
            Workbook wb = new Workbook(sourceFilePath);// 原始excel路径
            File pdfFile = new File("/Users/xuxu/Downloads/aa.pdf");// 输出路径
            FileOutputStream fileOS = new FileOutputStream(pdfFile);

            PdfSaveOptions pdfSaveOptions = new PdfSaveOptions();
            pdfSaveOptions.setOnePagePerSheet(true);//把内容放在一张PDF 页面上；

            wb.save(fileOS, pdfSaveOptions);//  只放一张纸；我的专为横向了
            //wb.save(fileOS, SaveFormat.PDF);  //不做限制 ，   导出的为纵向







            fileOS.close();

            long now = System.currentTimeMillis();
            System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}