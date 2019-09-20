package com.imooc.hadoop.spring;


import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 使用Spring Hadoop来访问HDFS文件系统
 */
public class SpringHadoopHDFSApp {
    private ApplicationContext ctx;
    private FileSystem fileSystem;

    /**
     * 创建HDFS文件夹
     * @throws Exception
     */
    @Test
    public void testMkdirs() throws Exception{
        fileSystem.mkdirs(new Path("/springhdfs"));
    }

    /**
     * 读取HDFS文件内容
     * @throws Exception
     */
    @Test
    public void testText() throws Exception{
        FSDataInputStream in = fileSystem.open(new Path("/springhdfs/hello.txt"));
        IOUtils.copyBytes(in,System.out,1024);//System.out输出至控制台
        in.close();
    }



    @Before
    public void setUP(){
        ctx = new ClassPathXmlApplicationContext("beans.xml");

        fileSystem= (FileSystem)ctx.getBean("fileSystem");
    }
    @After
    public void tearDown() throws Exception{
        ctx=null;
        fileSystem.close();
    }


}
