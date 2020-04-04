/**  
* Title: CharTools.java  
* Description: 
* @author：xx 
* @date 2019-7-6
* @version 1.0
* Company: www.zoutao.info
*/ 
package com.dl.tool;

/**
 *@class_name：CharTools
 *@comments:
 *@param:
 *@return: 
 *@author:xx
 *@createtime:2019-7-6
 */
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.URLDecoder;
/**
* <p>Title:字符编码工具类 </p>
* <p>Description:  </p>
* <p>Copyright: flashman.com.cn Copyright (c) 2005</p>
* <p>Company: flashman.com.cn </p>
* @author: jeffzhu
* @version 1.
*/
public class CharTools {
/**
* 转换编码 ISO-8859-1到GB2312
* @param text
* @return
*/
public String ISO2GB(String text) {
String result = "";
try {
result = new String(text.getBytes("ISO-8859-1"), "GB2312");
}
catch (UnsupportedEncodingException ex) {
result = ex.toString();
}
return result;
}
/**
* 转换编码 GB2312到ISO-8859-1
* @param text
* @return
*/
public String GB2ISO(String text) {
String result = "";
try {
result = new String(text.getBytes("GB2312"), "ISO-8859-1");
}
catch (UnsupportedEncodingException ex) {
ex.printStackTrace();
}
return result;
}
/**
* Utf8URL编码
* @param s
* @return
*/
public String Utf8URLencode(String text) {
StringBuffer result = new StringBuffer();
for (int i = 0; i < text.length(); i++) {
char c = text.charAt(i);
if (c >= 0 && c <= 255) {
result.append(c);
}else {
byte[] b = new byte[0];
try {
b = Character.toString(c).getBytes("UTF-8");
}catch (Exception ex) {
}
for (int j = 0; j < b.length; j++) {
int k = b[j];
if (k < 0) k += 256;
result.append("%" + Integer.toHexString(k).toUpperCase());
}
}
}
return result.toString();
}
/**
* Utf8URL解码
* @param text
* @return
*/
public String Utf8URLdecode(String text) {
String result = "";
int p = 0;
if (text!=null && text.length()>0){
text = text.toLowerCase();
p = text.indexOf("%e");
if (p == -1) return text;
while (p != -1) {
result += text.substring(0, p);
text = text.substring(p, text.length());
if (text == "" || text.length() < 9) return result;
result += CodeToWord(text.substring(0, 9));
text = text.substring(9, text.length());
p = text.indexOf("%e");
}
}
return result + text;
}
/**
* utf8URL编码转字符
* @param text
* @return
*/
private String CodeToWord(String text) {
String result;
if (Utf8codeCheck(text)) {
byte[] code = new byte[3];
code[0] = (byte) (Integer.parseInt(text.substring(1, 3), 16) - 256);
code[1] = (byte) (Integer.parseInt(text.substring(4, 6), 16) - 256);
code[2] = (byte) (Integer.parseInt(text.substring(7, 9), 16) - 256);
try {
result = new String(code, "UTF-8");
}catch (UnsupportedEncodingException ex) {
result = null;
}
}
else {
result = text;
}
return result;
}
/**
* 编码是否有效
* @param text
* @return
*/
private boolean Utf8codeCheck(String text){
String sign = "";
if (text.startsWith("%e"))
for (int i = 0, p = 0; p != -1; i++) {
p = text.indexOf("%", p);
if (p != -1)
p++;
sign += p;
}
return sign.equals("147-1");
}
/**
* 是否Utf8Url编码
* @param text
* @return
*/
public boolean isUtf8Url(String text) {
text = text.toLowerCase();
int p = text.indexOf("%");
if (p != -1 && text.length() - p > 9) {
text = text.substring(p, p + 9);
}
return Utf8codeCheck(text);
}
/**
* 测试
* @param args
*/
public static void main(String[] args) {
CharTools charTools = new CharTools();
String url;
url = "1%E5%BA%A71%E5%8F%B7%E7%B3%BB%E7%BB%9F1%E5%8F%B7%E7%A9%BA%E8%B0%83%E6%9C%BA%E7%BB%84%E5%8F%82%E6%95%B0%E6%8A%84%E8%A1%A8%E6%97%A5%E6%8A%A5%E8%A1%A8";
if(charTools.isUtf8Url(url)){
System.out.println(charTools.Utf8URLdecode(url));
}else{
System.out.println(URLDecoder.decode(url));
}
url = "http://www.baidu.com/baidu?word=%D6%D0%B9%FA%B4%F3%B0%D9%BF%C6%D4%DA%CF%DF%C8%AB%CE%C4%BC%EC%CB%F7&tn=myie2dg";
if(charTools.isUtf8Url(url)){
System.out.println(charTools.Utf8URLdecode(url));
}else{
System.out.println(URLDecoder.decode(url));
}
}
}
