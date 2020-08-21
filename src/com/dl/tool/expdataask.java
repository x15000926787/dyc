package com.dl.tool;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
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
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @des    报表处理
 * @author xuxu
 * @create 2020-04-14 14:31
 */
public class expdataask implements Runnable {
    int yy;
    String saveno;
    String path;
    String ip;
    public static  String IP="218.78.61.13";
    //数据库用户名
    private static  String USERNAME = "shcs";


    //数据库密码
    private static  String PASSWORD = "Shcs123";
    //驱动信息
    private static  String DRIVER = "com.mysql.jdbc.Driver";
    //数据库地址
    private static  String URL = "jdbc:mysql://"+IP+":3306/scada";
    private  Connection connection;
    private  PreparedStatement pstmt;
    private  ResultSet resultSet;
    private File file;


    private static final Logger logger = FirstClass.logger;
    private Workbook workbook;
    Cell cell = null;
    int gno=0;
    int vno=0;
    int sav=0;
    //private ScriptEngine scriptEngine ;// scriptEngineManager.getEngineByName("nashorn");
    public expdataask(int yy, String saveno, String path,String ip) throws IOException {
        this.yy = yy;
        this.saveno = saveno;
        this.path = path;
        this.ip = ip;


       // this.scriptEngine = scriptEngine;
        //this.tjedis = tjedis;
    }



    @Override
    public void run()  {

        logger.warn("正在执行bbtask "+saveno);
        try{
            Class.forName(DRIVER);
            try {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //System.out.println("数据库连接成功！");

        }catch(Exception e){

        }
        sav = Integer.parseInt(saveno);
        gno = (int)sav/200;
        vno = sav % 200;
        file=new File(path+"/"+ip+"/"+yy);
        if(!file.exists()){//如果文件夹不存在
            file.mkdir();//创建文件夹
        }
        int j;
        file = new File(path+"/"+"data.xlsx");
        FileInputStream is = null;


        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            workbook = new XSSFWorkbook(is);

        } catch (Exception e) {
            System.out.println("ReportDailyTask 362 :"+e.toString());
        }


        Sheet sheet = workbook.getSheetAt(0);
        try {
            //points = FirstClass.getJdbcTemplate().queryForList();//jdbcutils.findModeResult("select savetime,val"+vno+"  from hyc"+(yy % 10)+" where val"+vno+" is not null and groupno="+gno,null);
            List<Map<String, Object>> points = new ArrayList<Map<String, Object>>();
            int index = 0;
            pstmt = connection.prepareStatement("select savetime,val"+vno+"  from hyc"+(yy % 10)+" where chgtime is not null and groupno="+gno);

            resultSet = pstmt.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int cols_len = metaData.getColumnCount();
            while(resultSet.next()){
                Map<String, Object> map = new HashMap<String, Object>();
                for(int i=0; i<cols_len; i++){
                    String cols_name = metaData.getColumnName(i+1);
                    String cols_value = resultSet.getObject(cols_name).toString();
                    if(StringUtils.isEmpty(cols_value)){
                        cols_value = "";
                    }
                    try {
                        cell=sheet.getRow(index+1).createCell(i);
                    }catch (Exception e)
                    {
                        cell=sheet.createRow(index+1).createCell(i);
                    }

                    cell.setCellValue(cols_value);
                    //map.put(cols_name, cols_value);
                }
                //points.add(map);
                index++;
            }

        } catch (Exception e) {
            FirstClass.logger.warn("select savetime,val"+vno+"  from hyc"+(yy % 10)+" where val"+vno+" is not null and groupno="+gno);
            e.printStackTrace();
        }


        //FirstClass.logger.warn(sheet.toString());



        FileOutputStream fileOut = null;
        try {

            fileOut = new FileOutputStream(path+"/"+ip+"/"+yy+"/"+saveno+".xlsx");
            workbook.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            logger.warn("输出Excel时发生错误，错误原因：" + e.toString());
        } finally {
            try {
                if (null != fileOut) {
                    fileOut.close();
                }
                if (null != workbook) {
                    workbook.close();
                }
            } catch (IOException e) {
                logger.warn("关闭输出流时发生错误，错误原因：" + e.toString());
            }
        }


        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (!connection.isClosed())
            {
                try{
                    connection.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(resultSet != null){
            try{
                resultSet.close();
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
        logger.warn("bbtask "+saveno+"执行完毕");


    }
}

