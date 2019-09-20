package com.imooc.hadoop.spring;


import org.apache.hadoop.fs.FileStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.hadoop.fs.FsShell;

/**
 * 使用SpringBoot的方式访问HDFS
 */

@SpringBootApplication
public class SpringBootHDFSApp implements CommandLineRunner {

    @Autowired
    FsShell fsShell;

    @Override
    public void run(String... strings) throws Exception {

        for (FileStatus fileStatus:fsShell.lsr("/springhdfs")) {
           System.out.println(">"+fileStatus.getPath());
        }
    }

    public static void main(String[] args) {

        SpringApplication.run(SpringBootHDFSApp.class,args);

    }
}
