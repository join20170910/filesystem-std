package com.zhss.dfs.namenode.server;

import com.alibaba.fastjson.JSONArray;import com.alibaba.fastjson.JSONObject;import com.zhss.dfs.namenode.rpc.model.*;
import com.zhss.dfs.namenode.rpc.service.NameNodeServiceGrpc;
import io.grpc.stub.StreamObserver;import java.util.List;

/**
 * NameNode的rpc服务的接口
 *
 * @author zhonghuashishan
 */
public class NameNodeServiceImpl implements NameNodeServiceGrpc.NameNodeService {

  public static final Integer STATUS_SUCCESS = 1;
  public static final Integer STATUS_FAILURE = 2;
  public static final Integer STATUS_SHUTDOWN = 3;
  public static final Integer BACKUP_NODE_FETCH_SIZE =10;

  /** 负责管理元数据的核心组件
   * 它是一个逻辑上的组件,主要是负责管理元数据的更新
   * 比如说你要更新内存内的文件目录树
   * */

  private FSNamesystem namesystem;
  /** 负责管理集群中所有的datanode的组件 */
  private DataNodeManager datanodeManager;
  // 是否还在运行
  private volatile Boolean isRunning = true;

  /**
   * 当前 backupNode节点同步到了哪一条txid
   */
  private long backupSyncTxid = 0L;
  /** 当前缓冲区的editslog */
  private JSONArray currentBufferedEditsLog = new JSONArray();

  public NameNodeServiceImpl(FSNamesystem namesystem, DataNodeManager datanodeManager) {
    this.namesystem = namesystem;
    this.datanodeManager = datanodeManager;
  }

  /**
   * 创建目录
   *
   * @param path 目录路径
   * @return 是否创建成功
   * @throws Exception
   */
  public Boolean mkdir(String path) throws Exception {
    return this.namesystem.mkdir(path);
  }

  /** 启动这个rpc server */
  public void start() {
    System.out.println("开始监听指定的rpc server的端口号，来接收请求");
  }

  /**
   * DataNode进行注册
   *
   * @param request
   * @param responseObserver
   * @return
   */
  @Override
  public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
    datanodeManager.register(request.getIp(), request.getHostname());
    RegisterResponse response = RegisterResponse.newBuilder().setStatus(STATUS_SUCCESS).build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void heartbeat(
      HeartbeatRequest request, StreamObserver<HeartbeatResponse> responseObserver) {
    datanodeManager.heartbeat(request.getIp(), request.getHostname());
    HeartbeatResponse response = HeartbeatResponse.newBuilder().setStatus(STATUS_SUCCESS).build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  /**
   * 创建文件 及文件夹
   *
   * @param request
   * @param responseObserver
   */
  @Override
  public void mkdir(MkdirRequest request, StreamObserver<MkdirResponse> responseObserver) {
    try {
      MkdirResponse response = null;
      if (!isRunning){
         response = MkdirResponse.newBuilder().setStatus(STATUS_SHUTDOWN).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
      }else {
        this.namesystem.mkdir(request.getPath());
        response = MkdirResponse.newBuilder().setStatus(STATUS_SUCCESS).build();
      }
		responseObserver.onNext(response);
		responseObserver.onCompleted();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 优雅关闭
   * @param request
   * @param responseObserver
   */
  @Override public void shutdown(ShutdownRequest request, StreamObserver<ShutdownResponse> responseObserver) {
    try {
      this.isRunning = false;
      this.namesystem.flush();
      ShutdownResponse response = ShutdownResponse.newBuilder().setStatus(STATUS_SUCCESS).build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      e.printStackTrace();
    }
    }
    @Override public void fetchEditsLog(FetchEditsLogRequest request, StreamObserver<FetchEditsLogResponse> responseObserver) {
      FetchEditsLogResponse response = null;
      JSONArray fetchedEditsLog = new JSONArray();
        List<String> flushedTxIds = this.namesystem.getEditLog().getFlushedTxIds();
           if (flushedTxIds.size() == 0){
             if (backupSyncTxid != 0){
               currentBufferedEditsLog.clear();
               int fetchCount = 0;
               for(int i = 0; i < currentBufferedEditsLog.size() ; i++) {
               if (currentBufferedEditsLog.getJSONObject(i).getLong("txid") > backupSyncTxid){
                  fetchedEditsLog.add(currentBufferedEditsLog.getJSONObject(i));
                 backupSyncTxid = currentBufferedEditsLog.getJSONObject(i).getLong("txid");
                 fetchCount ++ ;

               }
               if (fetchCount == BACKUP_NODE_FETCH_SIZE){
                 break;
               }
               }
             }else {

               // 此时数据全部都存在于内存缓冲里
               String[] bufferedEditsLog = namesystem.getEditLog().getBufferedEditsLog();
               for(String editsLog :bufferedEditsLog) {
                 currentBufferedEditsLog.add(JSONObject.parseObject(editsLog));
               }

               //此时就可以从刚才内存缓冲区里的数据开始取数据了
               int fetchSize = Math.min(BACKUP_NODE_FETCH_SIZE,currentBufferedEditsLog.size());
               for(int i = 0; i < fetchSize; i++) {
                 fetchedEditsLog.add(currentBufferedEditsLog.getJSONObject(i));
                 backupSyncTxid = currentBufferedEditsLog.getJSONObject(i).getLong("txid");

               }
               response = FetchEditsLogResponse.newBuilder()
                       .setEditsLog(fetchedEditsLog.toJSONString()).build();
               responseObserver.onNext(response);
               responseObserver.onCompleted();
             }
           }
    }}
