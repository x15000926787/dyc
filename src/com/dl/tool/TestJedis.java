/**  
* Title: TestJedis.java  
* Description: 
* @author：xx 
* @date 2019-6-18
* @version 1.0
* Company: www.zoutao.info
*/ 
package com.dl.tool;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
/**
 *@class_name：TestJedis
 *@comments:
 *@param:
 *@return: 
 *@author:xx
 *@createtime:2019-6-18
 */
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class TestJedis {
	public static void main(String[] args) {
        //新建JSONObject对象
        JSONObject object1 = new JSONObject();
        
        //1.在JSONObject对象中放入键值对
        object1.put("name", "张三");
        object1.put("name1", "张三1");
        object1.put("name2", "张三2");
        
        //2.根据key获取value
        String name = (String) object1.get("name");
        System.out.println(name);
        
        //3.获取JSONObject中的键值对个数
        int size = object1.size();
        System.out.println(size);
        
        //4.判断是否为空
        boolean result = object1.isEmpty();
        System.out.println(result);
        
        //5.是否包含对应的key值，包含返回true，不包含返回false
        boolean isContainsKeyResult = object1.containsKey("name");
        System.out.println(isContainsKeyResult);
        
        //6.是否包含对应的value值，包含返回true，不包含返回false
        boolean isContainsValueResult = object1.containsValue("王五");
        System.out.println(isContainsValueResult);
        
        //7.JSONObjct对象中的value是一个JSONObject对象，即根据key获取对应的JSONObject对象；
        JSONObject object2 = new JSONObject();
        //将jsonobject对象作为value进行设置
        object2.put("student1", object1);
        JSONObject student =object2.getJSONObject("student1");
        System.out.println(student);
        
        //8.如果JSONObject对象中的value是一个JSONObject数组，既根据key获取对应的JSONObject数组；
        JSONObject objectArray = new JSONObject();
        //创建JSONArray数组
        JSONArray jsonArray = new JSONArray();
        //在JSONArray数组设值:jsonArray.add(int index, Object value);
        jsonArray.add(0, "this is a jsonArray value");
        jsonArray.add(1, "another jsonArray value");
        objectArray.put("testArray", jsonArray);
        //获取JSONObject对象中的JSONArray数组
        JSONArray jsonArray2 = objectArray.getJSONArray("testArray");
        System.out.println(jsonArray2);
        
        //9.remove.根据key移除JSONObject对象中的某个键值对
        object1.remove("name");
        System.out.println(object1);
        
        //10.取得JSONObject对象中key的集合
        Set<String> keySet= object1.keySet();
        for (String key : keySet) {
            System.out.print("   "+key);
        }
        System.out.println();
        
        //11.取得JSONObject对象中的键和值的映射关系
        Set<Map.Entry<String, Object>> entrySet = object1.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            System.out.println("entry:"+entry);
        }
        
        //12.转换为json字符串
        String str1 = object1.toJSONString();
        System.out.println(str1);
        String str2 =object1.toString();
        System.out.println(str2);
        
        
        Map<String, Object> maps = new HashMap<String,Object>();
        maps.put("dd", 1);
        maps.put("dd2", 1);
        object1.put("maps", maps);
        String str11 = object1.toJSONString();
        System.out.println(str11);
    }
	
}
