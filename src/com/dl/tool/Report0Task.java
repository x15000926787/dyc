package com.dl.tool;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @des    报表处理
 * @author xuxu
 * @create 2020-04-14 14:31
 */
public class Report0Task implements Runnable {
    private String bbname;
    private String bbtype,reportPath,zero_condition;
    private File file;
    private Excel2Pdf excel2Pdf = new Excel2Pdf();
    private JdbcTemplate jdbcTemplate;
    public static LocalDate rightnow;

    private static final Logger logger = FirstClass.logger;
    private Workbook workbook;
    DateTimeFormatter df = DateTimeFormatter.ofPattern("MMdd");
    DateTimeFormatter df2 = DateTimeFormatter.ofPattern("yyyyMM");
    DateTimeFormatter df3 = DateTimeFormatter.ofPattern("yyyy-MM");
    DecimalFormat decimalFormat = new DecimalFormat(".00");
    //private ScriptEngine scriptEngine ;// scriptEngineManager.getEngineByName("nashorn");
    public Report0Task(File file, String bbname, String bbtype, JdbcTemplate jdbcTemplate, LocalDate rightnow) throws IOException {
        this.bbname = bbname;
        this.bbtype = bbtype;
        this.jdbcTemplate = jdbcTemplate;
        this.rightnow = rightnow;
        this.file = file;
        reportPath = PropertyUtil.getProperty("reportPath");
        zero_condition = PropertyUtil.getProperty("zero_condition",">");

       // this.scriptEngine = scriptEngine;
        //this.tjedis = tjedis;
    }

    /**
     * 单元格内容解析
     *
     * @param cell
     * @return
     */
    private String getCellValue(Cell cell) {
        String cellValue = null;
        switch (cell.getCellType()) {
            // 数字
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    cellValue = sdf.format(DateUtil.getJavaDate(cell.getNumericCellValue()));
                } else {
                    DataFormatter dataFormatter = new DataFormatter();
                    cellValue = dataFormatter.formatCellValue(cell);
                }
                break;
            // 字符串
            case STRING:
                cellValue = cell.getStringCellValue();
                break;
            // Boolean
            case BOOLEAN:
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            // 公式
            case FORMULA:
                cellValue = cell.getCellFormula();
                break;
            // 空值
            case BLANK:
                cellValue = null;
                break;
            // 错误
            case ERROR:
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }

    /**
     *
     * @param jdbcTemplate
     * @param saveno
     * @param zero
     * @param type    预留：0：日平均值，1，日最大值，2：日最小值，3：整点取值
     * @param rightnow
     * @return
     */
    public JSONObject getYcbbData(JdbcTemplate jdbcTemplate,String saveno,String zero,int type, LocalDate rightnow){
        JSONObject data = new JSONObject();
        JSONObject daydata = new JSONObject();
        Map<String,Object> map =null;//new HashMap<>();// (HashMap)userData.get(i);
        int gno,vno;
        LocalDate st,et;



        st = rightnow.withDayOfMonth(1);
        et = rightnow.withDayOfMonth(rightnow.lengthOfMonth() );
        gno = Integer.parseInt(saveno)/200;
        vno = Integer.parseInt(saveno) % 200;
        float avg=0.0f;
        String sql="select (floor(savetime/10000) mod 100) dd,ROUND(avg(val"+vno+"),2) vv from hyc"+(rightnow.getYear()%10)+" where ABS(val"+vno+")"+zero_condition+zero+" and  groupno="+gno+" and savetime between "+df.format(st)+"0000 and "+df.format(et)+"2400 group by (floor(savetime/10000) mod 100) ";
       // logger.warn(sql);
        //if(rand.equalsIgnoreCase(imagerand)){
        try{
            List<Map<String, Object>> userData = jdbcTemplate.queryForList(sql);
            int size=userData.size() ;
            if (size> 0)
            {

                for (int i = 0; i<size; i++)
                {

                    map = (HashMap) userData.get(i);

                    avg = avg+Float.parseFloat(map.get("vv").toString());
                    daydata.put(map.get("dd").toString().replace(".0",""), map.get("vv").toString());

                }
                //map.clear();
                avg = avg/size;
                HashMap<String,Object> map2 = new HashMap<>();

                map2.put("avg",decimalFormat.format(avg));
                data.put("avg",  map2);
                data.put("daydata",  daydata);

            }

        }catch(Exception e){
            logger.warn("出错了"+e.toString());
            e.printStackTrace();
        }
        sql="select maxv,maxt from everyday a where not EXISTS (select 1 from everyday b where a.saveno=b.saveno and b.maxv>a.maxv and a.tdate and DATE_FORMAT(a.tdate,'%Y%m')=DATE_FORMAT(b.tdate,'%Y%m')) and a.saveno="+saveno+" and DATE_FORMAT(a.tdate,'%Y%m')='"+df2.format(rightnow)+"'";
//        logger.warn(sql);
        //if(rand.equalsIgnoreCase(imagerand)){
        try{
            List<Map<String, Object>> userData = jdbcTemplate.queryForList(sql);
            int size=userData.size() ;
            if (size> 0)
            {

                for (int i = 0; i<size; i++)
                {

                    map = (HashMap) userData.get(i);


                    data.put("max",  map);

                }



            }

        }catch(Exception e){
            logger.warn("出错了"+e.toString());
            e.printStackTrace();
        }
        sql="select minv,mint from everyday a where not EXISTS (select 1 from everyday b where a.saveno=b.saveno and b.minv<a.minv and a.tdate and DATE_FORMAT(a.tdate,'%Y%m')=DATE_FORMAT(b.tdate,'%Y%m')) and a.saveno="+saveno+" and DATE_FORMAT(a.tdate,'%Y%m')='"+df2.format(rightnow)+"'";
        //logger.warn(sql);
        //if(rand.equalsIgnoreCase(imagerand)){
        try{
            List<Map<String, Object>> userData = jdbcTemplate.queryForList(sql);
            int size=userData.size() ;
            if (size> 0)
            {

                for (int i = 0; i<size; i++)
                {

                    map = (HashMap) userData.get(i);


                    data.put("min",  map);

                }



            }

        }catch(Exception e){
            logger.warn("出错了"+e.toString());
            e.printStackTrace();
        }
        return data;
    }
    /**
     * 匹配是否包含数字
     * @param str 可能为中文，也可能是-19162431.1254，不使用BigDecimal的话，变成-1.91624311254E7
     * @return
     * @author xx
     * @date
     */
    public static boolean isNumeric(String str) {
        // 该正则表达式可以匹配所有的数字 包括负数
        Pattern pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*");
        String bigStr;
        try {
            bigStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            return false;//异常 说明包含非数字。
        }

        Matcher isNum = pattern.matcher(bigStr); // matcher是全匹配
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
    @Override
    public void run()  {

        logger.warn("正在执行bbtask "+bbname);

        JSONObject tdata = null,daydata=null;
        String cellstr = null,vv=null,zerostr=null;
        String exportFilePath = reportPath+rightnow.getYear()+"/"+rightnow.getMonthValue()+"/"+bbname;
        logger.warn(exportFilePath);
        FileInputStream is = null;

        try {
            is = new FileInputStream(file);
            //System.out.println(is.available());
            if (file.getName().endsWith(".xls")) {
                // Excel2003及以前版本
                workbook = new HSSFWorkbook(is);
            } else {
                // Excel2007及以后版本
                try {
                    workbook = new XSSFWorkbook(is);
                } catch (Exception e) {
                    System.out.println(e.toString());
                }

            }



            /**
             *  Font ztFont = wb.createFont();
             *             ztFont.setItalic(true);                     // 设置字体为斜体字
             *             ztFont.setColor(Font.COLOR_RED);            // 将字体设置为“红色”
             *             ztFont.setFontHeightInPoints((short)22);    // 将字体大小设置为18px
             *             ztFont.setFontName("华文行楷");             // 将“华文行楷”字体应用到当前单元格上
             *             ztFont.setUnderline(Font.U_DOUBLE);         // 添加（Font.U_SINGLE单条下划线/Font.U_DOUBLE双条下划线）
             * //          ztFont.setStrikeout(true);                  // 是否添加删除线
             *             ztStyle.setFont(ztFont);                    // 将字体应用到样式上面
             *             ztCell.setCellStyle(ztStyle);               // 样式应用到该单元格上
             *
             *             //============================
             *             //        设置单元格边框
             *             //============================
             *             Row borderRow = sheet.createRow(2);
             *             Cell borderCell = borderRow.createCell(1);
             *             borderCell.setCellValue("中国");
             *             // 创建单元格样式对象
             *             XSSFCellStyle borderStyle = (XSSFCellStyle)wb.createCellStyle();
             *             // 设置单元格边框样式
             *             // CellStyle.BORDER_DOUBLE      双边线
             *             // CellStyle.BORDER_THIN        细边线
             *             // CellStyle.BORDER_MEDIUM      中等边线
             *             // CellStyle.BORDER_DASHED      虚线边线
             *             // CellStyle.BORDER_HAIR        小圆点虚线边线
             *             // CellStyle.BORDER_THICK       粗边线
             *             borderStyle.setBorderBottom(CellStyle.BORDER_THICK);
             *             borderStyle.setBorderTop(CellStyle.BORDER_DASHED);
             *             borderStyle.setBorderLeft(CellStyle.BORDER_DOUBLE);
             *             borderStyle.setBorderRight(CellStyle.BORDER_THIN);
             *
             *             // 设置单元格边框颜色
             *             borderStyle.setBottomBorderColor(new XSSFColor(java.awt.Color.RED));
             *             borderStyle.setTopBorderColor(new XSSFColor(java.awt.Color.GREEN));
             *             borderStyle.setLeftBorderColor(new XSSFColor(java.awt.Color.BLUE));
             *
             *             borderCell.setCellStyle(borderStyle);
             *
             *             //============================
             *             //      设置单元内容的对齐方式
             *             //============================
             *             Row alignRow = sheet.createRow(4);
             *             Cell alignCell = alignRow.createCell(1);
             *             alignCell.setCellValue("中国");
             *
             *             // 创建单元格样式对象
             *             XSSFCellStyle alignStyle = (XSSFCellStyle)wb.createCellStyle();
             *
             *             // 设置单元格内容水平对其方式
             *             // XSSFCellStyle.ALIGN_CENTER       居中对齐
             *             // XSSFCellStyle.ALIGN_LEFT         左对齐
             *             // XSSFCellStyle.ALIGN_RIGHT        右对齐
             *             alignStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
             *
             *             // 设置单元格内容垂直对其方式
             *             // XSSFCellStyle.VERTICAL_TOP       上对齐
             *             // XSSFCellStyle.VERTICAL_CENTER    中对齐
             *             // XSSFCellStyle.VERTICAL_BOTTOM    下对齐
             *             alignStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
             *
             *             alignCell.setCellStyle(alignStyle);
             *
             *             //============================
             *             //      设置单元格的高度和宽度
             *             //============================
             *             Row sizeRow = sheet.createRow(6);
             *             sizeRow.setHeightInPoints(30);                  // 设置行的高度
             *
             *             Cell sizeCell = sizeRow.createCell(1);
             *             String sizeCellValue = "《Java编程思想》";            // 字符串的长度为10，表示该字符串中有10个字符，忽略中英文
             *             sizeCell.setCellValue(sizeCellValue);
             *             // 设置单元格的长度为sizeCellVlue的长度。而sheet.setColumnWidth使用sizeCellVlue的字节数
             *             // sizeCellValue.getBytes().length == 16
             *             sheet.setColumnWidth(1, (sizeCellValue.getBytes().length) * 256 );
             *
             *             //============================
             *             //      设置单元格自动换行
             *             //============================
             *             Row wrapRow = sheet.createRow(8);
             *             Cell wrapCell = wrapRow.createCell(2);
             *             wrapCell.setCellValue("宝剑锋从磨砺出,梅花香自苦寒来");
             *
             *             // 创建单元格样式对象
             *             XSSFCellStyle wrapStyle = (XSSFCellStyle)wb.createCellStyle();
             *             wrapStyle.setWrapText(true);                    // 设置单元格内容是否自动换行
             *             wrapCell.setCellStyle(wrapStyle);
             *
             *             //============================
             *             //         合并单元格列
             *             //============================
             *             Row regionRow = sheet.createRow(12);
             *             Cell regionCell = regionRow.createCell(0);
             *             regionCell.setCellValue("宝剑锋从磨砺出,梅花香自苦寒来");
             *
             *             // 合并第十三行中的A、B、C三列
             *             CellRangeAddress region = new CellRangeAddress(12, 12, 0, 2); // 参数都是从O开始
             *             sheet.addMergedRegion(region);
             *
             *             //============================
             *             //         合并单元格行和列
             *             //============================
             *             Row regionRow2 = sheet.createRow(13);
             *             Cell regionCell2 = regionRow2.createCell(3);
             *             String region2Value = "宝剑锋从磨砺出,梅花香自苦寒来。"
             *                                 + "采得百花成蜜后,为谁辛苦为谁甜。"
             *                                 + "操千曲而后晓声,观千剑而后识器。"
             *                                 + "察己则可以知人,察今则可以知古。";
             *             regionCell2.setCellValue(region2Value);
             *
             *             // 合并第十三行中的A、B、C三列
             *             CellRangeAddress region2 = new CellRangeAddress(13, 17, 3, 7); // 参数都是从O开始
             *             sheet.addMergedRegion(region2);
             *
             *             XSSFCellStyle region2Style = (XSSFCellStyle)wb.createCellStyle();
             *             region2Style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
             *             region2Style.setWrapText(true);                     // 设置单元格内容是否自动换行
             *             regionCell2.setCellStyle(region2Style);
             */

            Sheet sheet = workbook.getSheetAt(0);
            int colNum = sheet.getRow(0).getLastCellNum();
            //logger.warn(colNum);
            int rowNum = sheet.getLastRowNum();
            if(sheet == null) {
                return;
            }

            //遍历col
            for(int jcol= 1;jcol < colNum;jcol++) {

                int irow = 3;

                Cell cell = sheet.getRow(0).getCell(jcol);
                if (ObjectUtils.allNotNull(cell)) {
                    cellstr = getCellValue(cell);
                    cell.setCellValue("");

                    cell = sheet.getRow(1).getCell(jcol);
                    zerostr = getCellValue(cell);
                    cell.setCellValue("");
                    //logger.warn(cellstr+","+zerostr);
                    if (isNumeric(cellstr)) {
                        tdata = getYcbbData(jdbcTemplate, cellstr, zerostr, 1, rightnow);
                        //logger.warn(jcol+":"+getCellValue(cell)+":"+tdata);
                        if (tdata.containsKey("daydata")) {
                            daydata = tdata.getJSONObject("daydata");
                            // logger.warn(daydata);
                            for (String str : daydata.keySet()) {
                                Cell daycell = sheet.getRow(Integer.parseInt(str) + irow).getCell(jcol);
                                daycell.setCellValue(daydata.get(str).toString());
                                // System.out.println(str + ":" +daydata.get(str));

                            }
                            irow = irow + 32;

                            while (irow <= rowNum) {
                                cell = sheet.getRow(irow).getCell(jcol);
                                cellstr = getCellValue(cell);
                                vv = "";
                                if (cellstr != null) {
                                    switch (cellstr) {
                                        case "#04":
                                            if (tdata.containsKey("max"))
                                                vv = ((HashMap) tdata.get("max")).get("maxv").toString();
                                            break;
                                        case "#05":
                                            if (tdata.containsKey("min"))
                                                vv = ((HashMap) tdata.get("min")).get("minv").toString();
                                            break;
                                        case "#06":
                                            if (tdata.containsKey("avg"))
                                                vv = ((HashMap) tdata.get("avg")).get("avg").toString();

                                            break;
                                        case "#08":
                                            if (tdata.containsKey("max"))
                                                vv = ((HashMap) tdata.get("max")).get("maxt").toString().substring(5);
                                            break;
                                        case "#09":
                                            if (tdata.containsKey("min"))
                                                vv = ((HashMap) tdata.get("min")).get("mint").toString().substring(5);
                                            break;
                                        default:
                                            vv = "";
                                            break;
                                    }
                                    cell.setCellValue(vv);
                                }
                                irow++;
                            }
                        }


                    }


                }
                /*for(int cellNum=0;cellNum <= hssfRow.getLastCellNum();cellNum++) {
                    Cell hssfcell=hssfRow.getCell(cellNum);
                    if(hssfcell== null) {
                        continue;
                    }
                    System.out.print(" "+getCellValue(hssfcell));
                }
                System.out.println();*/
            }

            CellRangeAddress region = new CellRangeAddress(0, 0, 0, colNum-1);
            sheet.addMergedRegion(region);
            sheet.getRow(0).setHeightInPoints(30);
            Cell cell = null;
            cell = sheet.getRow(1).getCell(0);
            if (!ObjectUtils.allNotNull(cell)) cell = sheet.getRow( 1).createCell(0);
            cell.setCellValue(df3.format(rightnow));
            region = new CellRangeAddress(1, 1, 0, colNum-1);
            sheet.addMergedRegion(region);

            XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();//创建样式
            style.setAlignment(HorizontalAlignment.CENTER);
            Font ztFont = workbook.createFont();
            ztFont.setFontHeightInPoints((short)22);
            ztFont.setFontName("华文行楷");
            style.setFont(ztFont);
            cell = sheet.getRow(0).getCell(0);
            cell.setCellStyle(style);

            FileOutputStream fileOut = null;
            try {

                File exportFile = new File(exportFilePath);
                if (!exportFile.exists()) {
                    exportFile.createNewFile();
                }

                fileOut = new FileOutputStream(exportFilePath);
                workbook.write(fileOut);
                fileOut.close();
            } catch (Exception e) {
                logger.warn("输出Excel时发生错误，错误原因：" + e.getMessage());
            } finally {
                try {
                    if (null != fileOut) {
                        fileOut.close();
                    }
                    if (null != workbook) {
                        workbook.close();
                    }
                } catch (IOException e) {
                    logger.warn("关闭输出流时发生错误，错误原因：" + e.getMessage());
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        logger.warn("bbtask "+bbname+"执行完毕");

        try {
            excel2Pdf.excel2pdf(exportFilePath,exportFilePath.replace("XLSX","pdf"));
            logger.warn("bbtask "+bbname+"转换完毕");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}

