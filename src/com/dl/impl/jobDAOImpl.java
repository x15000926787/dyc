package com.dl.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dl.quartz.QuartzJob;
import com.dl.quartz.QuartzManager;
import com.dl.tool.AnaUtil;
import com.dl.tool.FirstClass;
import com.dl.tool.saveHistyc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.TriggerUtils;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.scheduling.support.CronSequenceGenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class jobDAOImpl extends JdbcDaoSupport{
	static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	//private static final Logger logger = LogManager.getLogger(jobDAOImpl.class);
	//public static AnaUtil myana=new AnaUtil();

	public String reloadana()
	{
		String jsonString="{\"result\":0}";
		try {
			AnaUtil.loadAna_v();
			jsonString="{\"result\":1}";
		}catch (Exception e)
		{

		}
		return jsonString;
	}
	public String restarthistyc()
	{
		String jsonString="{\"result\":0}";
		JSONObject objana = new JSONObject();
		int i,j,maxsav=0,maxdnsav=0;
		String key = null;
		String value = null;
		i=0;j=0;
		Map<String,Object> map =null;//new HashMap<>();// (HashMap)userData.get(i);
		String sql="select rtuno,sn,kkey,saveno,upperlimit,lowerlimit,pulsaveno,chgtime from prtuana ";

		//if(rand.equalsIgnoreCase(imagerand)){
		try{
			List<Map<String, Object>> userData = getJdbcTemplate().queryForList(sql);
			int size=userData.size() ;
			if (size> 0)
			{

				for ( i = 0; i<size; i++)
				{

					map = (HashMap) userData.get(i);

					/**/
		    			    		/* Map tmap=new HashMap<String, String>();
		    			    		 for (Map.Entry<String,Object> entry : map.entrySet()){
			    			    			key = entry.getKey();
			    			    			value = entry.getValue().toString();
			    			    			tmap.put(key, value);
			    			    		}*/
					//results.add(map);
					maxsav=(Integer.parseInt(map.get("saveno").toString())>maxsav?Integer.parseInt(map.get("saveno").toString()):maxsav);
					try
					{
						maxdnsav=(Integer.parseInt(map.get("pulsaveno").toString())>maxdnsav?Integer.parseInt(map.get("pulsaveno").toString()):maxdnsav);
					}
					catch(Exception e)
					{}
					objana.put(map.get("kkey").toString(),map);

				}
				objana.put("maxsav",maxsav);
				objana.put("maxdnsav",maxdnsav);
			}


			// pstmt.close();
			// dbcon.getClose();
		}catch(Exception e){
			FirstClass.logger.warn("出错了"+e.toString());
		}
		//FirstClass.logger.warn(objana);
		sql="select rtuno,sn,type ,kkey,chgtime from prtudig ";
		//if(rand.equalsIgnoreCase(imagerand)){
		try{
			List<Map<String, Object>> userData = getJdbcTemplate().queryForList(sql);
			int size=userData.size() ;
			if (size> 0)
			{

				for ( i = 0; i<size; i++)
				{
					map = (HashMap) userData.get(i);
					objana.put(map.get("kkey").toString(),map);
				}
			}
		}catch(Exception e){
			FirstClass.logger.warn("出错了"+e.toString());
		}
		FirstClass.logger.warn(objana);
		sql="select cronstr from timetask where type=3 ";
		//if(rand.equalsIgnoreCase(imagerand)){
		try{
			Map<String, Object> vmap = getJdbcTemplate().queryForMap(sql);
			if (!vmap.isEmpty()){
				value = vmap.get("cronstr").toString();
			}
		}catch(Exception e){
			FirstClass.logger.warn("cronstr出错了"+e.toString());
		}

		FirstClass.logger.warn(value);

		try
		{

			QuartzManager.removeJob("saveHistyc");
			QuartzManager.addJob("saveHistyc",saveHistyc.class,value,objana);
		}catch(Exception e){
			FirstClass.logger.warn("qmanager出错了"+e.toString());
		}


		return jsonString;
	}
	public String getquartz()
	{
		String jsonString="{\"result\":0}";
		String sql ="SELECT * from timetask where type=1 order by id";
		log(sql);
		List userData = getJdbcTemplate().queryForList(sql);
		int size=userData.size() ;
		if (size> 0)
		{
			Object arr[] = new Object[size];
			for (int i = 0; i<size; i++)
			{
				ArrayList kv = new ArrayList();
				Map listData = (Map)userData.get(i);
				kv.add(listData.get("id"));
				kv.add(listData.get("name"));
				kv.add(listData.get("cronstr"));
				kv.add(listData.get("description"));
				kv.add("<a id='subBtn' href='javascript:void(0);' title='保存'><span class='fa fa-save'></span></a>&nbsp;&nbsp;&nbsp;&nbsp;<a id='delBtn' href='javascript:void(0);' title='删除'><span class='glyphicon glyphicon-trash'></span></a>&nbsp;&nbsp;&nbsp;&nbsp;<a id='infoBtn' href='javascript:void(0);' title='权限详情'><span class='fa fa-info-circle' onclick=javascript:getQuartzDetail("+listData.get("id")+",'"+listData.get("name")+"')></span></a>");
				arr[i]=kv;


			}
			java.util.Map<String,Object> map1 = new HashMap<String,Object>();
			map1.put("result",1);
			map1.put("data",arr);
			jsonString = JSON.toJSONString(map1);
		}
		return jsonString;
	}
	public String getunion()
	{
		String jsonString="{\"result\":0}";
		String sql ="SELECT * from unionsel where valid=1 order by id";
		log(sql);
		List userData = getJdbcTemplate().queryForList(sql);
		int size=userData.size() ;
		if (size> 0)
		{
			Object arr[] = new Object[size];
			for (int i = 0; i<size; i++)
			{
				ArrayList kv = new ArrayList();
				Map listData = (Map)userData.get(i);
				kv.add(listData.get("id"));
				kv.add(listData.get("name"));
				kv.add(listData.get("type"));
				kv.add(listData.get("description"));
				//kv.add("<a id='saveBtn' href='javascript:void(0);' title='保存'><span class='fa fa-save'></span></a>&nbsp;&nbsp;&nbsp;&nbsp;<a id='delBtn' href='javascript:void(0);' title='删除'><span class='glyphicon glyphicon-trash'></span></a>&nbsp;&nbsp;&nbsp;&nbsp;<a id='infoBtn' href='javascript:void(0);' title='详情'><span class='fa fa-info-circle' onclick=javascript:getUnionDetail("+listData.get("id")+",'"+listData.get("name")+",'"+listData.get("type")+"')></span></a>");
				arr[i]=kv;


			}
			java.util.Map<String,Object> map1 = new HashMap<String,Object>();
			map1.put("result",1);
			map1.put("data",arr);
			jsonString = JSON.toJSONString(map1);
		}
		return jsonString;
	}
	public String savejobEdit(String jsonStr,Integer sn) throws SQLException, ParseException
	{
		String jsonString="{\"result\":0}";
		JSONObject jsonObj=JSON.parseObject(jsonStr);
		JSONArray addArr= jsonObj.getJSONArray("addArr");
		JSONArray delArr= jsonObj.getJSONArray("delArr");
		JSONArray editArr= jsonObj.getJSONArray("editArr");
		Connection con = null;
		PreparedStatement pstm = null;
		Statement stm = null;
		String sql="";
		int i;
		try
		{
			con = getJdbcTemplate().getDataSource().getConnection();
			con.setAutoCommit(false);
			stm = con.createStatement();
			for ( i = 0; i <  addArr.size(); i++)
			{
				JSONArray temp=addArr.getJSONArray(i);

				//String text2=temp.getString(2)+",以"+temp.getString(4)+"MW/MIN速率"+upDown+temp.getString(6)+"MW";

				sql="Insert into timetask (NAME,cronstr,description,type) values ('"+temp.getString(1)+"','"+temp.getString(2)+"','"+temp.getString(3)+"',1)";

				stm.addBatch(sql);

			}
			for (int d = 0; d <  delArr.size(); d++)
			{
				sql="delete from timetask where id="+delArr.getInteger(d);
				stm.addBatch(sql);
				sql="delete from timetask_detail where taskid="+delArr.getInteger(d);
				stm.addBatch(sql);
				//log("delete:  " + sql);
			}
			for (int e = 0; e <  editArr.size(); e++)
			{
				JSONArray  temp=editArr.getJSONArray(e);

				//String text2=temp.getString(2)+",以"+temp.getString(4)+"MW/MIN速率"+temp.getString(5)+"MW";
				sql="update timetask set NAME='"+temp.getString(1)+"' ,cronstr='"+temp.getString(2)+"',description='"+temp.getString(3)+"' where ID="+temp.getInteger(0);
				log("UPDATE  " + sql);
				stm.addBatch(sql);
			}
			stm.executeBatch();
			con.commit();


			QuartzManager.shutdownJobs();





			sql="select * from timetask where type=1";
			List<Map<String, Object>> tasktype1 = new ArrayList<Map<String, Object>>();
			//if(rand.equalsIgnoreCase(imagerand)){
			try{
				tasktype1= getJdbcTemplate().queryForList(sql);

				for (Map<String, Object> tmap:tasktype1){

					i=(int)tmap.get("id");
					QuartzManager.removeJob("qjob"+i);
					Map<String, String> maps = new HashMap<String,String>();
					//maps.put("vv", taskarray.get(""+i).toString());
					maps.put("vv", ""+i);
					QuartzManager.addJob("qjob"+i, QuartzJob.class,tmap.get("cronstr").toString(),maps);
					FirstClass.logger.warn("add定时ao/do任务:"+tmap.get("cronstr").toString()+maps.toString());

					i++;

				}

			}catch(Exception e){
				FirstClass.logger.error("生成定时任务出错了"+e.toString());
			}

			QuartzManager.startJobs();

			Map<String,Object> map1 = new HashMap<String,Object>();
			map1.put("result",1);
			jsonString = JSON.toJSONString(map1);
		}
		catch (SQLException e)
		{
			e.printStackTrace();

		}finally
		{
			stm.close();
			stm = null;
			con.close();
			con = null;
		}
		return jsonString;
	}
	public String saveUnionEdit(String jsonStr,Integer sn) throws SQLException
	{
		String jsonString="{\"result\":0}";
		JSONObject jsonObj=JSON.parseObject(jsonStr);
		JSONArray addArr= jsonObj.getJSONArray("addArr");
		JSONArray delArr= jsonObj.getJSONArray("delArr");
		JSONArray editArr= jsonObj.getJSONArray("editArr");
		Connection con = null;
		PreparedStatement pstm = null;
		Statement stm = null;
		String sql="";
		int i;
		try
		{
			con = getJdbcTemplate().getDataSource().getConnection();
			con.setAutoCommit(false);
			stm = con.createStatement();
			for ( i = 0; i <  addArr.size(); i++)
			{
				JSONArray temp=addArr.getJSONArray(i);

				//String text2=temp.getString(2)+",以"+temp.getString(4)+"MW/MIN速率"+upDown+temp.getString(6)+"MW";

				sql="Insert into unionsel (NAME,type,description,valid) values ('"+temp.getString(1)+"','"+temp.getString(2)+"','"+temp.getString(3)+"',1)";

				stm.addBatch(sql);

			}
			for (int d = 0; d <  delArr.size(); d++)
			{
				sql="delete from unionsel where id="+delArr.getInteger(d);
				stm.addBatch(sql);
				sql="delete from unionsel_detail where uid="+delArr.getInteger(d);
				stm.addBatch(sql);
				//log("delete:  " + sql);
			}
			for (int e = 0; e <  editArr.size(); e++)
			{
				JSONArray  temp=editArr.getJSONArray(e);

				//String text2=temp.getString(2)+",以"+temp.getString(4)+"MW/MIN速率"+temp.getString(5)+"MW";
				sql="update unionsel set name='"+temp.getString(1)+"',description='"+temp.getString(3)+"' where ID="+temp.getInteger(0);
				log("UPDATE  " + sql);
				stm.addBatch(sql);
			}
			stm.executeBatch();
			con.commit();







//QuartzManager.shutdownJobs();
			/*sql="select * from timetask where type=1";
			List<Map<String, Object>> tasktype1 = new ArrayList<Map<String, Object>>();
			//if(rand.equalsIgnoreCase(imagerand)){
			try{
				tasktype1= getJdbcTemplate().queryForList(sql);

				for (Map<String, Object> tmap:tasktype1){

					i=(int)tmap.get("id");
					QuartzManager.removeJob("qjob"+i);
					Map<String, String> maps = new HashMap<String,String>();
					//maps.put("vv", taskarray.get(""+i).toString());
					maps.put("vv", ""+i);
					QuartzManager.addJob("qjob"+i, QuartzJob.class,tmap.get("cronstr").toString(),maps);
					FirstClass.logger.warn("add定时ao/do任务:"+tmap.get("cronstr").toString()+maps.toString());

					i++;

				}

			}catch(Exception e){
				FirstClass.logger.error("生成定时任务出错了"+e.toString());
			}

			QuartzManager.startJobs();*/

			Map<String,Object> map1 = new HashMap<String,Object>();
			map1.put("result",1);
			jsonString = JSON.toJSONString(map1);
		}
		catch (SQLException e)
		{
			e.printStackTrace();

		}finally
		{
			stm.close();
			stm = null;
			con.close();
			con = null;
		}
		return jsonString;
	}

	public String newDetail(String task,String kkey,String vv) throws SQLException, ParseException
	{
		String jsonString="{\"result\":0}";

		Connection con = null;
		PreparedStatement pstm = null;
		Statement stm = null;
		String sql="";
		try
		{
			con = getJdbcTemplate().getDataSource().getConnection();
			con.setAutoCommit(false);
			stm = con.createStatement();


			//String text2=temp.getString(2)+",以"+temp.getString(4)+"MW/MIN速率"+upDown+temp.getString(6)+"MW";

			sql="Insert into timetask_detial (taskid,kkey,val,type) values ("+task+",'"+kkey+"',"+vv+",1)";
			log(sql);
			stm.addBatch(sql);


			stm.executeBatch();
			con.commit();
			con.setAutoCommit(true);
			Map<String,Object> map1 = new HashMap<String,Object>();
			map1.put("result",1);
			jsonString = JSON.toJSONString(map1);
		}
		catch (SQLException e)
		{
			e.printStackTrace();

		}finally
		{
			stm.close();
			stm = null;
			con.close();
			con = null;
		}
		return jsonString;
	}
	public String saveDetailEdit(String jsonStr,Integer sn) throws SQLException, ParseException
	{
		String jsonString="{\"result\":0}";
		JSONObject jsonObj=JSON.parseObject(jsonStr);
		JSONArray addArr= jsonObj.getJSONArray("addArr");
		JSONArray delArr= jsonObj.getJSONArray("delArr");
		JSONArray editArr= jsonObj.getJSONArray("editArr");
		Connection con = null;
		PreparedStatement pstm = null;
		Statement stm = null;
		String sql="";
		try
		{
			con = getJdbcTemplate().getDataSource().getConnection();
			con.setAutoCommit(false);
			stm = con.createStatement();
			/*for (int i = 0; i <  addArr.size(); i++)
			{
				JSONArray temp=addArr.getJSONArray(i);

				//String text2=temp.getString(2)+",以"+temp.getString(4)+"MW/MIN速率"+upDown+temp.getString(6)+"MW";

				sql="Insert into timetask_detial (NAME,cronstr,description,type) values ('"+temp.getString(1)+"','"+temp.getString(2)+"','"+temp.getString(3)+"',1)";

				stm.addBatch(sql);

			}*/
			for (int d = 0; d <  delArr.size(); d++)
			{
				sql="delete from timetask_detial where id="+delArr.getInteger(d);
				stm.addBatch(sql);
				//log("delete:  " + sql);
			}
			/*for (int e = 0; e <  editArr.size(); e++)
			{
				JSONArray  temp=editArr.getJSONArray(e);

				//String text2=temp.getString(2)+",以"+temp.getString(4)+"MW/MIN速率"+temp.getString(5)+"MW";
				sql="update timetask set NAME='"+temp.getString(1)+"' ,cronstr='"+temp.getString(2)+"',description='"+temp.getString(3)+"' where ID="+temp.getInteger(0);
				log("UPDATE  " + sql);
				stm.addBatch(sql);
			}*/
			stm.executeBatch();
			con.commit();
			con.setAutoCommit(true);
			Map<String,Object> map1 = new HashMap<String,Object>();
			map1.put("result",1);
			jsonString = JSON.toJSONString(map1);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			try {
				if(!con.isClosed()){

				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}finally
		{
			stm.close();
			stm = null;
			con.close();
			con = null;
		}
		return jsonString;
	}
	public String saveUnionDetailEdit(String jsonStr,Integer sn) throws SQLException, ParseException
	{
		String jsonString="{\"result\":0}";
		JSONObject jsonObj=JSON.parseObject(jsonStr);
		JSONArray addArr= jsonObj.getJSONArray("addArr");
		JSONArray delArr= jsonObj.getJSONArray("delArr");
		JSONArray editArr= jsonObj.getJSONArray("editArr");
		Connection con = null;
		PreparedStatement pstm = null;
		Statement stm = null;
		String sql="";
		try
		{
			con = getJdbcTemplate().getDataSource().getConnection();
			con.setAutoCommit(false);
			stm = con.createStatement();
			for (int i = 0; i <  addArr.size(); i++)
			{
				JSONArray temp=addArr.getJSONArray(i);

				//String text2=temp.getString(2)+",以"+temp.getString(4)+"MW/MIN速率"+upDown+temp.getString(6)+"MW";

				sql="Insert into unionsel_detail (uid,saveno,rate) values ('"+temp.getString(1)+"','"+temp.getString(2)+"','"+temp.getString(3)+"')";

				stm.addBatch(sql);

			}
			for (int d = 0; d <  delArr.size(); d++)
			{
				sql="delete from unionsel_detail where id="+delArr.getInteger(d);
				stm.addBatch(sql);
				//log("delete:  " + sql);
			}
			for (int e = 0; e <  editArr.size(); e++)
			{
				JSONArray  temp=editArr.getJSONArray(e);

				//String text2=temp.getString(2)+",以"+temp.getString(4)+"MW/MIN速率"+temp.getString(5)+"MW";
				sql="update unionsel_detail set uid='"+temp.getString(1)+"' ,saveno='"+temp.getString(2)+"',rate='"+temp.getString(3)+"' where ID="+temp.getInteger(0);
				log("UPDATE  " + sql);
				stm.addBatch(sql);
			}
			stm.executeBatch();
			con.commit();
			con.setAutoCommit(true);
			Map<String,Object> map1 = new HashMap<String,Object>();
			map1.put("result",1);
			jsonString = JSON.toJSONString(map1);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			try {
				if(!con.isClosed()){

				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}finally
		{
			stm.close();
			stm = null;
			con.close();
			con = null;
		}
		return jsonString;
	}
	public String getetype()
	{
		String jsonString="{\"result\":0}";
		String sql ="SELECT * from dotype_info";
		log(sql);
		List userData = getJdbcTemplate().queryForList(sql);
		int size=userData.size() ;
		if (size> 0)
		{
			Object arr[] = new Object[size];
			for (int i = 0; i<size; i++)
			{
				ArrayList kv = new ArrayList();
				Map listData = (Map)userData.get(i);
				kv.add(listData.get("e_type"));
				kv.add(listData.get("e_zt"));
				kv.add(listData.get("e_info"));
				//kv.add(listData.get("description"));
				//kv.add("<a id='subBtn' href='javascript:void(0);' title='保存'><span class='fa fa-save'></span></a>&nbsp;&nbsp;&nbsp;&nbsp;<a id='delBtn' href='javascript:void(0);' title='删除'><span class='glyphicon glyphicon-trash'></span></a>&nbsp;&nbsp;&nbsp;&nbsp;<a id='infoBtn' href='javascript:void(0);' title='权限详情'><span class='fa fa-info-circle' onclick=javascript:getuserauthor("+listData.get("id")+",'"+listData.get("name")+"')></span></a>");
				arr[i]=kv;


			}
			java.util.Map<String,Object> map1 = new HashMap<String,Object>();
			map1.put("result",1);
			map1.put("data",arr);
			jsonString = JSON.toJSONString(map1);
		}
		return jsonString;
	}
	public String getfullname()
	{
		String jsonString="{\"result\":0}"; //SELECT ID,NAME FROM A WHERE　NOT EXIST (SELECT * FROM B WHERE A.ID=B.AID)
		//String sql ="SELECT * from prtuana_fullname where ao_do=1 and not exist (select * from timetask_detial where prtuana_fullname.kkey=timetask_detial.kkey)";
		String sql ="SELECT * from ao_do_fullname where ao_do=1 and name is not null";//" and kkey not in (select kkey from timetask_detial )";
		List userData = getJdbcTemplate().queryForList(sql);
		int size=userData.size() ;
		Object arr[] = new Object[size];
		if (size> 0)
		{

			for (int i = 0; i<size; i++)
			{
				ArrayList kv = new ArrayList();
				Map listData = (Map)userData.get(i);
				kv.add(listData.get("name"));
				kv.add(listData.get("kkey"));
				kv.add(listData.get("etype"));
				//kv.add(listData.get("description"));
				//kv.add("<a id='subBtn' href='javascript:void(0);' title='保存'><span class='fa fa-save'></span></a>&nbsp;&nbsp;&nbsp;&nbsp;<a id='delBtn' href='javascript:void(0);' title='删除'><span class='glyphicon glyphicon-trash'></span></a>&nbsp;&nbsp;&nbsp;&nbsp;<a id='infoBtn' href='javascript:void(0);' title='权限详情'><span class='fa fa-info-circle' onclick=javascript:getuserauthor("+listData.get("id")+",'"+listData.get("name")+"')></span></a>");
				arr[i]=kv;


			}
		}
		sql ="SELECT * from ao_do_fullname where ao_do=2 and name is not null";//and kkey not in (select kkey from timetask_detial )";

		userData = getJdbcTemplate().queryForList(sql);
		size=userData.size() ;
		Object arr2[] = new Object[size];
		if (size> 0) {

			for (int i = 0; i < size; i++) {
				ArrayList kv = new ArrayList();
				Map listData = (Map) userData.get(i);
				kv.add(listData.get("name"));
				kv.add(listData.get("kkey"));
				kv.add(listData.get("etype"));
				//kv.add(listData.get("description"));
				//kv.add("<a id='subBtn' href='javascript:void(0);' title='保存'><span class='fa fa-save'></span></a>&nbsp;&nbsp;&nbsp;&nbsp;<a id='delBtn' href='javascript:void(0);' title='删除'><span class='glyphicon glyphicon-trash'></span></a>&nbsp;&nbsp;&nbsp;&nbsp;<a id='infoBtn' href='javascript:void(0);' title='权限详情'><span class='fa fa-info-circle' onclick=javascript:getuserauthor("+listData.get("id")+",'"+listData.get("name")+"')></span></a>");
				arr2[i] = kv;


			}
		}
		java.util.Map<String,Object> map1 = new HashMap<String,Object>();
		map1.put("result",1);
		map1.put("aos",arr);
		map1.put("dos",arr2);
		jsonString = JSON.toJSONString(map1);

		return jsonString;
	}
	public String getQuartzDetail(String sn,String nm)
	{
		String jsonString="{\"result\":0}";
		String sql ="SELECT * from timetask_detial a,ao_do_fullname b where a.kkey=b.kkey and a.taskid="+sn+" order by name";
		log(sql);
		List userData = getJdbcTemplate().queryForList(sql);
		int size=userData.size() ;
		if (size> 0)
		{
			Object arr[] = new Object[size];
			for (int i = 0; i<size; i++)
			{
				ArrayList kv = new ArrayList();
				Map listData = (Map)userData.get(i);
				kv.add(listData.get("id"));
				//kv.add(listData.get("type"));
				kv.add(listData.get("name"));
				kv.add(listData.get("kkey"));
				kv.add(listData.get("val"));

				kv.add("<a id='deldet' href='javascript:void(0);' title='删除'><span class='glyphicon glyphicon-trash'></span></a>");
				kv.add(listData.get("etype"));

				arr[i]=kv;


			}
			java.util.Map<String,Object> map1 = new HashMap<String,Object>();
			map1.put("result",1);
			map1.put("pnm",nm);
			map1.put("data",arr);
			jsonString = JSON.toJSONString(map1);
		}
		return jsonString;
	}
	public String getUnionDetail(String sn,int tp)
	{
		String jsonString="{\"result\":0}";
		String sql ="SELECT id,b.fname,a.saveno,a.rate from unionsel_detail a,prtuana_fullname b where a.saveno=b.saveno and a.uid="+sn+"  order by id";
		if (tp==1) sql ="SELECT id,b.fname,a.saveno,a.rate from unionsel_detail a,prtupul_fullname b where a.saveno=b.saveno and a.uid="+sn+"  order by id";
		log(sql);
		List userData = getJdbcTemplate().queryForList(sql);
		int size=userData.size() ;
		if (size> 0)
		{
			Object arr[] = new Object[size];
			for (int i = 0; i<size; i++)
			{
				ArrayList kv = new ArrayList();
				Map listData = (Map)userData.get(i);
				kv.add(listData.get("id"));
				//kv.add(listData.get("type"));
				kv.add(listData.get("fname"));
				kv.add(listData.get("saveno"));
				kv.add(listData.get("rate"));

				//kv.add("<a id='savedet' href='javascript:void(0);' title='保存'><span class='fa fa-save'></span></a>&nbsp;&nbsp;&nbsp;&nbsp;<a id='deldet' href='javascript:void(0);' title='删除'><span class='glyphicon glyphicon-trash'></span></a>");


				arr[i]=kv;


			}
			java.util.Map<String,Object> map1 = new HashMap<String,Object>();
			map1.put("result",1);

			map1.put("data",arr);
			jsonString = JSON.toJSONString(map1);
		}
		return jsonString;
	}
	public String getHisVal()
	{

		String jsonString="{\"result\":0}";
		Calendar beforeTime = Calendar.getInstance();
		beforeTime.add(Calendar.DATE, -4);
		String rltstr = "";
		Date beforeD = beforeTime.getTime();

		String before5 = new SimpleDateFormat("yyyyMMdd").format(beforeD);  //
		String sql ="SELECT * from h_inc where did>="+before5+" and saveno id (2,7,11,19,17) and kind=0";
		log(sql);
		List userData = getJdbcTemplate().queryForList(sql);
		int size=userData.size() ;
		if (size> 0)
		{
			Object arr[] = new Object[size];
			for (int i = 0; i<size; i++)
			{
				ArrayList kv = new ArrayList();
				Map listData = (Map)userData.get(i);
				kv.add(listData.get("id"));
				kv.add(listData.get("name"));
				kv.add(listData.get("cronstr"));
				kv.add(listData.get("description"));
				kv.add("<a id='subBtn' href='javascript:void(0);' title='保存'><span class='fa fa-save'></span></a>&nbsp;&nbsp;&nbsp;&nbsp;<a id='delBtn' href='javascript:void(0);' title='删除'><span class='glyphicon glyphicon-trash'></span></a>&nbsp;&nbsp;&nbsp;&nbsp;<a id='infoBtn' href='javascript:void(0);' title='权限详情'><span class='fa fa-info-circle' onclick=javascript:getuserauthor("+listData.get("id")+",'"+listData.get("name")+"')></span></a>");
				arr[i]=kv;


			}
			java.util.Map<String,Object> map1 = new HashMap<String,Object>();
			map1.put("result",1);
			map1.put("data",arr);
			jsonString = JSON.toJSONString(map1);
		}
		return jsonString;
	}
	public String getcronstr(String cron)  {
		String jsonString="{\"result\":0}";
		String ncron="";
		java.util.Map<String,Object> map1 = new HashMap<String,Object>();
		String[] croncnt=cron.split(" ");
		ncron = croncnt[0];
		for(int i=1;i<6;i++)
			ncron =ncron +" "+ croncnt[i];
		CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(ncron);


		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		List<String> list = new ArrayList<>(10);

		Date nextTimePoint = new Date();
		for (int i = 0; i < 10; i++) {
			// 计算下次时间点的开始时间
			nextTimePoint = cronSequenceGenerator.next(nextTimePoint);
			list.add(sdf.format(nextTimePoint));
		}
		map1.put("data",list);
		map1.put("result",1);
		jsonString = JSON.toJSONString(map1);

		return jsonString;
	}
	public String getcronstr_prv(String cron)throws ParseException, InterruptedException  {
		String jsonString="{\"result\":0}";

		java.util.Map<String,Object> map1 = new HashMap<String,Object>();
		List<String> list = new ArrayList<>(10);

		CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
		cronTriggerImpl.setCronExpression(cron);//这里写要准备猜测的cron表达式
		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		calendar.add(Calendar.DATE, -31);
		List<Date> dates = TriggerUtils.computeFireTimesBetween(cronTriggerImpl, null, calendar.getTime(), now);//这个是重点，一行代码搞定~~
		System.out.println(dates.size());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		list.add(dateFormat.format(dates.get(dates.size()-1)));
		map1.put("data",list);
		map1.put("result",1);
		jsonString = JSON.toJSONString(map1);

		return jsonString;
	}
	public void log(Object msg)
	{
		System.out.println(df.format(new Date())+":"+msg);


	}
}
