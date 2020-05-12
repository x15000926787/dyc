package com.dl.controller;

import com.dl.impl.treeDAOImpl;
import com.dl.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

@Controller
public class treeController
{
  @Autowired
  private treeDAOImpl user;

  @RequestMapping("tree")
  public void get_tree(String pno,HttpServletResponse response)
  {
	   Tool.request(user.get_tree(pno),response);
  }
  @RequestMapping({"tree_2"})
  public void get_tree_2(int dd,String kk,String rtu,HttpServletResponse response)
  {
	   Tool.request(user.get_tree_2(dd,kk,rtu),response);
  }
  @RequestMapping({"tree_3"})
  public void get_tree_3(String kk,String rtu,String pno,HttpServletResponse response)
  {
	   Tool.request(user.get_tree_3(kk,rtu,pno),response);
  }
  @RequestMapping({"tree_4"})
  public void get_tree_4(String kk,String rtu,String nm,HttpServletResponse response)
  {
	   Tool.request(user.get_tree_4(kk,rtu,nm),response);
  }
  public treeDAOImpl getUserDao()
  {
    return this.user;
  }
  
  public void setUserDao(treeDAOImpl userDao)
  {
    this.user = userDao;
  }
}

