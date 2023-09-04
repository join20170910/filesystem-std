package com.zhss.dfs.backupnode.server;

import com.alibaba.fastjson.JSONArray;import java.util.concurrent.TimeUnit;
/** 从 namenode同步editslog组件 */
public class EditLogFetcher extends Thread {
private BackupNode backupNode;
private NameNodeRpcClient nameNode;
    public EditLogFetcher(BackupNode backupNode){
     this.backupNode = backupNode;
     this.nameNode = new NameNodeRpcClient();
    }
  @Override
  public void run() {
    while (backupNode.isRunning()) {
        JSONArray jsonArray = nameNode.fetchEditsLog();
    }
  }
}
