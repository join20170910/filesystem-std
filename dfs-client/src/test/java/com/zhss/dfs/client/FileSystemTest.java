package com.zhss.dfs.client;

public class FileSystemTest {
    public static void main(String[] args)throws Exception{
        FileSystemImpl fileSystem = new FileSystemImpl();
        fileSystem.mkdir("/usr/warehouse/hive");
        fileSystem.mkdir("/usr/warehouse/spark");
        fileSystem.mkdir("/usr/log/access");
        fileSystem.mkdir("/data/old");
    }
}
