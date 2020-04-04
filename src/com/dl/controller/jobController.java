package com.dl.controller;

import com.dl.impl.jobDAOImpl;
import com.dl.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

@Controller
public class jobController
{
  @Autowired
  private jobDAOImpl user;

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
}

