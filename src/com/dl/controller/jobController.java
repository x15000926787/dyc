package com.dl.controller;

import com.alibaba.fastjson.JSONObject;
import com.dl.impl.jobDAOImpl;
import com.dl.quartz.QuartzManager;
import com.dl.tool.ReportDycJob;
import com.dl.tool.Tool;
import com.dl.tool.saveMaxMin;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

@Controller
public class jobController
{
  @Autowired
  private jobDAOImpl user;

  private QuartzManager qjob=new QuartzManager();

  @RequestMapping({"histycjob"})
  public void restarthistyc(HttpServletResponse response) throws IOException
  {
	   Tool.request(user.restarthistyc(),response);
  }
  @RequestMapping({"reloadana"})
  public void reloadana(HttpServletResponse response) throws IOException
  {
    Tool.request(user.reloadana(),response);
  }
  /*@RequestMapping({"saveeuserEdit"})
  public void saveeuserEdit(String jsonStr,int sn , HttpServletResponse response) throws ParseException
  {
	   try
		{
		   Tool.request( user.saveeuserEdit(jsonStr,sn),response);
		}
		catch (SQLException e)
		{
			e.printStackTrace(); getQuartzDetail
		}
  }*/
  @RequestMapping({"getQuartzDetail"})
  public void getQuartzDetail(String sn,String nm,HttpServletResponse response) throws IOException
  {
    Tool.request(user.getQuartzDetail(sn,nm),response);
  }
  @RequestMapping({"getUnionDetail"})
  public void getUnionDetail(String sn,int tp,HttpServletResponse response) throws IOException
  {
    Tool.request(user.getUnionDetail(sn,tp),response);
  }
  @RequestMapping({"saveMaxMin"})
  public void saveMaxMin(HttpServletResponse response) throws IOException
  {
    try {
      qjob.addJob("savemaxmin", "gsavemaxmin", "tsavemaxmin", "tgsavemaxmin",
              saveMaxMin.class,  new JSONObject(), 5,5 ,0);

    }catch (Exception e)
    {

    }
  }
  @RequestMapping({"dycbb"})
  public void dycbb(String tdatestr,HttpServletResponse response) throws IOException
  {
    HashMap<String,String> tdate =new HashMap<>();

    Gson gson = new Gson();
    //Map<String, Object> map = new HashMap<String, Object>();
    tdate = gson.fromJson("{\"dtstr\":"+tdatestr+"}", tdate.getClass());
    Tool.request(adddycbbjob(tdate),response);
  }
  public  String adddycbbjob(HashMap tdate)
  {
    String jsonString="{\"result\":0}";
    try {
      qjob.addJob("dycbb", "gdycbb", "tdycbb", "tgdycbb",
              ReportDycJob.class,  tdate, 5,5 ,0);
      jsonString="{\"result\":1}";
    }catch (Exception e)
    {

    }
    return jsonString;
  }
  @RequestMapping({"savejobEdit"})
  public void savejobEdit(String jsonStr,int sn , HttpServletResponse response) throws ParseException
  {
    try
    {
      Tool.request( user.savejobEdit(jsonStr,sn),response);
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }
  @RequestMapping({"saveUnionEdit"})
  public void saveUnionEdit(String jsonStr,int sn , HttpServletResponse response) throws ParseException
  {
    try
    {
      Tool.request( user.saveUnionEdit(jsonStr,sn),response);
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }
  @RequestMapping({"saveDetailEdit"})
  public void saveDetailEdit(String jsonStr,int sn , HttpServletResponse response) throws ParseException
  {
    try
    {
      Tool.request( user.saveDetailEdit(jsonStr,sn),response);
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }
  @RequestMapping({"saveUnionDetailEdit"})
  public void saveUnionDetailEdit(String jsonStr,int sn , HttpServletResponse response) throws ParseException
  {
    try
    {
      Tool.request( user.saveUnionDetailEdit(jsonStr,sn),response);
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }
  @RequestMapping({"newDetail"})
  public void newDetail(String task,String kkey,String vv , HttpServletResponse response) throws ParseException
  {
    try
    {
      Tool.request( user.newDetail(task,kkey,vv),response);
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }
  @RequestMapping({"getfullname"})
  public void getfullname(HttpServletResponse response) throws IOException
  {
    Tool.request(user.getfullname(),response);
  }
  @RequestMapping({"getetype"})
  public void getetype(HttpServletResponse response) throws IOException
  {
    Tool.request(user.getetype(),response);
  }
  @RequestMapping({"getquartz"})
  public void getquartz(HttpServletResponse response) throws IOException
  {
	   Tool.request(user.getquartz(),response);
  }
  @RequestMapping({"getunion"})
  public void getunion(HttpServletResponse response) throws IOException
  {
    Tool.request(user.getunion(),response);
  }
  @RequestMapping({"getHisVal"})
  public void getHisVal(HttpServletResponse response) throws IOException
  {
    Tool.request(user.getHisVal(),response);
  }
  @RequestMapping({"getcronstr"})
  public void getcronstr(String pno,HttpServletResponse response) throws IOException
  {
    Tool.request(user.getcronstr(pno),response);
  }
  @RequestMapping({"getcronstr_prv"})
  public void getcronstr_prv(String pno,HttpServletResponse response) throws ParseException, InterruptedException
  {
    Tool.request(user.getcronstr_prv(pno),response);
  }
  public jobDAOImpl getUserDao()
  {
    return this.user;
  }
  
  public void setUserDao(jobDAOImpl userDao)
  {
    this.user = userDao;
  }
  public QuartzManager getQuartzManager()
  {
    return this.qjob;
  }

  public void setQuartzManager(QuartzManager qjob)
  {
    this.qjob = qjob;
  }
}

