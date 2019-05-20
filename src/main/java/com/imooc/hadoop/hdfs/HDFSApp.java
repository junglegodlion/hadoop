package com.imooc.hadoop.hdfs;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.URI;


/**
 * Hadoop HDFS Java API操作
 */
public class HDFSApp {

    public static final String HDFS_PATH = "hdfs://192.168.123.101:8020";
    FileSystem fileSystem = null;
    Configuration configuration = null;

    /**
     * 创建目录
     * @throws Exception
     */
    @Test
    public void mkdir() throws Exception{
        fileSystem.mkdirs(new Path("/hdfsapi/test"));
    }

    /**
     * 创建文件
     * @throws Exception
     */
    @Test
    public void create() throws Exception{
        FSDataOutputStream output = fileSystem.create(new Path("/hdfsapi/test/c.txt"));
        output.write("hello hadoop".getBytes());
        output.flush();
        output.close();
    }

    /**
     * 查看HDFS文件内容
     * @throws Exception
     */
    @Test
    public void cat()throws Exception{
       FSDataInputStream in = fileSystem.open(new Path("/hdfsapi/test/a.txt"));
       IOUtils.copyBytes(in,System.out,1024);//System.out输出至控制台
        in.close();

    }

    /**
     * 重命名
     * @throws Exception
     */
    @Test
    public void rename() throws Exception{
      Path oldPath = new Path("/hdfsapi/test/a.txt");
      Path newPath = new Path("/hdfsapi/test/b.txt");
      fileSystem.rename(oldPath,newPath);
    }

    /**
     * 上传文件到HDFS
     * @throws Exception
     */
    @Test
    public void copyFromLocalFile() throws Exception{
        Path localPath = new Path("D:/10小时入门大数据/test222.txt");
        Path hdfsPath = new Path("/hdfsapi/test");
        fileSystem.copyFromLocalFile(localPath,hdfsPath);
    }

    /**
     * 上传da文件到HDFS
     * @throws Exception
     */
    @Test
    public void copyFromLocalFileWithProgress() throws Exception{
        Path localPath = new Path("D:/10小时入门大数据/test222.txt");
        Path hdfsPath = new Path("/hdfsapi/test");
        fileSystem.copyFromLocalFile(localPath,hdfsPath);
        InputStream in = new BufferedInputStream(
                new FileInputStream(
                       (new File("D:/10小时入门大数据/第2章 初识Hadoop/第2章 初识Hadoop.mp4"))));
        FSDataOutputStream output = fileSystem.create(new Path("/hdfsapi/test/a.mp4"),
                new Progressable() {
                    @Override
                    public void progress() {
                        System.out.println(".");//带进度提醒信息
                    }
                });
        IOUtils.copyBytes(in,output,18233344 );
    }

    /**
     * 下载HDFS文件
     * @throws Exception
     */
    @Test
    public void copyToLocalFile() throws Exception{
        String dest = HDFS_PATH+"/hdfsapi/test/b.txt";
        String local = "D:/10小时入门大数据/x.txt";
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(dest),conf);
        FSDataInputStream fsdi = fs.open(new Path(dest));
        OutputStream output = new FileOutputStream(local);
        IOUtils.copyBytes(fsdi,output,4096,true);

        /*Path localPath = new Path("D:/10小时入门大数据/x.txt");
        Path hdfsPath = new Path("/hdfsapi/test/b.txt");
        fileSystem.copyToLocalFile(hdfsPath,localPath);*/

    }


    /**
     * 查看某个目录下的所有文件
     * @throws Exception
     */
    @Test
    public void listFiles() throws Exception{

        FileStatus[] fileStatuses = fileSystem.listStatus(new Path("/hdfsapi/test"));
        for (FileStatus fileStatus:fileStatuses){
            String isDir = fileStatus.isDirectory()?"文件夹":"文件";
            short replication = fileStatus.getReplication();
            long len = fileStatus.getLen();
            String path = fileStatus.getPath().toString();
            System.out.println(isDir+"\t"+replication+"\t"+len+"\t"+path);
        }
    }

    @Test
    public void delete() throws Exception{
        fileSystem.delete(new Path("/hdfsapi/test"),true);
    }
    @Before
    public void setUp() throws Exception{
        System.out.println("HDFSApp.setUp");
        configuration = new Configuration();
        fileSystem = FileSystem.get(new URI(HDFS_PATH),configuration,"root");
    }

    @After
    public void tearDown() throws Exception {
        configuration = null;
        fileSystem = null;
        System.out.println("HDFSApp.tearDown");
    }}