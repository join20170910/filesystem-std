package com.zhss.dfs.client;

import com.zhss.dfs.namenode.rpc.model.*;
import com.zhss.dfs.namenode.rpc.service.NameNodeServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
/**
 * 文件系统客户端的实现类
 */
public class FileSystemImpl implements FileSystem {

    private final NameNodeServiceGrpc.NameNodeServiceBlockingStub namenode;
    private static final String NAMENODE_HOSTNAME = "localhost";
    private static final Integer NAMENODE_PORT = 50070;

    public FileSystemImpl() {
        ManagedChannel channel = NettyChannelBuilder
                .forAddress(NAMENODE_HOSTNAME,NAMENODE_PORT)
                .negotiationType(NegotiationType.PLAINTEXT).build();
        this.namenode = NameNodeServiceGrpc.newBlockingStub(channel);
} /**
     * 目录创建
     * @param path
     * @throws Exception
     */
    @Override
  public void mkdir(String path) throws Exception {

        MkdirRequest request = MkdirRequest.newBuilder()
                .setPath(path)
                .build();
        MkdirResponse response = namenode.mkdir(request);
    System.out.println("创建目录的响应:status = " + response.getStatus());

    }

    /**
     * 优雅关闭
     * @throws Exception
     */
    @Override public void shutdown()throws Exception {

        ShutdownRequest request = ShutdownRequest.newBuilder()
            .setCode(1)
            .build();
        ShutdownResponse response = namenode.shutdown(request);
    System.out.println("优雅关闭的响应:status = " + response.getStatus());

}}
