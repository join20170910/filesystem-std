package com.zhss.dfs.namenode.server;

import com.zhss.dfs.namenode.rpc.service.NameNodeServiceGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class NameNodeRpcServer {
    /**
     * 负责管理元数据的核心组件
     */
    private FSNamesystem namesystem;
    /**
     * 负责管理集群中所有的datanode的组件
     */
    private DataNodeManager datanodeManager;

    private static final int DEFAULT_PORT =50070;
    private Server server = null;
    public NameNodeRpcServer(FSNamesystem namesystem, DataNodeManager datanodeManager) {
                this.namesystem=namesystem;
                this.datanodeManager=datanodeManager;
}

    /**
     * 启动 一个 rpc server 监听指定的端口号，绑定开发的接口
     * @throws IOException
     */
    public void start() throws IOException{

    this.server =
        ServerBuilder.forPort(DEFAULT_PORT)
            .addService(NameNodeServiceGrpc.bindService(new NameNodeServiceImpl(namesystem, datanodeManager)))
            .build()
            .start();
    System.out.println("Server started,listening on " + DEFAULT_PORT);
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread() {
              @Override
              public void run() {
                System.out.println("*** shutting down gRPC server since JVM is shutting down");
                NameNodeRpcServer.this.stop();
                System.out.println("*** server shut down");
              }
            });
    }
    public void stop() {
        if (server != null){
            server.shutdown();
        }
}
    public void blockUntilShutdown()throws InterruptedException{
        if (server != null){
            server.awaitTermination();
        }
}

}
