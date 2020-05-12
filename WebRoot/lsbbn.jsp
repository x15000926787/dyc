<%@ page contentType="text/html;charset=utf-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*"%>
<!DOCTYPE html>
<html lang="zh-cn">

<head>
	<style>
		body{
			color: #fff;
		}
	.pull-right	table {
   
    width: 100%;
    display: block;
}
  
.pull-right thead {
    float: left;
}
  
.pull-right tbody tr {
    display: inline-block;
    margin: 77px 0 95px 150px;
}
.pull-right th{
	line-height: 228px;
	display: block;
}
  
.pull-right tr td {
    position: relative;
    width: 200px;
   
}
 
	
	</style>
	<!-- Bootstrap 3.3.6 -->
	
    <link href="css/bootstrsp.ziji.css" rel="stylesheet">

	<script src="plugins/jQuery/jquery-2.2.3.min.js"></script>
    <script src="js/jstree.min.js"></script>
	<!-- <script src="js/chart.js"></script> -->
	<script src="plugins/datepicker/bootstrap-datepicker.js"></script>
	<script src="plugins/datepicker/locales/bootstrap-datepicker.zh-CN.js"></script>
	<script type="text/javascript" src="plugins/datepicker/moment.js"></script>

	<script type="text/javascript" src="plugins/datepicker/daterangepicker.js"></script>
	<script src="js/bootstrap.min.js"></script>
	<script src="dist/js/app.js"></script>

	<script type="text/javascript" src="js/bootstrap-datetimepicker.js" charset="UTF-8"></script>
	<!-- <script type="text/javascript" src="js/locales/bootstrap-datetimepicker.zh-CN.js" charset="UTF-8"></script> -->

	<script src="js/handsontable.full.js"></script>
    <script src="setup.js"></script>
	<script src="js/DDoc.js"></script>
	<script src="js/FileSaver.js"></script>
	<script src="js/jszip.js"></script>
     <script src="js/content.min.js?v=1.0.0"></script>
    <script src="js/plugins/layer/laydate/laydate.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>




	

<body class="skin-blue layout-top-nav" style="height: auto;background: #343c48;">
	
	<div style="width:80%;">
		<div class="nav-tabs-custom" style="margin: 60px 0 0px 150px;">
			
								  <form class="form-horizontal m-t">
                                            <label class="col-sm-2 control-label" >选择日期：</label>
                                            <input id="dayInput"  class="laydate-icon form-control layer-date" style="margin: -5px 5px 0 0;padding-left: 10px;width:195px;color: #fff">
                                            <input onclick="setmonth(this)" name="tmon" id="tmon" style="display:none"/>
								     <select id="tyear"  valign="middle"  style="display:none"></select>
                                </form>
</div>
					<div class="pull-right" style="position: absolute;width: 100%;height: 1000px;">
					 
						<!-- <table border="0" cellspacing="0" cellpadding="0" width="950">
							<tr>
								<td width="500">



								</td>
								
									<td width="1200" align="right" >
    
                             
                                 
								</td>
								
								
							</tr>
						</table>

					</div> -->


			
			



		

        <table border=0 width=90%  align=center>
        	<thead>
   <!--    <tr> <th align=center>北楼</th><th align=center>南楼</th></tr> -->
       
        </thead>
       <!--  <td  valign=top  align=center> -->
       	 <!-- <tbody> -->
		<%
Connection con=null;
Statement sql=null;
ResultSet rs=null;
String fnm;

try
{
    Class.forName("com.mysql.jdbc.Driver").newInstance();
   String uri="jdbc:mysql://192.168.3.91:3306/scada";
    con=DriverManager.getConnection(uri,"root","Shcs2017@");
   sql=con.createStatement();
}
catch(Exception e){out.print(e.toString());}


try{
   
    rs=sql.executeQuery("SELECT * FROM rptlist where valid>0");
  
        out.print("<tbody border=0; cellpadding=5 cellspacing=15 style='width:900px;margin: -455px 0 0 0;'>");
   
    while(rs.next()){
        fnm = rs.getString(2);
        //fnm=fnm.substring(0,fnm.length()-5);
        out.print("<tr height=30px>");
        out.print("<td style='border-collapse:separate;border-spacing:30px 50px;padding:10px 30px 10px 20px;border:1px solid #999; border-radius: 8px;background: #2a3039;'><U><span style='color:#fff;' href=\"#\">"+fnm+"<br><br><img src='img/tupian.svg';style='height:10px';><a ' href=\"javascript:read_excel('"+rs.getString(1)+"',"+rs.getString(4)+")\" style='position: absolute;margin: 40px 0 0 20px;color:#c5dfff;'>查看</a><a ' href=\"javascript:excel('"+fnm+"',"+rs.getString(4)+","+rs.getString(1)+")\" style='position: absolute;margin: 40px 0 0 65px;color:#c5dfff;'>导出</a></span></U></td>");
       
       
        out.print("</tr>");
    
    }
    out.print("</tbody>");
   // con.close();
}
catch(SQLException e1){out.print(e1);}
%>
<!-- </td> -->

<!-- </tbody> -->
</table>
</div>

	</div>
	

	<script type="text/javascript">
	   
		
		
        var now = new Date();  
         now.setTime(now.getTime()-24*60*60*1000);
//alert(now.getDate()+'-'+now.getMonth());  
//格式化日，如果小于9，前面补0  
var day = ("0" + now.getDate()).slice(-2);  
//格式化月，如果小于9，前面补0  
var month = ("0" + (now.getMonth() + 1)).slice(-2);  
//拼装完整日期格式  
var today = now.getFullYear()+"-"+(month)+"-"+(day) ;  
//完成赋值  
$('#dayInput').val(today); 
       // document.getElementById('dayInput').valueAsDate = new Date();
       
        function gotourl(a){
        window.location=urlArr[a];
        }
         function excel(a,b,c){
         var dstr =$('#dayInput').val();

         window.open("excel.jsp?fm="+ encodeURI(encodeURI(a))+"&lb="+b+"&dt="+dstr+"&bid="+c);

         
      
        }
        function read_excel(a,b){
         var dstr =$('#dayInput').val();
           window.open("read_xlsx.jsp?fm="+encodeURI(encodeURI(a))+"&lb="+b+"&dt="+dstr,'height=1080,width=1920,scrollbars=yes,status =yes');
          //window.open('http://cztd.windlinker.com:8081/qs/index.html','','height=1080,width=1920,scrollbars=yes,status =yes')
 console.log("read_xlsx.jsp?fm="+encodeURI(encodeURI(a))+"&lb="+b+"&dt="+dstr);
        }
        
        
	</script>

  
    <script>
        laydate({elem:"#dayInput",event:"focus"});var start={elem:"#start",format:"YYYY/MM/DD hh:mm:ss",min:laydate.now(),max:"2099-06-16 23:59:59",istime:true,istoday:false,choose:function(datas){end.min=datas;end.start=datas}};var end={elem:"#end",format:"YYYY/MM/DD hh:mm:ss",min:laydate.now(),max:"2099-06-16 23:59:59",istime:true,istoday:false,choose:function(datas){start.max=datas}};laydate(start);laydate(end);
    </script>
</body>

</html>