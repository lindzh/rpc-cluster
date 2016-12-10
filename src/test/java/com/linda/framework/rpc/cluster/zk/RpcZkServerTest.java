package com.linda.framework.rpc.cluster.zk;

import com.linda.framework.rpc.cluster.HelloRpcService;
import com.linda.framework.rpc.cluster.HelloRpcServiceImpl;
import com.linda.framework.rpc.cluster.HelloRpcTestService;
import com.linda.framework.rpc.cluster.HelloRpcTestServiceImpl;
import com.linda.framework.rpc.cluster.LoginRpcService;
import com.linda.framework.rpc.cluster.LoginRpcServiceImpl;

public class RpcZkServerTest {
	
	public static void main(String[] args) {
		ZkRpcServer rpcServer = new ZkRpcServer();
		rpcServer.setConnectString("127.0.0.1:2181");
		rpcServer.setNamespace("myrpc");
		rpcServer.setHost("127.0.0.1");
		rpcServer.setPort(3335);
		rpcServer.setApplication("myapp");
		rpcServer.setValidateToken(true);
		rpcServer.register(HelloRpcService.class, new HelloRpcServiceImpl(),null,"hello");
		rpcServer.register(LoginRpcService.class, new LoginRpcServiceImpl(),null,"hello");
		rpcServer.register(HelloRpcTestService.class, new HelloRpcTestServiceImpl(),null,"hello");
		rpcServer.startService();
		System.out.println("--------started----------");
	}

}
