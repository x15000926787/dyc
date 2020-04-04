package com.dl.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.dl.impl.emailDAOImpl;

import com.dl.tool.Tool;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class emailController
{
  @Autowired
  private emailDAOImpl user;

  @RequestMapping({"getemailuser"})
  public void getuser(HttpServletResponse response) throws IOException
  {
	   Tool.request(user.getemailuser(),response);
  }
  @RequestMapping({"saveeuserEdit"})
  public void saveeuserEdit(String jsonStr,int sn , HttpServletResponse response) throws ParseException
  {
	   try
		{
		   Tool.request( user.saveeuserEdit(jsonStr,sn),response);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
  }
}

