package com.zhss.dfs.backupnode.server;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.JsonArray;
import com.zhss.dfs.namenode.rpc.model.FetchEditsLogRequest;import com.zhss.dfs.namenode.rpc.model.FetchEditsLogResponse;import com.zhss.dfs.namenode.rpc.service.NameNodeServiceGrpc;import io.grpc.ManagedChannel;import io.grpc.netty.NegotiationType;import io.grpc.netty.NettyChannelBuilder;

public class NameNodeRpcClient {

    private final NameNodeServiceGrpc.NameNodeServiceBlockingStub namenode;
    private static final String NAMENODE_HOSTNAME = "localhost";
    private static final Integer NAMENODE_PORT = 50070;
    public NameNodeRpcClient() {
        ManagedChannel channel = NettyChannelBuilder
                .forAddress(NAMENODE_HOSTNAME,NAMENODE_PORT)
                .negotiationType(NegotiationType.PLAINTEXT).build();
        this.namenode = NameNodeServiceGrpc.newBlockingStub(channel);
    }

    /**
     * 抓取 editslog数据
     * @return
     */
    public JSONArray fetchEditsLog(){
        FetchEditsLogRequest request = FetchEditsLogRequest.newBuilder().setCode(1).build();
        FetchEditsLogResponse response = namenode.fetchEditsLog(request);
        String editsLog = response.getEditsLog();
        return JSONArray.parseArray(editsLog);
    }
}
