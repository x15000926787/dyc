package com.dl.tool;

import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.formula.eval.FunctionEval;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.*;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*

import org.apache.poi.ss.formula.eval.FunctionEval;
 */

//

//

/**
 * Desc:报表任务，读取模板列表，分配线程
 * Created by xx on 2020/04/13.
 */
public  class ReportDycJob implements Job {
	private static final org.apache.logging.log4j.Logger logger = FirstClass.logger;
	public  String reportPath ,bbname;
	File file = null;
	String bbid ;//tmap.get("id").toString();
	String bbtype ;//= tmap.get("type").toString();
	//public DBConnection dbcon=new DBConnection();
	File exportFile;
	ResultSet rs=null;
	LocalDate rightnow = LocalDate.now().minusDays(1);
	LocalDate m1 = rightnow.with(TemporalAdjusters.firstDayOfMonth());
	LocalDate m2 = rightnow.plusDays(1);//LocalDate.now();
	List<Map<String, Object>> rptlist = new ArrayList<Map<String, Object>>();
	DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	//private static final Logger logger = LogManager.getLogger(ReportDailyTask.class);
	private Workbook workbook;
	DateTimeFormatter dfm = DateTimeFormatter.ofPattern("MMdd");
	DateTimeFormatter df2 = DateTimeFormatter.ofPattern("MM");
	DateTimeFormatter df3 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	DateTimeFormatter df4 = DateTimeFormatter.ofPattern("yyyy-MM");
	DateTimeFormatter df5 = DateTimeFormatter.ofPattern("yyyy");
	private int hv = 1;
	Float vv = 0.0f;
	JSONObject tdata = null;
	String cellstr = null;
	//float vv=null;
	String zerostr=null;
	String exportFilePath = null;
	public static JdbcTemplate jdbcTemplate;
	public ThreadPoolExecutor executor = null;
	private Excel2Pdf excel2Pdf = new Excel2Pdf();
	public ReportDycJob()
	   {
		   reportPath = PropertyUtil.getProperty("reportPath");
		   jdbcTemplate=FirstClass.jdbcTemplate;
		  // executor = FirstClass.executor;

	   }

	private static void copyFileUsingFileStreams(File source, File dest)
			throws IOException {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(source);
			output = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
		} finally {
			input.close();
			output.close();
		}
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
	private static void updateFormula(Workbook wb, Sheet s, int row){
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
				try {
					eval.evaluateFormulaCell(c);
				}catch (Exception e)
				{
					logger.warn(e.toString());
				}

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
	public JSONObject getMaxMinData(JdbcTemplate jdbcTemplate, String saveno, int type, LocalDate rightnow){
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
				case "999":
					data.put("0",df3.format(m1)+" - "+df3.format(m2.minusDays(1)));
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
				rightnow = rightnow.minusDays(1);
				break;
			case 1:
				rightnow = rightnow.minusDays(1);
				rightnow = rightnow.minusMonths(1);
				break;
			case 2:
				rightnow = rightnow.minusDays(1);
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
					sql="select (savetime mod 10000) dd,TRUNCATE(ifnull(val"+vno+",0),2) vv from "+bbname+" where (savetime mod 10000) in ("+PropertyUtil.getProperty("data_points_"+code[5],"0")+") and  groupno="+gno+" and savetime between "+dfm.format(rightnow)+"0000 and "+dfm.format(rightnow)+"2400 order by (savetime mod 10000)";
					//logger.warn(sql);
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
			case "11":
				bbname = "hyc"+rightnow.getYear()%10;
				if (dtp==0)                  //固定点值电表抄表值
				{
//logger.warn(code[6]);CASE sva
//WHEN 1 THEN '男'
//　　ELSE '女'
//END AS
					// sql="select mod(floor(savetime/10000),100)-"+code[6]+" dd,ifnull(val"+vno+",0) vv from "+bbname+" where (savetime mod 10000) in ("+PropertyUtil.getProperty("data_points_"+code[5],"0")+") and  groupno="+gno+" and savetime between "+df2.format(rightnow)+code[6]+"0000 and "+df2.format(rightnow)+code[7]+"2400 order by mod(floor(savetime/10000),100)";
					sql="select case savetime when "+Integer.parseInt(dfm.format(m1))+"0000 then 0 else 1 end as  dd,floor(ifnull(val"+vno+",0)) vv from "+bbname+" where (savetime mod 10000) in (0) and  groupno="+gno+" and savetime in ("+dfm.format(m1)+"0000,"+dfm.format(m2)+"0000) order by mod(floor(savetime/10000),100)";

					 logger.warn(sql);
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
				switch ( dtp) {
					case (0) :                 //maxv
					{
						sql = "select 0 dd,ifnull(maxv,0) vv from " + bbname + " where   saveno=" + saveno + " and str_to_date('" + df3.format(rightnow) + "','%Y-%m-%d') =tdate";
						break;
					}
					case (201):                   //大悦城特例，此处根据一个存档号查询连续的三个数据点的极值
					{
						sql = "select 0 dd,max(ifnull(maxv,0)) vv from " + bbname + " where   (saveno between " + saveno + " and " + saveno + "+2) and str_to_date('" + df3.format(rightnow) + "','%Y-%m-%d') =tdate";
						//logger.warn(sql);
						break;
					}
					case ( 1) :                  //maxt
					{
						sql = "select 0 dd,ifnull(maxt,0) vv from " + bbname + " where   saveno=" + saveno + " and str_to_date('" + df3.format(rightnow) + "','%Y-%m-%d') =tdate";
						break;
					}
					case (2) :                  //minv
					{
						sql = "select 0 dd,ifnull(minv,0) vv from " + bbname + " where   saveno=" + saveno + " and str_to_date('" + df3.format(rightnow) + "','%Y-%m-%d') =tdate";
						break;
					}
					case (3):                   //mint
					{
						sql = "select 0 dd,ifnull(mint,0) vv from " + bbname + " where   saveno=" + saveno + " and str_to_date('" + df3.format(rightnow) + "','%Y-%m-%d') =tdate";
						break;
					}
					case (4) :                  //月极值，极值时间
					{
						sql = "select Day(tdate) dd,ifnull(maxv,0) vv from " + bbname + " where   saveno=" + saveno + " and  tdate between '" + df3.format(rightnow.withDayOfMonth(1)) + "' and '" + df3.format(rightnow.with(TemporalAdjusters.lastDayOfMonth())) + "'";
						break;
					}
					case (202) :                  //月极值，极值时间   大悦城特例，此处根据一个存档号查询连续的三个数据点的极值
					{
						sql = "select Day(tdate)-1 dd,max(ifnull(maxv,0)) vv from " + bbname + " where   (saveno between " + saveno + " and " + saveno + "+2) and  tdate between '" + df3.format(rightnow.withDayOfMonth(1)) + "' and '" + df3.format(rightnow.with(TemporalAdjusters.lastDayOfMonth())) + "' group by Day(tdate)";
						break;
					}
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
								logger.warn(calccode+","+sql);
							}
						}else
						{
							try {
								data.put(map.get("dd").toString().replace(".0",""), map.get("vv").toString());
							}catch (Exception e)
							{
								data.put(String.valueOf(i), "");
							}
							//logger.warn(calccode+","+sql);
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
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

		HashMap paraobj =(HashMap) (jobExecutionContext.getJobDetail().getJobDataMap().get("taskdetial"));
		if(paraobj!=null) {
			if (paraobj.containsKey("dtstr")) {
				rightnow = LocalDate.parse(paraobj.get("dtstr").toString(), df);
				m1 = rightnow.with(TemporalAdjusters.firstDayOfMonth());
				m2 = m1.plusMonths(1);//LocalDate.now();
				FirstClass.logger.warn((int) ChronoUnit.DAYS.between(LocalDate.now(),m2));
				if ((int) ChronoUnit.DAYS.between(LocalDate.now(),m2)>0)
					m2 = LocalDate.now();
			}
		}
		try {
			file = new File(reportPath+"/"+rightnow.getYear());
			if (!file.exists()) {
				file.mkdirs();
			}
			file = new File(reportPath+"/"+rightnow.getYear()+"/"+rightnow.getMonthValue());
			if (!file.exists()) {
				file.mkdirs();
			}
			file = new File(reportPath+"/"+rightnow.getYear()+"/"+rightnow.getMonthValue()+"/"+rightnow.getDayOfMonth());
			if (!file.exists()) {
				file.mkdirs();
			}

			/*file = new File(reportPath+"\\bb");
			if (file.isDirectory()) {

				File[] fileArr = file.listFiles(); // 遍历目录,只遍历直接子目录。
				for(File f : fileArr){
					bbname = f.getName();
					if
				}


			}*/



			String sql = "select * from rptlist where valid=1 and type in (4,5) order by id";
			try{
				rptlist= jdbcTemplate.queryForList(sql);

				for(Map<String, Object> tmap : rptlist)
				{
					 bbname = (String)tmap.get("filename")+".xlsx";
					 bbid = tmap.get("id").toString();
					 bbtype = tmap.get("type").toString();
					 file = new File(reportPath+"/mb/"+bbname);





					//ReportDailyTask reportDailyTask = new ReportDailyTask(file,bbname,bbid,tmap.get("type").toString(),jdbcTemplate,rightnow);
					//if (executor.getQueue().size()<100)
					/*executor.execute(new Runnable() {

						@Override
						public void run() {*/


							logger.warn("正在执行bbtask "+bbname);
							exportFilePath = reportPath+"/"+rightnow.getYear()+"/"+rightnow.getMonthValue()+"/"+bbid;
							if (bbtype.matches("5"))
							{
								exportFilePath = reportPath+"/"+rightnow.getYear()+"/"+rightnow.getMonthValue()+"/"+rightnow.getDayOfMonth()+"/"+bbid;
								//exportPdfPath = reportPath+"/"+rightnow.getYear()+"/"+rightnow.getMonthValue()+"/"+rightnow.getDayOfMonth()+"/"+bbid;
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


								Sheet sheet = workbook.getSheetAt(0);
								int colNum = sheet.getRow(0).getLastCellNum();

								int rowNum = sheet.getLastRowNum();
								//logger.warn(rowNum+","+colNum);
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
															daycell.setCellType(CellType.NUMERIC);
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

								XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
								//updateFormula(workbook,sheet,5);
								FileOutputStream fileOut = null;
								try {

									exportFile = new File(exportFilePath+".xlsx");
									if (!exportFile.exists()) {
										exportFile.createNewFile();
									}

									fileOut = new FileOutputStream(exportFilePath+".xlsx");
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
								excel2Pdf.excel2pdf(exportFilePath+".xlsx",exportFilePath+".pdf");
								logger.warn("bbtask "+bbname+"转换完毕");
							}catch (Exception e) {
								e.printStackTrace();
							}


						/*}
					});*/
				}

			}catch(Exception e){
				logger.warn("出错了"+e.toString());
				e.printStackTrace();
			}


		} catch (Exception e) {
			logger.warn("readfile()   Exception:" + e.getMessage());
		}

	}
}
