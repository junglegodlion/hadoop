package com.imooc.bigdata.hadoop.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;


/**
 * Hadoop HDFS Java API操作
 *
 * 关键点：
 * 1）创建Configuration
 * 2）获取FileSystem
 * 3）。。。就是HDFS API的操作
 */
public class HDFSApp {

    FileSystem fileSystem = null;
    Configuration configuration = null;

    /**
     * HDFS的路径,core-site.xml中配置的端口号
     */
    public static final String HDFS_PATH = "hdfs://192.168.123.100:8020";
    /**
     * 解决无权限访问,设置远程hadoop的linux用户名称
     */
    public static final String USER = "hadoop";

    /**
     * 单元测试之前的准备工作,准备环境,加载配置
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {

        System.out.println("HDFSApp is setUp.....");
        configuration = new Configuration();
        //设置副本系数为1，不设置默认为3
        configuration.set("dfs.replication","1");
        /**
         * 构造一个访问指定HDFS系统的客户端对象
         * 第一个参数：HDFS的URI
         * 第二个参数：客户端指定的配置参数
         * 第三个参数：客户端的身份，说白了就是用户名
         */
        fileSystem = FileSystem.get(new URI(HDFS_PATH), configuration, USER);

    }

    /**
     * 创建目录
     *
     * @throws Exception
     */
    @Test
    public void mkDir() throws Exception {
        fileSystem.mkdirs(new Path("/hdfstest/456"));
    }

    /**
     * 查看HDFS内容
     */
    @Test
    public void text()throws Exception{
        FSDataInputStream in = fileSystem.open(new Path("/hdfstest/456/start-dfs.sh"));

        IOUtils.copyBytes(in,System.out,1024);//System.out输出至控制台
    }

    /**
     * 创建文件
     * @throws Exception
     */
    @Test
    public void create() throws Exception{
        FSDataOutputStream out = fileSystem.create(new Path("/hdfsapi/test/b.txt"));

        out.writeUTF("HELLO JUNGLE");
        out.flush();
        out.close();
    }

    /**
     * 重命名
     * @throws Exception
     */
    @Test
    public void rename() throws Exception{
        Path oldPath = new Path("/hdfsapi/test/b.txt");
        Path newPath = new Path("/hdfsapi/test/c.txt");
        boolean result = fileSystem.rename(oldPath,newPath);
        System.out.println(result);

    }

    /**
     * 拷贝本地文件到HDFS文件系统
     * @throws Exception
     */
    @Test
    public void copyFromLocalFile() throws Exception{
        Path src = new Path("pom.xml");
        Path dst = new Path("/hdfsapi/test");
        fileSystem.copyFromLocalFile(src,dst);
    }

    /**
     * 拷贝本地大文件到HDFS文件系统：带进度
     * @throws Exception
     */
    @Test
    public void copyFromLocalBigFile() throws Exception{
        InputStream in = new BufferedInputStream(new FileInputStream(new File("jdk-8u152-linux-x64.tar.gz")));
        FSDataOutputStream out = fileSystem.create(new Path("/hdfsapi/test/jdk.tgz"),
                new Progressable() {
                    @Override
                    public void progress() {
                        System.out.print(".");
                    }
                });
        IOUtils.copyBytes(in,out,4096);
        Path src = new Path("jdk-8u152-linux-x64.tar.gz");
        Path dst = new Path("/hdfsapi/test/");
        fileSystem.copyFromLocalFile(src,dst);
    }

    /**
     * 拷贝HDFS文件到本地：下载
     * @throws Exception
     */
    @Test
    public void copyToLocalFile() throws Exception{
        Path src = new Path("/hdfstest/456/start-dfs.sh");
        Path dst = new Path("D:/10小时入门大数据/源码/b1qv7n/hadoop-train-v2");
        //第一个参数是是否删除掉源目录
        //最后一个参数是是否使用本地文件系统，改用java的io流
        fileSystem.copyToLocalFile(false,src,dst,true);
    }

    /**
     * 查看目标文件夹下的所有文件
     * @throws Exception
     */
    @Test
    public void listFiles() throws Exception{
        FileStatus[] statuses = fileSystem.listStatus(new Path("/hdfsapi/test"));
        for (FileStatus file:statuses) {
            String isDir = file.isDirectory()?"文件夹":"文件";
            String permission = file.getOwner().toString();
            short replication = file.getReplication();
            long length = file.getLen();
            String path = file.getPath().toString();
            System.out.println(isDir+"\t"+permission+"\t"+replication+"\t"+length+"\t"+path);
        }
    }

    /**
     * 递归查看目标文件夹下的所有文件
     * @throws Exception
     */
    @Test
    public void listFilesRecursive() throws Exception{
        RemoteIterator<LocatedFileStatus> files = fileSystem.listFiles(new Path("/hdfsapi/test"),true);
        while (files.hasNext()){
            LocatedFileStatus file = files.next();
            String isDir = file.isDirectory()?"文件夹":"文件";
            String permission = file.getOwner().toString();
            short replication = file.getReplication();
            long length = file.getLen();
            String path = file.getPath().toString();
            System.out.println(isDir+"\t"+permission+"\t"+replication+"\t"+length+"\t"+path);
        }

    }
    /**
     * 查看文件块信息
     *
     */
    @Test
    public void getFileBlockLocations() throws Exception{
        FileStatus fileStatus = fileSystem.getFileStatus(new Path("/hdfsapi/test/jdk.tgz"));
        BlockLocation[] blocks = fileSystem.getFileBlockLocations(fileStatus,0,fileStatus.getLen());
        for (BlockLocation block:blocks) {
            for (String name:block.getNames()){
                System.out.println(name+":"+block.getOffset()+":"+block.getLength());
            }
            
        }
    }

    @Test
    public void delete() throws Exception{
        boolean result = fileSystem.delete(new Path("/hdfsapi/test/jdk.tgz"),true);

        System.out.println(result);
    }

    /**
     * 单元测试之后的工作,清理环境,释放资源等等
     *
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        configuration = null;
        fileSystem = null;
        System.out.println("HDFSApp is tearDown....");
    }




//    public static void main(String[] args) throws Exception {
//        Configuration configuration = new Configuration();
//        FileSystem fileSystem = FileSystem.get(new URI("hdfs://ecust-Lenovo.lan:8020"),configuration,"hadoop");
//
//        Path path = new Path("/hdfsjungle/123/2/3");
//        boolean result = fileSystem.mkdirs(path);
//        System.out.println(result);
//
//    }

}