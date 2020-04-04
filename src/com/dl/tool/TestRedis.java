/**  
* Title: TestRedis.java  
* Description: 
* @author：xx 
* @date 2019-2-13
* @version 1.0
* Company: www.zoutao.info
*/ 
package com.dl.tool;

/**
 *@class_name：TestRedis
 *@comments:
 *@param:
 *@return: 
 *@author:xx
 *@createtime:2019-2-13
 */

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestRedis {
    private JedisPool pool;  


    /**
     * 初始化连接池
     */
    @Before
    public void init() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(10);
        pool = new JedisPool(jedisPoolConfig, "127.0.0.1", 6379, 2000);
        System.out.println("连接池初始化成功");
        //System.out.println(addr);
    }
    @Test
    public void testPing(){
        // Jedis 实现了java.lang.AutoCloseable接口,所以这里可以用java 1.7 try-with-resources语法自动完成close
        try(Jedis jedis = pool.getResource()){
            //查看服务是否运行 PING
            System.out.println("服务正在运行: "+jedis.ping());
        }
    }
    @Test
    public void testString(){
        try(Jedis jedis = pool.getResource()){
            //设置 redis 字符串数据 SET 10km blog.csdn.net/10km
            jedis.set("10km", "blog.csdn.net/10km");
            // 获取存储的数据并输出
            System.out.println("redis 存储的字符串为: "+ jedis.get("10km"));           
        }
    }
    /**
	 * @return the pool
	 */
	public JedisPool getPool() {
		return pool;
	}
	/**
	 * @param pool the pool to set
	 */
	public void setPool(JedisPool pool) {
		this.pool = pool;
	}
	/**
	 * @return the addr
	 */
	
	@Test
    public void testList() {
        try (Jedis jedis = pool.getResource()) {
            // 选择数据库:  SELECT 2
            jedis.select(2);
            // 存储数据到列表中
            // LPUSH 
            jedis.lpush("phone_list", "Apple");
            jedis.lpush("phone_list", "Huawei");
            jedis.lpush("phone_list", "XiaoMi");

            // 获取存储的数据并输出: LRANGE phone_list 0 2
            List<String> list = jedis.lrange("phone_list", 0, 2);
            for (int i = 0; i < list.size(); i++) {
                System.out.println("phone_list 列表项为: " + list.get(i));
            }
        }
    }
   
   
	/**
     * 程序关闭时，需要调用关闭方法
     */
    @After
    public void end(){      
        if(null != pool){
            pool.destroy();
            System.out.println("连接池关闭");
        }

    }
}
