package com.imooc.hadoop.project;

import com.kumkee.userAgent.UserAgent;
import com.kumkee.userAgent.UserAgentParser;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * UserAgent测试类
 */
public class UserAgentTest {
    public static void main(String[] args) {
        String source="Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36";
        UserAgentParser userAgentParser = new UserAgentParser();
        UserAgent agent = userAgentParser.parse(source);
        String browser = agent.getBrowser();//浏览器
        String engine = agent.getEngine();//引擎
        String engineVersion = agent.getEngineVersion();//引擎版本
        String os = agent.getOs();//操作系统
        String platform = agent.getPlatform();//平台
        boolean mobile = agent.isMobile();//移动
        System.out.println(browser+ ","+engine+ ","+engineVersion+ ","+os+ ","+platform+ ","+mobile);
        //Chrome,Webkit,537.36,Windows 7,Windows,false
    }

    /**
     * 本地测试聚合计算浏览器分类个数
     * @throws Exception
     */
    @Test
    public void testReadFile() throws Exception{
        String path="D:\\12306\\100-access.log";
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(new File(path)))
        );
        String line="";//此字符串不代表null值

        Map<String, Integer> browserMap = new HashMap<String, Integer>();//浏览器类型和出现次数

        UserAgentParser userAgentParser = new UserAgentParser();
        while (line!=null){
            line = reader.readLine();//一次读入一行数据

            if (StringUtils.isNotBlank(line)){//判断这一行是否为空
                String source = line.substring(getCharacterPosition(line, "\"", 7)+1);
                UserAgent agent = userAgentParser.parse(source);
                String browser = agent.getBrowser();//浏览器
                String engine = agent.getEngine();//引擎
                String engineVersion = agent.getEngineVersion();//引擎版本
                String os = agent.getOs();//操作系统
                String platform = agent.getPlatform();//平台
                boolean mobile = agent.isMobile();//移动

                Integer browserValue=browserMap.get(browser);
                if(browserValue!=null){
                    browserMap.put(browser,browserMap.get(browser)+1);
                }else{
                    browserMap.put(browser,1);
                }
                System.out.println(browser+ ","+engine+ ","+engineVersion+ ","+os+ ","+platform+ ","+mobile);
            /*
            部分结果
            Unknown,Unknown,null,Unknown,Android,true
            Unknown,Unknown,null,Linux,Linux,false
            Chrome,Webkit,537.36,Windows 7,Windows,false
             */
            }
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        for(Map.Entry<String, Integer> entry:browserMap.entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
        /*
        Unknown:78
        Chrome:20
        Firefox:2
         */
    }

    /**
     * 测试自定义方法
     */
    @Test
    public void testGetCharacterPosition(){
        String value="117.35.88.11 - - [10/Nov/2016:00:01:02 +0800] \"GET /article/ajaxcourserecommends?id=124 HTTP/1.1\" 200 2345 \"www.imooc.com\" \"http://www.imooc.com/code/1852\" - \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36\" \"-\" 10.100.136.65:80 200 0.616 0.616\n";
        int characterPosition = getCharacterPosition(value, "\"", 7);
        System.out.println(characterPosition);//158
    }

    /**
     * 获取指定字符串中指定标识的字符串出现的索引位置
     * @param value
     * @param operator
     * @param index
     * @return
     */
    private int getCharacterPosition(String value,String operator,int index){
        Matcher matcher = Pattern.compile(operator).matcher(value);
        int mIdx=0;
        while (matcher.find()){
            mIdx++;
            if(mIdx== index){
                break;
            }
        }
        return matcher.start();
    }
}