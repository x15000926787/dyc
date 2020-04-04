package com.dl.impl;
import org.sqlite.JDBC;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.jdbc.core.ResultSetExtractor;

import java.util.ArrayList;
import java.util.List;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bjsxt.vo.RowMapper;;

/**
 * sqlite帮助类，直接创建该类示例，并调用相应的借口即可对sqlite数据库进行操作
 * 
 * 本类基于 sqlite jdbc v56
 * 
 * @author xx
 */
public class SqliteHelper {
    final static Logger logger = LoggerFactory.getLogger(SqliteHelper.class);
    
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private String dbFilePath = null;
    
    /**
     * 构造函数
     * @param dbFilePath sqlite db 文件路径
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public SqliteHelper(String dbFilePath) throws ClassNotFoundException, SQLException {
        this.dbFilePath = dbFilePath;
        connection = getConnection(dbFilePath);
        System.out.println("init info:"+connection.toString());
    }
    
    /**
     * 获取数据库连接
     * @param dbFilePath db文件路径
     * @return 数据库连接
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Connection getConnection(String dbFilePath) throws ClassNotFoundException, SQLException {
        Connection conn = null;
       
       // System.out.println("dddd");
       /* try
        {
         //连接SQLite的JDBC
         Class.forName("org.sqlite.JDBC");       
         //建立一个数据库名lincj.db的连接，如果不存在就在当前目录下创建之
          conn = DriverManager.getConnection("jdbc:sqlite:C:/cstation/w32/ch_0.rdb");  
         //Connection conn = DriverManager.getConnection("jdbc:sqlite:path(路径)/lincj.db");
         Statement stat = conn.createStatement(); 
         stat.executeUpdate( "drop table if exists table1;" );//创建一个表，两列      
         stat.executeUpdate( "create table table1(name varchar(64), age int);" );//创建一个表，两列      
         stat.executeUpdate( "insert into table1 values('aa',12);" ); //插入数据
         stat.executeUpdate( "insert into table1 values('bb',13);" );
         stat.executeUpdate( "insert into table1 values('cc',20);" );
 
         ResultSet rs = stat.executeQuery("select * from table1;"); //查询数据 

         while (rs.next()) { //将查询到的数据打印出来

             System.out.print("name = " + rs.getString("name") + " "); //列属性一
             System.out.println("age = " + rs.getString("age")); //列属性二
         }
         rs.close();
         conn.close(); //结束数据库的连接 
        }
        catch( Exception e )
        {
         e.printStackTrace ( );
        }*/
        try {
        	//连接SQLite的JDBC
            Class.forName("org.sqlite.JDBC");       
            //建立一个数据库名lincj.db的连接，如果不存在就在当前目录下创建之
             conn = DriverManager.getConnection("jdbc:sqlite:"+dbFilePath); 
		} catch ( Exception e) {
			System.out.print(e.toString());
		}
       
        System.out.println("dddd"+"jdbc:sqlite:" + conn.toString());
        return conn;
    }
    
    /**
     * 执行sql查询
     * @param sql sql select 语句
     * @param rse 结果集处理类对象
     * @return 查询结果
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public <T> T executeQuery(String sql, ResultSetExtractor<T> rse) throws SQLException, ClassNotFoundException {
        try {
            resultSet = getStatement().executeQuery(sql);
            T rs = rse.extractData(resultSet);
            return rs;
        } finally {
            destroyed();
        }
    }
    
    /**
     * 执行select查询，返回结果列表
     * 
     * @param sql sql select 语句
     * @param rm 结果集的行数据处理类对象
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException 
     */
    public <T> List<T> executeQuery(String sql, RowMapper<T> rm) throws SQLException, ClassNotFoundException {
        List<T> rsList = new ArrayList<T>();
        try {
            resultSet = getStatement().executeQuery(sql);
            while (resultSet.next()) {
                rsList.add(rm.mapRow(resultSet, resultSet.getRow()));
            }
        } finally {
            destroyed();
        }
        return rsList;
    }
    
    /**
     * 执行数据库更新sql语句
     * @param sql
     * @return 更新行数
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public int executeUpdate(String sql) throws SQLException, ClassNotFoundException {
    	 
        try {
            int c = getStatement().executeUpdate(sql);
            return c;
        } finally {
            destroyed();
        }
        
    }

    /**
     * 执行多个sql更新语句
     * @param sqls
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void executeUpdate(String...sqls) throws SQLException, ClassNotFoundException {
        try {
            for (String sql : sqls) {
                getStatement().executeUpdate(sql);
            }
        } finally {
            destroyed();
        }
    }
    
    /**
     * 执行数据库更新 sql List
     * @param sqls sql列表
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void executeUpdate(List<String> sqls) throws SQLException, ClassNotFoundException {
        try {
            for (String sql : sqls) {
                getStatement().executeUpdate(sql);
            }
        } finally {
            destroyed();
        }
    }
    
    private Connection getConnection() throws ClassNotFoundException, SQLException {
    	if (null == connection)  System.out.println("conn init2 err");
        if (null == connection) connection = getConnection("jdbc:sqlite:"+dbFilePath);
        return connection;
    }
    
    private Statement getStatement() throws SQLException, ClassNotFoundException {
    	 
        if (null == statement) statement = getConnection().createStatement();
        if (null == statement)  System.out.println("stmt init err");
        return statement;
    }
    
    /**
     * 数据库资源关闭和释放
     */
    public void destroyed() {
        try {
        	/*
            if (null != connection) {
                connection.close();
                connection = null;
            }*/
            
            if (null != statement) {
                statement.close();
                statement = null;
            }
            
            if (null != resultSet) {
                resultSet.close();
                resultSet = null;
            }
        } catch (SQLException e) {
            logger.error("Sqlite数据库关闭时异常", e);
        }
    }
    /**
     * 数据库关闭和释放
     */
    public void close() {
        try {
        	
            if (null != connection) {
                connection.close();
                connection = null;
            }
            
           
        } catch (SQLException e) {
            logger.error("Sqlite数据库关闭时异常", e);
        }
    }
}
