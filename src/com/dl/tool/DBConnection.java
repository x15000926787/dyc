package com.dl.tool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DBConnection  {
	/*private String Driver="com.mysql.jdbc.Driver";
	private String URL="jdbc:mysql://192.168.3.203:3306/scada";
	private String dataBaseName="shcs";
	private String dataBasePassword="@Shcs2017";*/
	//private String dataBasePassword="Shcs123";
	public Connection con=null;
	public PreparedStatement pstmt=null;
	public ResultSet rs=null;
    public JdbcTemplate jdbcTemplate;
	private String project_id;
	private static final Logger logger = LogManager.getLogger(DBConnection.class);
	public DBConnection(){
		
		
		jdbcTemplate = FirstClass.getJdbcTemplate();
		try {
			con = jdbcTemplate.getDataSource().getConnection();
			//if (null!=con) logger.warn("get conn sesscess");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*try{
			Class.forName(Driver);
		}catch(ClassNotFoundException e){
			logger.warn("conn err:"+e.getMessage());
		}*/
		//getConnection();
	}
	
	
	public List<Map<String, Object>> queryforList(String sql){
		List<Map<String, Object>> userData = null;
		
		try{
			userData = jdbcTemplate.queryForList(sql);
			
		}catch(Exception e){
			logger.warn("open database err:"+e.getMessage());
		}
		return userData;
	}
	
	public int getexecuteUpdate(){
		int flag=0;
		
		try{
			flag=pstmt.executeUpdate();
		}catch(SQLException e){
			logger.error("exec sql err:"+e.getMessage());
		}
		return flag;
	}
	
	public PreparedStatement setPreparedStatement(String sql){
		/*if(con==null){
			this.getConnection();
		}*/
		try{
			pstmt=con.prepareStatement(sql);
			//if (null!=pstmt) logger.warn("get pstmt sesscess"+pstmt.isClosed());
		}catch(SQLException e){
			logger.warn("PreparedStatement:"+e.getMessage());
		}
		return pstmt;
	}
	public ResultSet getPrepatedResultSet(){
		try{
			rs=pstmt.executeQuery();			
		}catch(SQLException e){
			logger.warn("executeQuery:"+e.getMessage());
		}

		return rs;
	}
	
	public void getClose(){
		try{
			if(rs!=null){
				rs.close();
			}
			if(pstmt!=null){
				pstmt.close();
			}
			if(con!=null){
				con.close();
			}
		}catch(SQLException e){
			logger.warn("getClose:"+e.getMessage());
		}
	}
//	public static void main(String[] ages){
//		new DBConnection();
//	}
}
