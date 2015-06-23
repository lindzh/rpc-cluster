package com.linda.framework.rpc.cluster.etcd;

import com.linda.framework.rpc.cluster.HelloRpcService;
import com.linda.framework.rpc.cluster.HelloRpcServiceImpl;
import com.linda.framework.rpc.cluster.HelloRpcTestService;
import com.linda.framework.rpc.cluster.HelloRpcTestServiceImpl;
import com.linda.framework.rpc.cluster.LoginRpcService;
import com.linda.framework.rpc.cluster.LoginRpcServiceImpl;

public class EtcdServerTest {
	
	public static void main(String[] args) {
		
		EtcdRpcServer rpcServer = new EtcdRpcServer();
		rpcServer.setEtcdUrl("http://192.168.139.129:2911");
		rpcServer.setNamespace("lindezhi");
		rpcServer.setHost("127.0.0.1");
		rpcServer.setPort(3333);
		rpcServer.register(HelloRpcService.class, new HelloRpcServiceImpl());
		rpcServer.register(LoginRpcService.class, new LoginRpcServiceImpl());
		rpcServer.register(HelloRpcTestService.class, new HelloRpcTestServiceImpl());
		rpcServer.startService();
		System.out.println("--------etcd client start---------");
	}

}
