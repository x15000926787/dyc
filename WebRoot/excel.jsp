<%@ page language="java" contentType="application/x-msdownload" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*"%>
<%@ page import="com.dl.tool.*" %>



    <% 
     response.reset();
  response.setContentType("application/x-download");


           
String ch="0";
String fm="";
String bid="";
String dt="2017-11-01";
//String rptdir = "/Volumes/APP/OneDrive/dyc/out/artifacts/dyc_war_exploded/bb";
        String rptdir = "/opt/java/tomcat8/webapps/dyc/bb";
try{
  ch=(request.getParameter("lb")); 
}
catch(Exception e){ch="0";}
        try{
            bid=(request.getParameter("bid"));
        }
        catch(Exception e){bid="1";}
fm = request.getParameter("fm");

System.out.println(fm);
   CharTools charTools = new CharTools();
            String url="";
            if(charTools.isUtf8Url(fm)){
url=charTools.Utf8URLdecode(fm);
            System.out.println(charTools.Utf8URLdecode(fm));
            }else{
            System.out.println("dd");
            }



try{
  dt=(request.getParameter("dt")); 
}
catch(Exception e){dt="2017-11-01";}
String[] tems = dt.split("-"); 
dt=dt.replace("-","");

//application.getRealPath("/main/mvplayer/CapSetup.msi");

        String filedownload = rptdir+"/"+Integer.parseInt(tems[0])+"/"+Integer.parseInt(tems[1]);//+"\\"+url+".pdf";
        if (ch.matches("5")) filedownload = filedownload+"/"+Integer.parseInt(tems[2]);
System.out.println(filedownload);
File f = new File(filedownload+"/"+bid+".xlsx");
 

   String filedisplay = fm+".xlsx";
  // filedisplay = URLEncoder.encode(filedisplay,"UTF-8");  

  response.addHeader("Content-Disposition","attachment;filename=" + filedisplay);
  
  java.io.OutputStream outp = null;  
  java.io.FileInputStream in = null;  
   out.print(filedownload);
 if(f.exists())
  {
System.out.println(filedownload);
  try  
  {  
  outp = response.getOutputStream();  
  in = new FileInputStream(filedownload+"/"+bid+".xlsx");
  
  byte[] b = new byte[1024];  
  int i = 0;  
  
  while((i = in.read(b)) > 0)  
  {  
  outp.write(b, 0, i);  
  }  
//    
outp.flush();  

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

        }
    %>

