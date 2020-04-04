package com.dl.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dl.impl.updownDAOImpl;
import com.dl.impl.treeDAOImpl;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class updownController
{
  @Autowired
  private updownDAOImpl user;

  @RequestMapping({"loadconfig_updown_emc"})
  public void loadconfig_updown(String dno, String nm, int lev, HttpServletResponse response) throws ParseException
  { 
	  Tool.request(this.user.loadconfig_updown(dno, nm, lev), response); 
	  }
  @RequestMapping({"testHelper_emc"})
  public void testHelper(int pId, HttpServletResponse response) throws ParseException { Tool.request(this.user.testHelper(pId), response); }


  @RequestMapping({"loadconfig_updown_cn_emc"})
  public void loadconfig_updown_cn(String dno, String nm, int lev, HttpServletResponse response) throws ParseException { Tool.request(this.user.loadconfig_updown_cn(dno, nm, lev), response); }

  
  @RequestMapping({"saveupdown_emc"})
  public void saveupdown(String jsonStr, int sn, HttpServletResponse response) throws ParseException {
    try {
      Tool.request(this.user.saveupdown(jsonStr, Integer.valueOf(sn)), response);
    }
    catch (SQLException e) {
      
      e.printStackTrace();
    } 
  }

  
  @RequestMapping({"syncupdown_emc"})
  public void syncupdown(HttpServletResponse response) throws ParseException, SQLException { Tool.request(this.user.syncupdown(), response); }

  public updownDAOImpl getUserDao()
  {
    return this.user;
  }
  
  public void setUserDao(updownDAOImpl userDao)
  {
    this.user = userDao;
  }
}

