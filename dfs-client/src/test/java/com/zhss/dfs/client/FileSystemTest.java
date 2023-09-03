package com.zhss.dfs.client;

public class FileSystemTest {
    public static void main(String[] args)throws Exception{
        FileSystemImpl fileSystem = new FileSystemImpl();

       for(int j = 0; j < 10; j++) {
           new Thread(){
               @Override public void run() {
                   try{
                       for(int i = 0; i < 200; i++) {
                           fileSystem.mkdir("/usr/warehouse/hive" +i);
                           fileSystem.mkdir("/usr/warehouse/spark"+i);
                           fileSystem.mkdir("/usr/log/access"+i);
                           fileSystem.mkdir("/data/old"+i);
                       }
                   }catch (Exception e){
                       e.printStackTrace();
                   }
    }}.start();
       }
    }
}
