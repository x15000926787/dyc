package com.dl.controller;

import com.alibaba.fastjson.JSON;
import com.dl.impl.UserDAOImpl;
import com.dl.tool.Tool;
import jxl.write.WriteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

//import javax.servlet.http.userId;

@Controller
public class TfController
{
  @Autowired
  private UserDAOImpl user;

  @RequestMapping("test")
  public void test(String userId, HttpServletResponse response)
  {
	   Tool.request(user.test(),response);
  }
  
  @RequestMapping({"executeCmd"})
  public void executeCmd(String strcmd, HttpServletResponse response) throws Exception
  {
	   Tool.request(user.executeCmd(strcmd),response);
  }
  @RequestMapping({"rbt"})
  public void rbt(HttpServletResponse response) throws IOException
  {
	   Tool.request(user.rbt(),response);
  }
  @RequestMapping({"setRedisVal"})
  public void setRedisVal(Integer optype,String key,String okey, String val,String hlimit,HttpServletRequest request,HttpServletResponse response)
  {
	 
	  java.util.Map<String,Object> map1 = new HashMap<String,Object>();  
		 String jsonString="";
		 String sess=null;
		 sess =  String.valueOf(request.getSession().getAttribute("LOGIN_SUCCESS"));
	  if (!"null".equals(sess) )
	    Tool.request( user.setRedisVal(optype,key,okey,val,hlimit,sess),response);
	  else {
		  map1.put("result",-2); 
		  jsonString = JSON.toJSONString(map1);
		  Tool.request( jsonString,response);
	}
  }
    @RequestMapping({"TimeTaskLock"})
    public void TimeTaskLock(String key,Integer type,HttpServletRequest request,HttpServletResponse response)
    {

        java.util.Map<String,Object> map1 = new HashMap<String,Object>();
        String jsonString="";
        String sess=null;
        sess =  String.valueOf(request.getSession().getAttribute("LOGIN_SUCCESS"));
        if (!"null".equals(sess) )
            Tool.request( user.TimeTaskLock(key,type,sess),response);
        else {
            map1.put("result",-2);
            jsonString = JSON.toJSONString(map1);
            Tool.request( jsonString,response);
        }
    }
  @RequestMapping({"linkaction"})
  public void linkaction(String key, String val,HttpServletRequest request,HttpServletResponse response)
  {
	 
	  java.util.Map<String,Object> map1 = new HashMap<String,Object>();  
		 String jsonString="";
		 String sess=null;
		 sess =  String.valueOf(request.getSession().getAttribute("LOGIN_SUCCESS"));
	  if (!"null".equals(sess) )
	    Tool.request( user.linkaction(key,val,sess),response);
	  else {
		  map1.put("result",-2); 
		  jsonString = JSON.toJSONString(map1);
		  Tool.request( jsonString,response);
	}
  }
  @RequestMapping({"getRedisVal"})
  public void getRedisVal(String key,int type,HttpServletResponse response)
  {
	  
	    Tool.request( user.getRedisVal(key,type),response);
  }
    @RequestMapping({"sendUpdateCMD"})
    public void sendUpdateCMD(String key,HttpServletResponse response)
    {

        Tool.request( user.sendUpdateCMD(key),response);
    }
 /* @RequestMapping({"login"})
  public void login(String uname, String pwd,int userId,HttpServletResponse response)
  {
	  
	    Tool.request( user.login(uname,pwd,userId),response);
  }*/
  @RequestMapping({"getMainRealGl"})
  public void getMainRealGl(String crew, int year,HttpServletResponse response)
  {

	    Tool.request( user.getMainRealGl(crew,year),response);
  }
  @RequestMapping({"sound"})
  public void sound(HttpServletResponse response) throws IOException
  {
	   Tool.request(user.sound(),response);
  }
  @RequestMapping({"loadUpdown"})
    public void loadUpdown(int rtuno,int devno,int pno, int userId,HttpServletResponse response) throws ParseException
  {
      //System.out.println("dddsss");//();
      Tool.request(this.user.loadUpdown(rtuno,devno,pno,userId), response);
  }
    @RequestMapping({"loadEvt"})
    public void loadEvt(String gky,int xh,int ck,HttpServletResponse response) throws ParseException
    {
        //System.out.println("dddsss");//();
        Tool.request(this.user.loadEvt(gky,xh,ck), response);
    }
    @RequestMapping({"loadUpdown_dig"})
    public void loadUpdown_dig(int rtuno,int devno,int pno, int userId,HttpServletResponse response) throws ParseException
    {
        //System.out.println("dddsss");//();
        Tool.request(this.user.loadUpdown_dig(rtuno,devno,pno,userId), response);
    }
    @RequestMapping({"loadDevice"})
    public void loadDevice(String dm,String gky,HttpServletResponse response) throws ParseException
    {
        Tool.request( user.loadDevice(dm,gky),response);
    }
    @RequestMapping({"askDevice"})
    public void askDevice(String dm,HttpServletResponse response) throws ParseException
    {
        Tool.request( user.askDevice(dm),response);
    }
    @RequestMapping({"sendDevice"})
    public void sendDevice(String dm,HttpServletResponse response) throws ParseException
    {
        Tool.request( user.sendDevice(dm),response);
    }
    /**
     * 请求遥测历史数据 根据设备号
     * @param deviceno   设备号 rtuno,roomno,deviceno
     * @param len    时间长度，天
     * @param dtype  数据类型，详见ana_type表
     * @param type   1：起始时间为当日零点，2：起始时间为当前时间往前推len*24小时
     * @return       返回5分钟遥测值
     */
    @RequestMapping({"ask4devdata"})
    public void ask4devdata(String deviceno,int len,int dtype,int type,String dtstr,HttpServletResponse response) throws ParseException
    {
        Tool.request( user.ask4devdata(deviceno,len,dtype,type,dtstr),response);
    }
    /**
     * 组合查询
     * @param no   设备号 rtuno,roomno,deviceno
     * @param len    时间长度，天

     * @return       返回5分钟zuhe值
     */
    @RequestMapping({"ask4uniondata"})
    public void ask4uniondata(String no,int len,String dtstr,HttpServletResponse response) throws ParseException
    {
        Tool.request( user.ask4uniondata(no,len,dtstr),response);
    }
    @RequestMapping({"ask4devavgdata"})
    public void ask4devavgdata(String deviceno,int len,int dtype,int type,HttpServletResponse response) throws ParseException
    {
        Tool.request( user.ask4devavgdata(deviceno,len,dtype,type),response);
    }
    /**
     * 请求遥测历史数据
     * @param keys   格式为：un_0_.ai_0,un_0_.ai_1,un_0_.ai_2
     * @param len    时间长度，天
     * @param type   1：起始时间为当日零点，2：起始时间为当前时间往前推len*24小时，弃用
     * @param dtstr  取数日期
     * @return       返回5分钟遥测值
     */
    @RequestMapping({"ask4data"})
    public void ask4data(String keys,int len,int type,String dtstr,HttpServletResponse response) throws ParseException
    {
        Tool.request( user.ask4data(keys,len,type,dtstr),response);
    }
    /**
     * 请求日电量结算数据
     * @param keys  格式为：un_0_.ai_0,un_0_.ai_1,un_0_.ai_2
     * @param len   时间长度，单位：天
     * @param type  1：包含当天，2：不包含当天
     * @return      日电量结算值
     */
    @RequestMapping({"ask4dndata"})
    public void ask4dndata(String keys,int len,int type,HttpServletResponse response) throws ParseException
    {
        Tool.request( user.ask4dndata(keys,len,type),response);
    }

    @RequestMapping({"loadDevice_dig"})
    public void loadDevice_dig(String dm,String gky,HttpServletResponse response) throws ParseException
    {
        Tool.request( user.loadDevice_dig(dm,gky),response);
    }
  @RequestMapping({"getuser"})
  public void getuser(String userId,String gky,HttpServletResponse response) throws IOException
  {
	   Tool.request(user.getuser(userId,gky),response);
  }
  @RequestMapping({"stopsound"})
  public void stopsound(HttpServletResponse response) throws IOException
  {
	   Tool.request(user.stopsound(),response);
  }
  @RequestMapping({"loadUser"})
  public void loadUser(HttpServletResponse response) throws IOException, ParseException
  {
	   Tool.request(user.loadUser(),response);
  }
  @RequestMapping({"logred"})
  public void logred(String uname,HttpServletResponse response) throws IOException, ParseException
  {
	   Tool.request(user.logred(uname),response);
  }
  @RequestMapping({"getTfList"})
  public void getTfList(HttpServletResponse response) throws ParseException
  {
	  Tool.request( user.getTfList(),response);
  }
  @RequestMapping({"getstatus"})
  public void getstatus(HttpServletResponse response) throws ParseException
  {
	  Tool.request( user.getstatus(),response);
  }
  @RequestMapping({"mysqltest"})
  public void mysqltest(String pno,HttpServletResponse response) throws ParseException
  {
	  Tool.request( user.mysqltest(pno),response);
  }
    @RequestMapping({"getrooms"})
    public void getrooms(String pno,HttpServletResponse response) throws ParseException
    {
        Tool.request( user.getrooms(pno),response);
    }
    @RequestMapping({"resettime"})
    public void resettime(String key,int tp,HttpServletResponse response) throws ParseException
    {
        Tool.request( user.resettime(key,tp),response);
    }
  @RequestMapping({"get_vals"})
  public void get_vals(String dt,String nm,int dno,String pno,int userId,HttpServletResponse response) throws ParseException, WriteException, IOException, ScriptException {
	  Tool.request( user.get_vals(dt,nm,dno,pno,userId),response);
  }
  @RequestMapping({"savedsplan"})
  public void savedsplan(int all,String id,String oof,String op,String cl,HttpServletResponse response) throws ParseException, SQLException
  {
	  Tool.request( user.savedsplan(all,id,oof,op,cl),response);
  }
  @RequestMapping({"deldsplann"})
  public void deldsplann(String id,String op,String wk,HttpServletResponse response) throws ParseException, SQLException
  {
	  Tool.request( user.deldsplann(id,op,wk),response);
  }
  @RequestMapping({"savedsplann"})
  public void savedsplann(int all,String id,String oof,String op,String cl,String wk,HttpServletResponse response) throws ParseException, SQLException
  {
	  Tool.request( user.savedsplann(all,id,oof,op,cl,wk),response);
  }
  @RequestMapping({"deldsplann_temp"})
  public void deldsplann_temp(String id,String op,String st,String wk,HttpServletResponse response) throws ParseException, SQLException
  {
	  Tool.request( user.deldsplann_temp(id,op,st,wk),response);
  }
  @RequestMapping({"savedsplann_temp"})
  public void savedsplann_temp(int all,String id,String oof,String op,String cl,String wk,HttpServletResponse response) throws ParseException, SQLException
  {
	  Tool.request( user.savedsplann_temp(all,id,oof,op,cl,wk),response);
  }
  @RequestMapping({"loadconfig"})
  public void loadconfig(String dno,String nm,int lev,HttpServletResponse response) throws ParseException, IOException
  {
	  Tool.request( user.loadconfig(dno,nm,lev),response);
  }
  @RequestMapping({"loadrtuno"})
  public void loadrtuno(String pno,HttpServletResponse response) throws ParseException
  {
	  Tool.request( user.loadrtuno(pno),response);
  }
  @RequestMapping({"loaddsdevntemp"})
  public void loaddsdev_temp(HttpServletResponse response) throws ParseException
  {
	  Tool.request( user.loaddsdev_temp(),response);
  }
  @RequestMapping({"loaddsdev"})
  public void loaddsdev(HttpServletResponse response) throws ParseException
  {
	  Tool.request( user.loaddsdev(),response);
  }
  @RequestMapping({"loaddsdevn"})
  public void loaddsdevn(HttpServletResponse response) throws ParseException
  {
	  Tool.request( user.loaddsdevn(),response);
  }
  
  @RequestMapping({"loadconfig_xb"})
  public void loadconfig_xb(String dno,String nm,int lev,HttpServletResponse response) throws ParseException
  {
	  Tool.request( user.loadconfig_xb(dno,nm,lev),response);
  }
  @RequestMapping({"loaddsplan"})
  public void loaddsplan(int lev,HttpServletResponse response) throws ParseException
  {
	  Tool.request( user.loaddsplan(lev),response);
  }

  @RequestMapping({"getsession"})
  public void getsession(String snm,int userId,HttpServletResponse response) throws ParseException
  {
	  Tool.request( user.getsession(snm, userId),response);
  }
    @RequestMapping({"getsessions"})
    public void getsessions(HttpServletResponse response,HttpServletRequest request) throws ParseException
    {
        java.util.Map<String,Object> map1 = new HashMap<String,Object>();
        String jsonString="";
        String sess=null;
        sess =  String.valueOf(request.getSession().getAttribute("LOGIN_SUCCESS"));
        if (!"null".equals(sess) ) {
            map1.put("result",1);
            map1.put("uname",String.valueOf(request.getSession().getAttribute("username")));
            map1.put("gkey",String.valueOf(request.getSession().getAttribute("groupKey")));
            map1.put("leval",String.valueOf(request.getSession().getAttribute("uleval")));
            map1.put("phone",String.valueOf(request.getSession().getAttribute("phone")));
            map1.put("email",String.valueOf(request.getSession().getAttribute("email")));
            map1.put("domain",String.valueOf(request.getSession().getAttribute("domain")));
            map1.put("auth",String.valueOf(request.getSession().getAttribute("auth")));
            map1.put("uid",sess);
        }
        else {
            map1.put("result",-1);

        }
        jsonString = JSON.toJSONString(map1);
        Tool.request( jsonString,response);
    }
  @RequestMapping({"loadconfig_dl"})
  public void loadconfig_dl(String dno,String nm,int lev,HttpServletResponse response) throws ParseException
  {
	  Tool.request( user.loadconfig_dl(dno,nm,lev),response);
  }
  @RequestMapping({"loadconfig_dn"})
  public void loadconfig_dn(String dno,String nm,int lev,HttpServletResponse response) throws ParseException, IOException
  {
	  Tool.request( user.loadconfig_dn(dno,nm,lev),response);
  }
    @RequestMapping({"resetpasswd"})
    public void resetpasswd(String op,String np,String us,HttpServletResponse response) throws ParseException, IOException
    {
        Tool.request( user.resetpasswd(op,np,us),response);
    }
    @RequestMapping({"resetphone"})
    public void resetphone(String op,String np,String us,HttpServletResponse response) throws ParseException, IOException
    {
        Tool.request( user.resetphone(op,np,us),response);
    }
  @RequestMapping({"gettree"})
  public void gettree(String pno,HttpServletResponse response) throws ParseException
  {
	  Tool.request( user.gettree(pno),response);
  }
  @RequestMapping({"getyTfList"})
  public void getyTfList(HttpServletResponse response) throws ParseException
  {
	  Tool.request( user.getyTfList(),response);
  }
  @RequestMapping({"getTfPlanAll"})
  public void getTfPlanAll(String crew,String tab,int pId,int userId , HttpServletResponse response) throws ParseException
  {

	    Tool.request( user.getTfPlanAll(crew,tab,pId,userId),response);
  }
  @RequestMapping({"getTfPlanAll_kb"})
  public void getTfPlanAll_kb(String crew,String tab,int pId , HttpServletResponse response) throws ParseException
  {

	    Tool.request( user.getTfPlanAll_kb(crew,tab,pId),response);
  }
  @RequestMapping({"getTfPlanAll_evt"})
  public void getTfPlanAll_evt(String crew,String tab,int pId,int ob ,String pno, int userId,HttpServletResponse response) throws ParseException
  {
      //System.out.println(pId);
	    Tool.request( user.getTfPlanAll_evt(crew,tab,pId,ob,pno,userId),response);
  }
  @RequestMapping({"getTfPlanAll_xb"})
  public void getTfPlanAll_xb(String crew,String tab,String pId ,int nmd, HttpServletResponse response) throws ParseException
  {

	    Tool.request( user.getTfPlanAll_xb(crew,tab,pId,nmd),response);
  }
  @RequestMapping({"getTfPlanAll_dl"})
  public void getTfPlanAll_dl(String crew,String tab,String pId ,int nmd, HttpServletResponse response) throws ParseException
  {

	    Tool.request( user.getTfPlanAll_dl(crew,tab,pId,nmd),response);
  }
  @RequestMapping({"getyTfPlanAll"})
  public void getyTfPlanAll(int sn,String jz,int tab , HttpServletResponse response) throws ParseException
  {

	    Tool.request( user.getyTfPlanAll(sn,jz,tab),response);
  }
  @RequestMapping({"showgd"})
  public void showgd(int aid,int pid , HttpServletResponse response) throws ParseException
  {

	    Tool.request( user.showgd(aid,pid),response);
  }
  @RequestMapping({"getyTfPlanAll11"})
  public void getyTfPlanAll11(int sn,String jz,int tab , HttpServletResponse response) throws ParseException
  {

	    Tool.request( user.getyTfPlanAll11(sn,jz,tab),response);
  }
  @RequestMapping({"getplandl"})
  public void getplandl(int sn,String jz,int tab , HttpServletResponse response) throws ParseException
  {

	    Tool.request( user.getplandl(sn,jz,tab),response);
  }
  @RequestMapping({"getyPlanAll"})
  public void getyPlanAll(int sn,String jz,int tab , HttpServletResponse response) throws ParseException
  {

	    Tool.request( user.getyPlanAll(sn,jz,tab),response);
  }
 /* @RequestMapping({"getTfPlanAllByTime"})
  public void getTfPlanAllByTime(String planTime,String jz,int tab , HttpServletResponse response) throws ParseException
  {

	    Tool.request( user.getTfPlanAllByTime(planTime,jz,tab),response);
  }*/
  
  @RequestMapping({"newTfPlan"})
  public void newTfPlan(int year, String pName,HttpServletResponse response) throws ParseException, SQLException
  {

	    Tool.request( user.newTfPlan(year, pName),response);
  }
  @RequestMapping({"updateTfPlan"})
  public void updateTfPlan(int dsn, String p_exp,HttpServletResponse response) throws ParseException, SQLException
  {

	    Tool.request( user.updateTfPlan(dsn, p_exp),response);
  }
  @RequestMapping({"getdlData"})
  public void getdlData(String crew,String crewLong, int pId,HttpServletResponse response) throws ParseException
  {
	    Tool.request( user.getdlData(crew, crewLong,pId),response);
  }
  @RequestMapping({"getTfData"})
  public void getTfData(String crew,String crewLong, int pId,int userId,HttpServletResponse response) throws ParseException
  {
	    Tool.request( user.getTfData(crew, crewLong,pId,userId),response);
  }
  @RequestMapping({"getTfData_gzr"})
  public void getTfData_gzr(String crew,String crewLong, int pId,HttpServletResponse response) throws ParseException
  {
	    Tool.request( user.getTfData_gzr(crew, crewLong,pId),response);
  }
  @RequestMapping({"getTfData_xb"})
  public void getTfData_xb(String crew,String crewLong, String pId,HttpServletResponse response) throws ParseException
  {
	    Tool.request( user.getTfData_xb(crew, crewLong,pId),response);
  }
  @RequestMapping({"getTfData_dl"})
  public void getTfData_dl(String crew,String crewLong, String pId,HttpServletResponse response) throws ParseException
  {
	    Tool.request( user.getTfData_dl(crew, crewLong,pId),response);
  }
  @RequestMapping({"chgPwd"})
  public void chgPwd(String uname,String opwd,String npwd,HttpServletResponse response) throws ParseException
  {
	    Tool.request( user.chgPwd(uname,opwd, npwd),response);
  }
    @RequestMapping({"setEvtStat"})
    public void setEvtStat(String info,HttpServletResponse response) throws ParseException
    {
        Tool.request( user.setEvtStat(info),response);
    }
  /*@RequestMapping({"getyTfData"})
  public void getyTfData(String crew,String crewLong, int pId,HttpServletResponse response) throws ParseException
  {
	    Tool.request( user.getyTfData(crew, crewLong,pId),response);
  }*/
  @RequestMapping({"saveTfEdit"})
  public void saveTfEdit(String jsonStr,int sn , HttpServletResponse response) throws ParseException
  {
	   try
		{
		   Tool.request( user.saveTfEdit(jsonStr,sn),response);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
  }
  @RequestMapping({"saveUser"})
  public void saveUser(String jsonStr,int sn , HttpServletResponse response) throws ParseException
  {
	   try
		{
		   Tool.request( user.saveUser(jsonStr,sn),response);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
  }
  @RequestMapping({"getUserAuthor"})
  public void getUserAuthor(int uId, HttpServletResponse response) throws ParseException 
  { 
	  Tool.request(this.user.getUserAuthor(uId), response); 
	  }
  @RequestMapping({"getauthor"})
  public void getauthor(HttpServletResponse response) throws ParseException { Tool.request(this.user.getauthor(), response); }
  @RequestMapping({"set_author"})
  public void set_author(String devs, int uid, HttpServletResponse response) { Tool.request(this.user.set_author(devs, uid), response); }
  @RequestMapping({"loadconfig_updown"})
  public void loadconfig_updown(String dno, String nm, int lev, HttpServletResponse response) throws ParseException { Tool.request(this.user.loadconfig_updown(dno, nm, lev), response); }
  @RequestMapping({"testHelper"})
  public void testHelper(int pId, HttpServletResponse response) throws ParseException { Tool.request(this.user.testHelper(pId), response); }
  @RequestMapping({"loadconfig_updown_cn"})
  public void loadconfig_updown_cn(String dno, String nm, int lev, HttpServletResponse response) throws ParseException { Tool.request(this.user.loadconfig_updown_cn(dno, nm, lev), response); }

  
  @RequestMapping({"saveupdown"})
  public void saveupdown(String jsonStr, int sn, HttpServletResponse response) throws ParseException {
    try {
      Tool.request(this.user.saveupdown(jsonStr, Integer.valueOf(sn)), response);
    }
    catch (SQLException e) {
      
      e.printStackTrace();
    } 
  }

  
  @RequestMapping({"syncupdown"})
  public void syncupdown(HttpServletResponse response) throws ParseException, SQLException { Tool.request(this.user.syncupdown(), response); }

  
  @RequestMapping({"saveyTfEdit"})
  public void saveyTfEdit(String jsonStr,int sn , String gky,HttpServletResponse response) throws ParseException
  {
	   try
		{
		   Tool.request( user.saveyTfEdit(jsonStr,sn,gky),response);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
  }
    @RequestMapping({"saveUpDown"})
    public void saveUpDown(String jsonStr,int sn , HttpServletResponse response) throws ParseException
    {
        try
        {
            Tool.request( user.saveUpDown(jsonStr,sn),response);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    @RequestMapping({"saveUpDown_dig"})
    public void saveUpDown_dig(String jsonStr,String sn , HttpServletResponse response) throws ParseException
    {
        try
        {
            Tool.request( user.saveUpDown_dig(jsonStr,sn),response);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
  @RequestMapping({"savegdEdit"})
  public void savegdEdit(String jsonStr,int mark ,int evt, HttpServletResponse response) throws ParseException
  {
	   try
		{
		   
		   Tool.request( user.savegdEdit(jsonStr,mark,evt),response);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
  }
  @RequestMapping({"tfExport"})
  public void tfExport(int sn,int tab,int mode,String key, HttpServletResponse response)
  {
	  String from="";
	  String to="";
	  if(mode==2)
	  {
		  String[] keyArr=key.split("\\|");
		  from=keyArr[0];
		  to=keyArr[1];
	  }
	  Tool.request( user.tfExport(sn,tab,mode,from,to),response);
  }

  /*@RequestMapping({"doinfo"})
  public void doinfo(String key, int mode, String crew , String crewLong , HttpServletResponse response) throws ParseException 
  {
	  
	  if(mode==1)
	  { 
		  Tool.request(   user.doinfo(key,mode),response);
	  }
	  else if (mode==2)
	  {
		  Tool.request(   user.doinfo(key,mode),response);
	  }
	  else
	  {
		  String[] keyArr=key.split("\\|");
		  String from=keyArr[0];
		  String to=keyArr[1];
		  Tool.request(   user.doTfTimeSearch(from,to,crew,crewLong),response);
	  }
	  
  }*/
  @RequestMapping({"getmsg"})
  //aid: aids, pid: pids, crew: nowJz, crewLong: nowJzLong 
  public void getmsg(int st, int len, int domain , HttpServletResponse response)
  {
	  

		 // Tool.request(   user.delgd(aid,pid),response);
	 
	  
  }
  /*@RequestMapping({"doTfSearch"})
  public void doTfSearch(String key, int mode, String crew , String crewLong , HttpServletResponse response) throws ParseException 
  {
	  if(mode==1)
	  { 
		  Tool.request(   user.doTfSearch(key),response);
	  }
	  else
	  {
		  String[] keyArr=key.split("\\|");
		  String from=keyArr[0];
		  String to=keyArr[1];
		  Tool.request(   user.doTfTimeSearch(from,to,crew,crewLong),response);
	  }
  }*/

 /* @RequestMapping({"tfdcs"})
  public void tfdcs(String crew, String crewLong , int pId, int tab,int mode,String key,HttpServletResponse response) throws ParseException
  {

	  int num=1;
	  ArrayList jzArr=new ArrayList();  
	  ArrayList jzLongArr=new ArrayList();  
	  ArrayList dataArr=new ArrayList();  
	  

	  if(crew.endsWith("all"))
	  {
		  num=10;
		  jzArr.add("all");
		  jzArr.add("Q1-1");
		  jzArr.add("Q2-1");
		  jzArr.add("Q2-2"); 
		  jzArr.add("Q2-3"); 
		  jzArr.add("Q2-4"); 
		  jzArr.add("Q3-1");
		  jzArr.add("Q3-2");
		  jzArr.add("F1-1");
		  jzArr.add("F1-2"); 

		  jzLongArr.add("all");
		  jzLongArr.add("QM_GYD010WZ");
		  jzLongArr.add("1RCP009VE");
		  jzLongArr.add("2RCP009VE"); 
		  jzLongArr.add("3RCP010VE"); 
		  jzLongArr.add("4RCP010VE");
		  jzLongArr.add("U1_AI2565");
		  jzLongArr.add("U2_AI2565");
		  jzLongArr.add("1GRE012MY_AVALUE_RT");
		  jzLongArr.add("2GRE012MY_AVALUE_RT"); 
	  }
	  else if(crew.endsWith("p_1"))
	  {
		  num=2;
		  jzArr.add("p_1");
		  jzArr.add("Q1-1");
		  jzLongArr.add("p_1");
		  jzLongArr.add("QM_GYD010WZ");
	  }
	  else if(crew.endsWith("p_2"))
	  {
		  num=5;
		  jzArr.add("p_2");
		  jzArr.add("Q2-1");
		  jzArr.add("Q2-2"); 
		  jzArr.add("Q2-3"); 
		  jzArr.add("Q2-4"); 
		  jzLongArr.add("p_2");
		  jzLongArr.add("1RCP009VE");
		  jzLongArr.add("2RCP009VE"); 
		  jzLongArr.add("3RCP010VE"); 
		  jzLongArr.add("4RCP010VE");
	  }
	  else if(crew.endsWith("p_3"))
	  {
		  num=3;
		  jzArr.add("p_3");
		  jzArr.add("Q3-1");
		  jzArr.add("Q3-2");
		  jzLongArr.add("p_3");
		  jzLongArr.add("U1_AI2565");
		  jzLongArr.add("U2_AI2565");
	  }
	  else if(crew.endsWith("p_4"))
	  {
		  num=3;
		  jzArr.add("p_4");
		  jzArr.add("F1-1");
		  jzArr.add("F1-2"); 
		  jzLongArr.add("p_4");
		  jzLongArr.add("1GRE012MY_AVALUE_RT");
		  jzLongArr.add("2GRE012MY_AVALUE_RT"); 
	  }
	  else
	  {
		  jzArr.add(crew); 
		  jzLongArr.add(crewLong);
	  }
	  java.util.Map<String,Object> map = new HashMap<String,Object>();  
	  
	  if(mode==1)
	  {
			for (int i= 0; i<num; i++)
			{
				dataArr.add((Map<String,String>)JSON.parse(user.getTfData(jzArr.get(i).toString(), jzLongArr.get(i).toString(),pId)));
			}
			if(num==1)
			{
				dataArr.add(null);
			}
			map.put("data",dataArr);  
	  }
	  else
	  {
		  String[] keyArr=key.split("\\|");
		  String from=keyArr[0];
		  String to=keyArr[1];
			for (int i= 0; i<num; i++)
			{
				dataArr.add((Map<String,String>)JSON.parse(user.doTfTimeSearch(from,to,jzArr.get(i).toString(), jzLongArr.get(i).toString())));
			}
			if(num==1)
			{
				dataArr.add(null);
			}
			map.put("data",dataArr);  
	  }
		map.put("result",1);  
	 String jsonString = JSON.toJSONString(map);
	  Tool.request( jsonString,response);
  }*/


  public UserDAOImpl getUserDao()
  {
    return this.user;
  }
  
  public void setUserDao(UserDAOImpl userDao)
  {
    this.user = userDao;
  }
}
