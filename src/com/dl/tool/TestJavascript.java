/**  
* Title: TestJedis.java  
* Description: 
* @author：xx 
* @date 2019-6-18
* @version 1.0
* Company: www.zoutao.info
*/ 
package com.dl.tool;

/**
 *@class_name：TestJedis
 *@comments:
 *@param:
 *@return: 
 *@author:xx
 *@createtime:2019-6-18
 */

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TestJavascript {
	public static void main(String[] args) {

        try{
            BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir")+"/src/zjdy.txt"));//构造一个BufferedReader类来读取文件
            String s = null;
            System.out.println("ddd");
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                //result.append(System.lineSeparator()+s);
                System.out.println(s);
                String[] calc=s.split("=");
                System.out.println(calc);
                String[] vals=calc[1].split("\\{|\\}");
                Pattern p = Pattern.compile("Fun[^\\}]+");
                Matcher m = p.matcher(s);//strTmp替换成你的字符串
                while (m.find()) {
                    System.out.println("find: "+m.group(0));
                }
                System.out.println(s);
            }
            br.close();
        }catch(Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
        }
            try

            {
                    ScriptEngine engine = (new ScriptEngineManager()).getEngineByName("JavaScript");
                    List<Object> imgList = Arrays.asList(1,2,3);


                    File fName = new File(System.getProperty("user.dir")+"/src/myscript.js");
                    Reader scriptReader = new FileReader(fName);

                    engine.eval(scriptReader);

                    Invocable invocable = (Invocable) engine;
                    System.out.println(invocable.invokeFunction("arrsum", imgList));


            }catch(NoSuchMethodException e){
                    System.out.println("check updown err: " +  e.toString() );
            }
            catch(FileNotFoundException e){
                    System.out.println("System.getProperty(\"user.dir\")+\"/src/myscript.js\" err: " +  e.toString() );
            }
            catch (ScriptException e) {
                    System.out.println("check updown err: " +  e.toString() );
                    e.printStackTrace();
            }

    }
	
}
