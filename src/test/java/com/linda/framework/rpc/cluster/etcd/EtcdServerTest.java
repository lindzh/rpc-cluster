package com.linda.framework.rpc.cluster.etcd;

import com.linda.framework.rpc.cluster.HelloRpcService;
import com.linda.framework.rpc.cluster.HelloRpcServiceImpl;
import com.linda.framework.rpc.cluster.HelloRpcTestService;
import com.linda.framework.rpc.cluster.HelloRpcTestServiceImpl;
import com.linda.framework.rpc.cluster.LoginRpcService;
import com.linda.framework.rpc.cluster.LoginRpcServiceImpl;
import com.linda.framework.rpc.cluster.serializer.ProtostuffSerializer;
import com.linda.framework.rpc.cluster.serializer.simple.SimpleSerializer;

public class EtcdServerTest {
	
	public static void main(String[] args) {
		
		EtcdRpcServer rpcServer = new EtcdRpcServer();
		rpcServer.setEtcdUrl("http://192.168.139.128:2911");
		rpcServer.setNamespace("myapp");
		rpcServer.setApplication("simple");
		rpcServer.setSerializer(new SimpleSerializer());
		rpcServer.setPort(3354);
		rpcServer.register(HelloRpcService.class, new HelloRpcServiceImpl());
		rpcServer.register(LoginRpcService.class, new LoginRpcServiceImpl());
		rpcServer.register(HelloRpcTestService.class, new HelloRpcTestServiceImpl());
		rpcServer.startService();
		System.out.println("--------etcd client start---------");
	}

}
