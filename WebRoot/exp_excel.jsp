<%@page language="java" contentType="application/x-msdownload" pageEncoding="UTF-8"%>  
<%@page import="java.net.URLEncoder" %>
<%@ page import="java.io.*"%>
<%  
  //关于文件下载时采用文件流输出的方式处理：  
  //加上response.reset()，并且所有的％>后面不要换行，包括最后一个；  
  request.setCharacterEncoding("UTF-8");
  response.reset();//可以加也可以不加  
  response.setContentType("application/x-download");  

String ch="0";
String fm="";
String dt="2017-11-01";
String rptdir = application.getRealPath("exp_excel.jsp");
rptdir="C:\\exp";

out.print(rptdir);

 
try{
  ch=(request.getParameter("lb")); 
}
catch(Exception e){ch="0";}
try{
  fm=new String(request.getParameter("fm").getBytes("iso-8859-1"), "UTF-8"); ; 
}
catch(Exception e){fm="";}
//fm="汽车结构性能检测研究实验室能耗月报表";
try{
  dt=(request.getParameter("dt")); 
}
catch(Exception e){dt="2017-11-01";}
dt=dt.replace("-", "");
dt=dt.replace(" ", "");
dt=dt.replace(":", "");
//application.getRealPath("/main/mvplayer/CapSetup.msi");获取的物理路径  
String[] tems = dt.split("-");  
String filedownload = rptdir+"\\exp_"+ch+"_"+dt+".xlsx";  

File f = new File(filedownload);
 
  
   String filedisplay ="exp_"+ch+"_"+dt+".xlsx";  
   filedisplay = URLEncoder.encode(filedisplay,"UTF-8");  
   
  response.addHeader("Content-Disposition","attachment;filename=" + filedisplay);  
  
  java.io.OutputStream outp = null;  
  java.io.FileInputStream in = null;  
   out.print(filedownload);
 if(f.exists())
  {
  
  try  
  {  
  outp = response.getOutputStream();  
  in = new FileInputStream(filedownload);  
  
  byte[] b = new byte[1024];  
  int i = 0;  
  
  while((i = in.read(b)) > 0)  
  {  
  outp.write(b, 0, i);  
  }  
//    
outp.flush();  
//要加以下两句话，否则会报错  
//java.lang.IllegalStateException: getOutputStream() has already been called for //this response    
out.clear();  
out = pageContext.pushBody();  
}  
  catch(Exception e)  
  {  
  System.out.println("Error!");  
  e.printStackTrace();  
  }  
  finally  
  {  
  if(in != null)  
  {  
  in.close();  
  in = null;  
  }  

  }  
  } else
  {
     out.print(filedownload+"文件不存在！");
  }
%> 