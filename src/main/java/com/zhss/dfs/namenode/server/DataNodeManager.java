package com.zhss.dfs.namenode.server;

import java.util.ArrayList;import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 这个组件，就是负责管理集群里的所有的datanode的
 * @author zhonghuashishan
 *
 */
public class DataNodeManager {

	/**
	 * 内存中维护的datanode列表
	 */
	private Map<String,DataNodeInfo> datanodes = new ConcurrentHashMap<String, DataNodeInfo>();

	 public DataNodeManager(){
		 new DataNodeAliveMonitor().start();
	 }
	/**
	 * datanode进行注册
	 * @param ip 
	 * @param hostname
	 */
	public Boolean register(String ip, String hostname) {
		DataNodeInfo datanode = new DataNodeInfo(ip, hostname);
		datanodes.put(ip + "-" + hostname, datanode);
		return true;
	}

	/**
	 *  datanode 进行心跳
	 * @param ip
	 * @param hostname
	 * @return
	 */
	public Boolean heartbeat(String ip, String hostname){
		DataNodeInfo datanode = datanodes.get(ip + "-" + hostname);
		if (datanode != null){
			datanode.setLatestHeartbeatTime(System.currentTimeMillis());
		}
		return true;
	}
	class DataNodeAliveMonitor extends Thread{
    @Override
    public void run() {
		try{
			while (true){
				ArrayList<String> toRemoveDatanodes = new ArrayList<String>();
				for (DataNodeInfo datanode: datanodes.values()){
					if ((System.currentTimeMillis() - datanode.getLatestHeartbeatTime()) > 90 * 1000){
                        toRemoveDatanodes.add(datanode.getIp() + "_" + datanode.getHostname());
					}
				}
				if (!toRemoveDatanodes.isEmpty()){
					for (String toRemoveDatanode:toRemoveDatanodes){
						datanodes.remove(toRemoveDatanode);
					}
				}
				Thread.sleep(30 * 1000);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
    }
  }
}
