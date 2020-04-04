package com.dl.controller;

import java.sql.SQLException;
import java.text.ParseException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dl.impl.UserDAOImpl;
import com.dl.tool.Tool;

@Controller
public class DlController
{
	  @Autowired
	  private UserDAOImpl user;
	  
	  @RequestMapping({"getdltj"})
	  public void getdltj(String startNum,String endNum, String startT,String endT , HttpServletResponse response) throws ParseException
	  {
		  Tool.request( user.getdltj(startNum,endNum,startT,endT),response);
	  }
	  
	  
	  @RequestMapping({"getdlssShowCell"})
	  public void getdlssShowCell(String startT ,int resion, int type,int inOut, HttpServletResponse response)
	  {
		  Tool.request( user.getdlssShowCell(startT,resion,type,inOut),response);
	  }
	  
	  @RequestMapping({"getdlssPointEditCell"})
	  public void getdlssPointEditCell(String startT , HttpServletResponse response)
	  {
		    Tool.request( user.getdlssPointEditCell(startT),response);
	  }
	  
	  @RequestMapping({"getdlssCaseEditCell"})
	  public void getdlssCaseEditCell(int resion, int type,int inOut , HttpServletResponse response)
	  {
		  Tool.request( user.getdlssCaseEditCell(resion,type,inOut),response);
	  }
	  
	  
	  @RequestMapping({"savedlssPointEdit"})
	  public void savedlssPointEdit(String jsonStr, HttpServletResponse response) throws ParseException
	  {
		   try
			{
			   Tool.request( user.savedlssPointEdit(jsonStr),response);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
	  }
	  
	  
	  @RequestMapping({"savedlssCaseEdit"})
	  public void savedlssCaseEdit(String jsonStr, HttpServletResponse response) throws ParseException
	  {
		   try
			{
			   Tool.request( user.savedlssCaseEdit(jsonStr),response);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
	  }

	  
	  @RequestMapping({"dlExport"})
	  public void export(HttpServletResponse response)
	  {
	  }
	  
}
