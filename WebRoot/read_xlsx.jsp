<%@ page contentType="text/html;charset=utf-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*"%>
<%@ page import="com.dl.tool.*" %>
<html>
<head>
    <title>预览excel</title>
</head>
<body>

<%
    String ch="0";
    String fm="";
    String rptdir = "bb";
    fm = request.getParameter("fm");
    try{
        ch=(request.getParameter("lb"));
    }
    catch(Exception e){ch="0";}
    String dt="2017-11-01";
   // System.out.println(fm);
    CharTools charTools = new CharTools();
    String url="";
    if(charTools.isUtf8Url(fm)){
        url=charTools.Utf8URLdecode(fm);
       // System.out.println(charTools.Utf8URLdecode(fm));
    }else{
       // System.out.println("dd");
    }

    try{
        dt=(request.getParameter("dt"));
    }
    catch(Exception e){dt="2017-11-01";}

    String[] tems = dt.split("-");
    dt=dt.replace("-","");



    String filedownload = rptdir+"/"+Integer.parseInt(tems[0])+"/"+Integer.parseInt(tems[1]);//+"\\"+url+".pdf";
    if (ch.matches("5")) filedownload = filedownload+"/"+Integer.parseInt(tems[2]);
    filedownload = filedownload+"/"+fm+".pdf";
    //out.print(filedownload);

%>
<script>
    function displayDate()
    {
        //alert('dddd');
        window.close();
    }
</script>
<button type="button" onclick="displayDate()" style="">
    关闭</button>

<embed src="<%out.print(filedownload);%>#view=FitH,top" type="application/pdf" style="overflow: auto; position: absolute; top: 30px; right: 0; bottom: 0; left: 0; width: 100%; height: 95%;">
</body>
</html>
