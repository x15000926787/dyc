package com.dl.tool;

import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
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
public class ReportDailyTask implements Runnable {
    private String bbname;
    private String bbid;
    private String bbtype,reportPath,data_points;
    private File file;
    private Excel2Pdf excel2Pdf = new Excel2Pdf();
    private JdbcTemplate jdbcTemplate;
    public static LocalDate rightnow;

    private static final Logger logger = LogManager.getLogger(ReportDailyTask.class);
    private Workbook workbook;
    DateTimeFormatter df = DateTimeFormatter.ofPattern("MMdd");
    DateTimeFormatter df2 = DateTimeFormatter.ofPattern("MM");
    DateTimeFormatter df3 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter df4 = DateTimeFormatter.ofPattern("yyyy-MM");
    DateTimeFormatter df5 = DateTimeFormatter.ofPattern("yyyy");
    private int hv = 1;
    //private ScriptEngine scriptEngine ;// scriptEngineManager.getEngineByName("nashorn");
    public ReportDailyTask(File file, String bbname,String bbid, String bbtype, JdbcTemplate jdbcTemplate, LocalDate rightnow) throws IOException {
        this.bbname = bbname;
        this.bbid = bbid;
        this.bbtype = bbtype;
        this.jdbcTemplate = jdbcTemplate;
        this.rightnow = rightnow;
        //;
       // LocalDate parse = LocalDate.parse("2020-01-15", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
       // this.rightnow = parse;
        this.file = file;
        reportPath = PropertyUtil.getProperty("reportPath");
        data_points = PropertyUtil.getProperty("data_points","0");

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
        try {
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
        }catch (Exception e)
        {
            cellValue = "0";
            logger.warn(cell.getRowIndex()+","+cell.getColumnIndex());
        }
        return cellValue;
    }
    private static void updateFormula(Workbook wb,Sheet s,int row){
        Row r=s.getRow(row);
        Cell c=null;
        XSSFFormulaEvaluator eval=null;
        /*if(wb instanceof HSSFWorkbook)
            eval=new HSSFFormulaEvaluator((HSSFWorkbook) wb);
        else if(wb instanceof XSSFWorkbook)*/
            eval=new XSSFFormulaEvaluator((XSSFWorkbook) wb);
        for(int i=r.getFirstCellNum();i<r.getLastCellNum();i++){
            c=r.getCell(i);
            if(c.getCellType()==CellType.FORMULA) {
               // logger.warn(i);
                eval.evaluateFormulaCell(c);
            }
        }
    }
    /**
     *
     * @param jdbcTemplate
     * @param saveno
     * @param type    预留：1，最大值，2：最小值
     * @param rightnow
     * @return
     */
    public JSONObject getMaxMinData(JdbcTemplate jdbcTemplate,String saveno,int type, LocalDate rightnow){
        JSONObject data = new JSONObject();
        //JSONObject daydata = new JSONObject();
        Map<String,Object> map =null;//new HashMap<>();// (HashMap)userData.get(i);

        String tt=null;
        switch(type){
            case 1:
                tt="maxv";
                break;
            case 2:
                tt="maxt";
                break;
            case 3:
                tt="minv";
                break;
            case 4:
                tt="mint";
                break;
            default:
                break;

        }




        //st = rightnow.withDayOfMonth(1);
        //et = rightnow.withDayOfMonth(rightnow.lengthOfMonth() );


        String sql="select "+tt+" vv from everyday where saveno="+saveno+" and  str_to_date('"+df3.format(rightnow)+"','%Y-%m-%d') =tdate ";
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

                    //avg = avg+Float.parseFloat(map.get("vv").toString());
                    data.put(String.valueOf(i), map.get("vv").toString());

                }
                //map.clear();


                //data.put("daydata",  data);

            }

        }catch(Exception e){
            logger.warn("出错了"+e.toString());
            e.printStackTrace();
        }


        return data;
    }
    /**
     *
     * @param jdbcTemplate
     * @param calccode
     * @param rightnow
     * @return
     */
    public JSONObject getdailyData(JdbcTemplate jdbcTemplate,String calccode,LocalDate rightnow){
        JSONObject data = new JSONObject();
        //JSONObject daydata = new JSONObject();
        Map<String,Object> map =null;//new HashMap<>();// (HashMap)userData.get(i);
        int gno,vno,timetype;
        String tcode = calccode;//.substring(1);
        String[] code = tcode.split(",");
        String saveno = code[0];

        if (saveno.startsWith("T"))
        {
            switch (saveno.substring(1))
            {
                case "101":
                    data.put("0",df4.format(rightnow));
                    break;
                case "100":
                    data.put("0",df3.format(rightnow));
                    break;
                case "102":
                    hv = 1;
                    data.put("0",df5.format(rightnow));
                    data.put("1",df5.format(rightnow.minusYears(1)));
                    break;
                default:
                    break;
            }
            //logger.warn(data.toJSONString());
            return data;
        }

        timetype=Integer.parseInt(code[3]);
        hv = Integer.parseInt(code[2]);
        //logger.warn(hv);
        switch (timetype){
            case 0:
                rightnow=rightnow.minusDays(1);
                break;
            case 1:
                rightnow=rightnow.minusMonths(1);
                break;
            case 2:
                rightnow = rightnow.minusYears(1);
            default:
                break;
        }
        String bbname = null;
        String sql="";
        gno = Integer.parseInt(saveno)/200;
        vno = Integer.parseInt(saveno) % 200;
        int dtp = Integer.parseInt(code[4]);
        //logger.warn(code[1]);
        switch(code[1])
        {
            case "0":
                bbname = "hyc"+rightnow.getYear()%10;
                if (dtp==0)                  //当日固定点值，从property文件读取时间点序列
                {
                    sql="select (savetime mod 10000) dd,TRUNCATE(ifnull(val"+vno+",0),2) vv from "+bbname+" where (savetime mod 10000) in ("+PropertyUtil.getProperty("data_points_"+code[5],"0")+") and  groupno="+gno+" and savetime between "+df.format(rightnow)+"0000 and "+df.format(rightnow)+"2400 order by (savetime mod 10000)";

                }
                if(dtp==1)                   //当月日平均
                {

                }
                break;
            case "1":
                bbname = "hdn"+rightnow.getYear()%10;
                if (dtp==0)                  //固定点值电表抄表值
                {
//logger.warn(code[6]);
                   // sql="select mod(floor(savetime/10000),100)-"+code[6]+" dd,ifnull(val"+vno+",0) vv from "+bbname+" where (savetime mod 10000) in ("+PropertyUtil.getProperty("data_points_"+code[5],"0")+") and  groupno="+gno+" and savetime between "+df2.format(rightnow)+code[6]+"0000 and "+df2.format(rightnow)+code[7]+"2400 order by mod(floor(savetime/10000),100)";
                    sql="select mod(floor(savetime/10000),100)-"+code[6]+" dd,floor(ifnull(val"+vno+",0)) vv from "+bbname+" where (savetime mod 10000) in (0) and  groupno="+gno+" and savetime between "+df2.format(rightnow)+code[6]+"0000 and "+df2.format(rightnow)+code[7]+"2400 order by mod(floor(savetime/10000),100)";

                   // logger.warn(sql);
                }
                break;
            case "2":
                bbname = "hdz"+rightnow.getYear()%10;
                if (dtp==0)                  //日电量
                {
                    sql="select mod(floor(savetime/10000),100)-1 dd,floor(sum(ifnull(val"+vno+",0))) vv from "+bbname+" where   groupno="+gno+" and savetime between "+df2.format(rightnow)+"010000 and "+df2.format(rightnow)+"312400 group by mod(floor(savetime/10000),100)-1 order by mod(floor(savetime/10000),100)-1";

                }
                break;
            case "3":

                bbname = "everyday";
                if (dtp==0)                  //maxv
                {
                    sql="select 0 dd,ifnull(maxv,0) vv from "+bbname+" where   saveno="+saveno+" and str_to_date('"+df3.format(rightnow)+"','%Y-%m-%d') =tdate";
                }
                if (dtp==1)                  //maxv
                {
                    sql="select 0 dd,ifnull(maxt,0) vv from "+bbname+" where   saveno="+saveno+" and str_to_date('"+df3.format(rightnow)+"','%Y-%m-%d') =tdate";
                }
                if (dtp==2)                  //maxv
                {
                    sql="select 0 dd,ifnull(minv,0) vv from "+bbname+" where   saveno="+saveno+" and str_to_date('"+df3.format(rightnow)+"','%Y-%m-%d') =tdate";
                }
                if (dtp==3)                  //maxv
                {
                    sql="select 0 dd,ifnull(mint,0) vv from "+bbname+" where   saveno="+saveno+" and str_to_date('"+df3.format(rightnow)+"','%Y-%m-%d') =tdate";
                }
                if (dtp==4)                  //maxv
                {
                    sql="select Day(tdate) dd,ifnull(maxv,0) vv from "+bbname+" where   saveno="+saveno+" and  tdate between '"+df3.format(rightnow.withDayOfMonth(1))+"' and '"+df3.format(rightnow.with(TemporalAdjusters.lastDayOfMonth()))+"'";
                }
                break;
            case "4":
                bbname = "everymon";
                break;
            default:
                break;
        }




        //st = rightnow.withDayOfMonth(1);
        //et = rightnow.withDayOfMonth(rightnow.lengthOfMonth() );


        //        logger.warn(sql);
        //if(rand.equalsIgnoreCase(imagerand)){
        logger.warn(sql);
        try{
            List<Map<String, Object>> userData = jdbcTemplate.queryForList(sql);
            int size=userData.size() ;
            if (size> 0)
            {

                for (int i = 0; i<size; i++)
                {

                    map = (HashMap) userData.get(i);

                    //avg = avg+Float.parseFloat(map.get("vv").toString());
                    if (code[1].matches("0"))
                        data.put(String.valueOf(i), map.get("vv").toString());
                    else if (code[1].matches("3"))
                    {   if (dtp<4)
                        {
                            try {
                                if (Math.abs(Float.parseFloat(map.get("vv").toString()))>9999999)
                                {
                                    data.put(String.valueOf(i), "");
                                } else
                                    data.put(String.valueOf(i), map.get("vv").toString());
                            }catch (Exception e)
                            {
                                data.put(String.valueOf(i), "");
                            }
                        }else
                            {
                                data.put(map.get("dd").toString().replace(".0",""), map.get("vv").toString());
                            }

                    }
                    else
                    data.put(map.get("dd").toString().replace(".0",""), map.get("vv").toString());

                }
                //map.clear();


                //data.put("daydata",  data);

            }

        }catch(Exception e){
            logger.warn("出错了"+e.toString());
            e.printStackTrace();
        }

        //logger.warn(data.toJSONString());
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
    /**
     * 匹配是否目标存档号
     * @param str
     * @return
     * @author xx
     * @date
     */
    public static boolean isTheSaveno(String str) {
        // 该正则表达式可以匹配所有的数字 包括负数

        String s2 = "^[&].*";
        if (str.matches(s2))
        {
           return isNumeric(str.substring(1));
        }
        else
        return false;
    }

    public static boolean isCalcCode(String str){
        String s2 = "^[&].*";
        if (str!=null)
        {
            if (str.matches(s2))
                return true;
            else
                return false;
        }
        else
            return false;

    }
    @Override
    public void run()  {

        logger.warn("正在执行bbtask "+bbname);
        Float vv = 0.0f;
        JSONObject tdata = null;
        String cellstr = null;
        //float vv=null;
        String zerostr=null;
        String exportFilePath = reportPath+"/"+rightnow.getYear()+"/"+rightnow.getMonthValue()+"/"+bbid;
        String exportPdfPath = reportPath+"/"+rightnow.getYear()+"/"+rightnow.getMonthValue()+"/"+bbid;
        if (bbtype.matches("5"))
        {
            exportFilePath = reportPath+"/"+rightnow.getYear()+"/"+rightnow.getMonthValue()+"/"+rightnow.getDayOfMonth()+"/"+bbid;
            exportPdfPath = reportPath+"/"+rightnow.getYear()+"/"+rightnow.getMonthValue()+"/"+rightnow.getDayOfMonth()+"/"+bbid;
        }
        FileInputStream is = null;

        try {
            is = new FileInputStream(file);
            //System.out.println(is.available());

                // Excel2007及以后版本
                try {
                    workbook = new XSSFWorkbook(is);
                } catch (Exception e) {
                    System.out.println("ReportDailyTask 362 :"+e.toString());
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

            int rowNum = sheet.getLastRowNum();
            logger.warn(rowNum+","+colNum);
            if(sheet == null) {
                return;
            }

            //遍历col
            for(int jcol= 1;jcol < colNum;jcol++) {
                for (int irow=1;irow < rowNum;irow++) {


                    try {
                    Cell cell = sheet.getRow(irow).getCell(jcol);
                    cellstr = getCellValue(cell);
                       // logger.warn(cellstr);

                    if (isCalcCode(cellstr)) {
                        cell.setCellValue("");
                        cellstr = cellstr.substring(1);
                        //logger.warn(cellstr);
                        tdata = getdailyData(jdbcTemplate, cellstr, rightnow);

                            for (String str : tdata.keySet()) {
                                Cell daycell = null;
                                switch(hv)
                                {
                                    case 0:
                                        daycell = sheet.getRow( Integer.parseInt(str) +irow).getCell(jcol);
                                        if (daycell==null)
                                        daycell = sheet.getRow( Integer.parseInt(str) +irow).createCell(jcol);

                                        break;
                                    case 1:
                                        daycell = sheet.getRow( irow).getCell(Integer.parseInt(str) +jcol);
                                        if (daycell==null)
                                        daycell = sheet.getRow( irow).createCell(Integer.parseInt(str) +jcol);

                                        break;
                                    default:
                                        break;
                                }

                                try {
                                    try {
                                        vv = Float.parseFloat(tdata.get(str).toString());
                                        daycell.setCellValue(vv);
                                    }catch (Exception e)
                                    {
                                        daycell.setCellValue(tdata.get(str).toString());
                                    }

                                }catch (Exception e)
                                {

                                    logger.warn(tdata.get(str).toString());
                                }


                            }

                    }

                    }catch (Exception e)
                    {
                        //logger.error("read err:"+irow+","+jcol+"  --"+e.toString());
                    }

                }

            }
            //updateFormula(workbook,sheet,5);
           /* CellRangeAddress region = new CellRangeAddress(0, 0, 0, colNum-1);
            sheet.addMergedRegion(region);
            sheet.getRow(0).setHeightInPoints(30);
            Cell cell = sheet.getRow(1).getCell(0);
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
            cell.setCellStyle(style);*/

            FileOutputStream fileOut = null;
            try {

                File exportFile = new File(exportFilePath+".xlsx");
                if (!exportFile.exists()) {
                    exportFile.createNewFile();
                }

                fileOut = new FileOutputStream(exportFilePath+".xlsx");
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
            //
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
            excel2Pdf.excel2pdf(exportFilePath+".xlsx",exportPdfPath+".pdf");
            logger.warn("bbtask "+bbname+"转换完毕");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}

