package com.zhss.dfs.backupnode.server;

import java.util.concurrent.TimeUnit;
/** 启动类 */
public class BackupNode {
  private volatile Boolean isRunning = true;

  public static void main(String[] args) {
    BackupNode backupNode = new BackupNode();
    backupNode.start();
  }

  public void start() {
    EditLogFetcher editLogFetcher = new EditLogFetcher(this);
    editLogFetcher.start();
  }

  public void run()throws InterruptedException {
      while (isRunning) {
          TimeUnit.SECONDS.sleep(1);
      }
  }

  public Boolean isRunning(){
      return isRunning;
  }
}
