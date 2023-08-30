package com.zhss.dfs.datanode.server;

import com.zhss.dfs.namenode.rpc.model.HeartbeatRequest;import com.zhss.dfs.namenode.rpc.model.HeartbeatResponse;import com.zhss.dfs.namenode.rpc.model.RegisterRequest;import com.zhss.dfs.namenode.rpc.model.RegisterResponse;import com.zhss.dfs.namenode.rpc.service.NameNodeServiceGrpc;import io.grpc.ManagedChannel;import io.grpc.netty.NegotiationType;import io.grpc.netty.NettyChannelBuilder;import java.util.concurrent.CountDownLatch;

/**
 * 负责跟一组NameNode中的某一个进行通信的线程组件
 * @author zhonghuashishan
 *
 */
public class NameNodeServiceActor {
	private static final String NAMENODE_HOSTNAME = "localhost";
	private static final Integer NAMENODE_PORT = 50070;
	private final NameNodeServiceGrpc.NameNodeServiceBlockingStub namenode;
	public NameNodeServiceActor() {
		ManagedChannel channel = NettyChannelBuilder.forAddress(NAMENODE_HOSTNAME,NAMENODE_PORT).negotiationType(NegotiationType.PLAINTEXT).build();
		this.namenode = NameNodeServiceGrpc.newBlockingStub(channel);
	}

	/**
	 * 向自己负责通信的那个NameNode进行注册
	 */
	public void register()throws InterruptedException {
		Thread registerThread = new RegisterThread();
		registerThread.start();
		registerThread.join();
	}
	
	/**
	 * 负责注册的线程
	 * @author zhonghuashishan
	 *
	 */
	class RegisterThread extends Thread {

		@Override
		public void run() {
			try {
				// 发送rpc接口调用请求到NameNode去进行注册
				System.out.println("发送RPC请求到NameNode进行注册.......");  
				
				// 在这里进行注册的时候会提供哪些信息过去呢？
				// 比如说当前这台机器的ip地址、hostname，这两个东西假设是写在配置文件里的
				// 我们写代码的时候，主要是在本地来运行和测试，有一些ip和hostname，就直接在代码里写死了
				// 大家后面自己可以留空做一些完善，你可以加一些配置文件读取的代码
				String ip = "127.0.0.1";
				String hostname = "dfs-data-01";
				// 通过RPC接口发送到NameNode他的注册接口上去
				// 发送请求到 namenode server上
				RegisterRequest request = RegisterRequest.newBuilder().setIp(ip)
						.setHostname(hostname).build();
				RegisterResponse registerResponse = namenode.register(request);

				System.out.println("接收以NameNode返回注册响应 "+ registerResponse.getStatus());

				HeartbeatRequest heartbeatRequest = HeartbeatRequest.newBuilder().setIp(ip)
						.setHostname(hostname).build();
				HeartbeatResponse rsponse = namenode.heartbeat(heartbeatRequest);
				System.out.println("接收以NameNode返回心跳响应 "+ rsponse.getStatus());
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
